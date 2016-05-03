package com.tesnik.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

import scala.util.{Failure, Success, Try}

class KafkaDStreamSink(producer: LazyKafkaProducer) {

  def write(ssc: StreamingContext, topic: String, stream: DStream[KafkaPayload]): Unit = {
    val topicVar = ssc.sparkContext.broadcast(topic)
    val producerVar = ssc.sparkContext.broadcast(producer)

    val successCounter = ssc.sparkContext.accumulator(0L, "Success counter")
    val failureCounter = ssc.sparkContext.accumulator(0L, "Failure counter")

    // cache to speed-up processing if action fails
    stream.persist(StorageLevel.MEMORY_ONLY_SER)

    stream.foreachRDD { rdd =>
      // TODO: report counters somewhere

      rdd.foreach { record =>
        val topic = topicVar.value
        val producer = producerVar.value.producer

        val future = producer.send(new ProducerRecord(topic, record.value))

        Try(future.get) match {
          case Failure(ex) =>
            failureCounter += 1
            // TODO: how errors in action are handled by spark
            throw ex
          case Success(_) =>
            successCounter += 1
        }
      }
    }
  }

}

object KafkaDStreamSink {
  def apply(config: Map[String, String]): KafkaDStreamSink = {

    val KEY_SERIALIZER = "org.apache.kafka.common.serialization.ByteArraySerializer"
    val VALUE_SERIALIZER = "org.apache.kafka.common.serialization.ByteArraySerializer"

    val defaultConfig = Map(
      "key.serializer" -> KEY_SERIALIZER,
      "value.serializer" -> VALUE_SERIALIZER
    )

    val producer = new LazyKafkaProducer(defaultConfig ++ config)

    new KafkaDStreamSink(producer)
  }
}

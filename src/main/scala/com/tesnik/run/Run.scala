package com.tesnik.run

import com.tesnik.example.SampleEventConsumer
import com.tesnik.kafka.KafkaDStreamSource
import com.tesnik.spark.streaming.{StringKafkaPayloadCodecConfig, SparkStreamingApplicationConfig, SparkApplicationConfig, StringKafkaPayloadCodec}
import com.typesafe.config.ConfigFactory
import consumer.{EventConsumerJob, EventConsumerJobConfig}

import scala.concurrent.duration.FiniteDuration

/**
  * Created by tiwariaa on 5/2/2016.
  */
object Run {
  import com.typesafe.config.{Config, ConfigFactory}
  import net.ceedubs.ficus.Ficus._
  import net.ceedubs.ficus.readers.ArbitraryTypeReader.arbitraryTypeValueReader

  def main(args: Array[String]): Unit = {
    val appConfig = ConfigFactory.load.getConfig("EquipmentJob")
    val config = new EventConsumerJobConfig(
      appConfig.as[String]("topic.consumeFrom"),
      appConfig.as[FiniteDuration]("windowDuration"),
      appConfig.as[FiniteDuration]("slideDuration"),
      appConfig.as[SparkApplicationConfig]("spark"),
      appConfig.as[SparkStreamingApplicationConfig]("sparkStreaming"),
      appConfig.as[StringKafkaPayloadCodecConfig]("stringCodec"),
      appConfig.as[Map[String, String]]("kafkaSource"))

    val source = KafkaDStreamSource(config.sourceKafka)

    val codec = StringKafkaPayloadCodec(config.stringCodec)

    val streamingJob = new EventConsumerJob(config, source, codec, new SampleEventConsumer)
    streamingJob.start()
  }

}

package com.tesnik.kafka

import org.apache.kafka.clients.producer.KafkaProducer

import scala.collection.JavaConversions._

// TODO: close producer gracefully (shutdown hook on spark executor?)
class LazyKafkaProducer(config: Map[String, Object]) extends Serializable {

  @transient
  lazy val producer: KafkaProducer[Array[Byte], Array[Byte]] = new KafkaProducer(config)

}

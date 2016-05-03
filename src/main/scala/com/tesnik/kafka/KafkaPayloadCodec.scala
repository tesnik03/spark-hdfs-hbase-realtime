package com.tesnik.kafka

import org.apache.spark.streaming.StreamingContext

import scala.util.Try

trait KafkaPayloadCodec[V] extends Serializable {

  def decoder(ssc: StreamingContext): KafkaPayload => Try[V]

  def encoder(ssc: StreamingContext): V => Try[KafkaPayload]

}

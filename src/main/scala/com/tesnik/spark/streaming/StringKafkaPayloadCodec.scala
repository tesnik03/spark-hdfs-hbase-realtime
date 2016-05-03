package com.tesnik.spark.streaming

import com.tesnik.kafka.{KafkaPayload, KafkaPayloadCodec}
import org.apache.spark.streaming.StreamingContext

import scala.util.Try

class StringKafkaPayloadCodec(config: StringKafkaPayloadCodecConfig) extends KafkaPayloadCodec[String] {

  override def decoder(ssc: StreamingContext): KafkaPayload => Try[String] = {
    payload => decode(payload.value)
  }

  override def encoder(ssc: StreamingContext): String => Try[KafkaPayload] = {
    value => Try {
      KafkaPayload(encode(value))
    }
  }

  private def decode(bytes: Array[Byte]): Try[String] = Try {
    new String(bytes, config.encoding)
  }

  private def encode(s: String): Array[Byte] = s.getBytes(config.encoding)

}

object StringKafkaPayloadCodec {
  def apply(config: StringKafkaPayloadCodecConfig): StringKafkaPayloadCodec = new StringKafkaPayloadCodec(config)
}

case class StringKafkaPayloadCodecConfig(encoding: String) extends Serializable


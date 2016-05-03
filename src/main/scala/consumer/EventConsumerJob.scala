package consumer

import com.tesnik.example.SampleEventConsumer
import com.tesnik.hbase.DynamicUpsertDML
import com.tesnik.hdfs.LoadMessagesFromMemory
import com.tesnik.kafka.KafkaDStreamSource
import com.tesnik.spark.streaming._

import scala.concurrent.duration.FiniteDuration

/**
  * Created by tiwariaa on 9/11/15.
  */

case class EventConsumerJobConfig(
                                   topic: String,
                                   windowDuration: FiniteDuration,
                                   slideDuration: FiniteDuration,
                                   spark: SparkApplicationConfig,
                                   sparkStreaming: SparkStreamingApplicationConfig,
                                   stringCodec: StringKafkaPayloadCodecConfig,
                                   sourceKafka: Map[String, String]) extends Serializable

class EventConsumerJob(
                        config: EventConsumerJobConfig,
                        source: KafkaDStreamSource,
                        codec: StringKafkaPayloadCodec,
                        consumable: ConsumableMessages)
  extends SparkStreamingApplication {

  override def sparkConfig: SparkApplicationConfig = config.spark

  override def sparkStreamingConfig: SparkStreamingApplicationConfig = config.sparkStreaming

  def start(): Unit = {
    withSparkStreamingContext { (sc, ssc) =>

      val topic = source.createSource(ssc, config.topic)

      val events = topic.map(codec.decoder(ssc)).flatMap(_.toOption)
      val outputMessages = events.map(event => consumable.inputMessage(event))
      outputMessages.foreachRDD(output => {
        output.foreach(hbaseMessage => new DynamicUpsertDML().getPreparedStatement(hbaseMessage.getHBaseConsumableMessages()))
        output.foreach(mapOutput =>
          //for each map output
          mapOutput.getHDFSMessages().foreach(message => {
            new LoadMessagesFromMemory(message._1, message._2).flushMessages()
          })
        )
      })
    }

  }
}

object EventConsumerJob {

  def main(args: Array[String]): Unit = {
    val config = EventConsumerJobConfig()

    val source = KafkaDStreamSource(config.sourceKafka)

    val codec = StringKafkaPayloadCodec(config.stringCodec)

    val streamingJob = new EventConsumerJob(config, source, codec, new SampleEventConsumer)
    streamingJob.start()
  }

}


object EventConsumerJobConfig {

  import com.typesafe.config.{Config, ConfigFactory}
  import net.ceedubs.ficus.Ficus._
  import net.ceedubs.ficus.readers.ArbitraryTypeReader.arbitraryTypeValueReader

  def apply(): EventConsumerJobConfig = apply(ConfigFactory.load)

  def apply(applicationConfig: Config): EventConsumerJobConfig = {

    val config = applicationConfig.getConfig("EquipmentJob")

    new EventConsumerJobConfig(
      config.as[String]("topic.consumeFrom"),
      config.as[FiniteDuration]("windowDuration"),
      config.as[FiniteDuration]("slideDuration"),
      config.as[SparkApplicationConfig]("spark"),
      config.as[SparkStreamingApplicationConfig]("sparkStreaming"),
      config.as[StringKafkaPayloadCodecConfig]("stringCodec"),
      config.as[Map[String, String]]("kafkaSource")
    )
  }

}


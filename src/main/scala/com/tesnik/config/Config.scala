package com.tesnik.config

/**
  * Created by tiwariaa on 4/30/2016.
  */
import util.Try
import com.typesafe.config.ConfigFactory

trait Config {

  val config = ConfigFactory.load()

  //Server config
  lazy val serviceHost = Try(config.getString("consumableEvents.service.host")).getOrElse("0.0.0.0")
  lazy val servicePort = Try(config.getInt("service.port")).getOrElse(8091)

  //Spark
  lazy val sparkAppName = Try(config.getString("spark.appName")).getOrElse("sparkConsumer")
  lazy val sparkMaster = Try(config.getString("spark.master")).getOrElse("localhost:7077")
  lazy val sparkExeMem = Try(config.getString("spark.executorMemory")).getOrElse("10g")
  lazy val sparkCoresMax = Try(config.getString("spark.coresMax")).getOrElse("6")
  lazy val sparkDefaultParallelism = Try(config.getString("spark.defaultParallelism")).getOrElse("60")

  lazy val hbaseHostName = Try(config.getString("hbase.hostname")).getOrElse("127.0.0.1")
  lazy val hbaseHostPort = Try(config.getString("hbase.port")).getOrElse(60000)

  lazy val numberOfMessagesThreshold = Try(config.getString("message.count")).getOrElse(10)
  lazy val lineSeparator = Try(config.getString("separator.line")).getOrElse("\n")

}
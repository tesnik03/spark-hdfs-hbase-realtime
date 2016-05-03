package com.tesnik.spark.streaming

import org.apache.spark.{SparkConf, SparkContext}

trait SparkApplication {

  def sparkConfig: SparkApplicationConfig

  def withSparkContext(f: SparkContext => Unit): Unit = {
    val sparkConf = new SparkConf()
      .setMaster(sparkConfig.master)
      .setAppName(sparkConfig.appName)

    val sc = new SparkContext(sparkConf)

    f(sc)
  }

}

case class SparkApplicationConfig(master: String,
                                  appName: String)
  extends Serializable


// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.tesnik.spark.streaming

import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.concurrent.duration.FiniteDuration

trait SparkStreamingApplication extends SparkApplication {

  def sparkStreamingConfig: SparkStreamingApplicationConfig

  def withSparkStreamingContext(f: (SparkContext, StreamingContext) => Unit): Unit = {
    withSparkContext { sc =>
      val ssc = new StreamingContext(sc, Seconds(sparkStreamingConfig.batchDuration.toSeconds))
      ssc.checkpoint(sparkStreamingConfig.checkpoint)

      f(sc, ssc)

      ssc.start()
      ssc.awaitTermination()
    }
  }

}

case class SparkStreamingApplicationConfig(
                                            batchDuration: FiniteDuration,
                                            checkpoint: String)
  extends Serializable

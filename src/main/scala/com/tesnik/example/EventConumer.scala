package com.tesnik.example

import java.sql.Connection

import com.tesnik.hbase.{HBaseConsumableMessage, MessagesForHBase}
import com.tesnik.spark.streaming.{CounsumedMessagesOuput, ConsumableMessages}

/**
  * Created by tiwariaa on 5/2/2016.
  */
class SampleEventConsumer extends ConsumableMessages{
  override def inputMessage(message: String): CounsumedMessagesOuput = OutputObject
}


//Sample object

object OutputObject extends CounsumedMessagesOuput{
  override def getHDFSMessages(): Map[String, String] = Map("hdfs://localhost:201212" -> "1|Joe|25|1")

  override def getHBaseConsumableMessages(): MessagesForHBase = MessagesForHBase(List(HBaseConsumableMessage("person", Map("pkId" -> 1, "version" -> 1), List("pkId", "version"))))
}

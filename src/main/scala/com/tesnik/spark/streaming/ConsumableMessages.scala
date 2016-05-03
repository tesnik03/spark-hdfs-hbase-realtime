package com.tesnik.spark.streaming

import com.tesnik.hbase.MessagesForHBase

/**
  * Created by tiwariaa on 5/2/2016.
  */
trait ConsumableMessages {
  def inputMessage(message:String):CounsumedMessagesOuput
}

trait CounsumedMessagesOuput {
  def getHDFSMessages():Map[String, String]
  def getHBaseConsumableMessages[T >: AnyVal]():MessagesForHBase[T]
}

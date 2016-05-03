package com.tesnik.example

import com.tesnik.spark.streaming.{CounsumedMessagesOuput, ConsumableMessages}

/**
  * Created by tiwariaa on 5/2/2016.
  */
class SampleEventConsumer extends ConsumableMessages{
  override def inputMessage(message: String): CounsumedMessagesOuput = ???
}

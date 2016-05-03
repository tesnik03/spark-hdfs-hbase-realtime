package com.tesnik.config

import scala.collection.mutable.ListBuffer

/**
  * Created by tiwariaa on 4/30/2016.
  */
abstract class MemoryBuffer extends CustomBuffer with Config {
  val messageListBuffer = new ListBuffer[String]

  @Override
  def getMessageCount(): Int = messageListBuffer.size

  @Override
  def addMessageInBuffer(message: String) = messageListBuffer += message

  @Override
  def getThresholdValue(): Int = numberOfMessagesThreshold.toString.toInt

}
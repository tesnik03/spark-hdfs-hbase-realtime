package com.tesnik.config

/**
  * Created by tiwariaa on 5/1/2016.
  */
trait CustomBuffer {
  def getMessageCount():Int
  def addMessageInBuffer(message: String)
  def getThresholdValue():Int
  def flushMessages()
}

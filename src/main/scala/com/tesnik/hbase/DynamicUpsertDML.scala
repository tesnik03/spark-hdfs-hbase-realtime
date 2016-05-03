package com.tesnik.hbase

import java.sql.{Statement, Connection, PreparedStatement}

import akka.actor.Actor

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

/**
  * Created by tiwariaa on 4/30/2016.
  */

case class HBaseConsumableMessage[T >: AnyVal](conn:Connection, table:String, keyValues:Map[String, T], columns:List[String])
case class MessagesForHBase[T >: AnyVal](messages: List[HBaseConsumableMessage[T]])
class DynamicUpsertDML extends Actor {

  def getPreparedStatement[T >: AnyVal](messagesForHBase: MessagesForHBase[T]):List[String] = {
    messagesForHBase.messages.map( message => {
      val query = new StringBuffer("upsert into $table ")
      val keyList = message.columns
      val valueList = message.keyValues.values.toList
      for(i <- 0 until message.columns.size - 1){
        query.append(keyList(i)).append(",")
      }
      //append last
      query.append(message.columns.last).append(") values (")

      for(i <- 0 until message.columns.size - 1){
        val value = message.keyValues(message.columns(i))
        query.append(appendQueryBasedOnType(value).getOrElse(null)).append(",")
      }

      query.append(appendQueryBasedOnType(message.keyValues(message.columns.last)).getOrElse(null)).append(")")

      query.toString
    })
  }



  def receive[T >: AnyVal] = {
    case messages:MessagesForHBase[T] => updateStatements(messages)
    case _ => printf("Error unknow paramenter")
  }

  def updateStatements[T >: AnyVal](messagesForHBase: MessagesForHBase[T]) = {

    val updateStatementes = getPreparedStatement(messagesForHBase)
    val con = HbaseConnection.getConnection
    updateStatementes.foreach(updateStmnt => {
      val stmnt:Statement = con.createStatement()
      stmnt.executeUpdate(updateStmnt)
    })
  }

  private def appendQueryBasedOnType[T >: AnyVal](input:T): Option[String] = {
    input match {
      case value: String => Some("'" + value + "'")
      case value: Int => Some(value.toString)
      case value: Double => Some(value.toString)
      case value: Long => Some(value.toString)
      case value: Float =>  Some(value.toString)
      case _ => null
    }
  }
}

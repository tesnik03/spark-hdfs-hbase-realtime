package com.tesnik.hbase

import java.sql.{DriverManager, Connection}

import com.tesnik.config.Config
import org.apache.hadoop.hbase.HBaseConfiguration

/**
  * Created by tiwariaa on 4/30/2016.
  */
object HbaseConnection extends Config{
  private var connection:Connection = null

  def getConnection:Connection = {
    if(connection == null) {
      Class.forName("org.apache.hadoop.hbase.jdbc.Driver")
      connection = DriverManager.getConnection("jdbc:hbql;maxtablerefs=10;hbase.master=" + hbaseHostName + ":" + hbaseHostPort);
      connection
    }else{
      connection
    }
  }


  def closeConnection = {
    if(connection!=null)
      connection.close()
  }
}

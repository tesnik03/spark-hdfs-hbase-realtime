package com.tesnik.hdfs

import java.io.{BufferedWriter, OutputStreamWriter}

import com.tesnik.config.{Config, MemoryBuffer}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.util.Random

/**
  * Created by tiwariaa on 5/1/2016.
  */
class LoadMessagesFromMemory(location: String, rowValue: String) extends MemoryBuffer with Config{


  //Alt + shift + P
  override def flushMessages(): Unit = {

    val eol = lineSeparator;

    val conf = new Configuration()
    conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"))
    conf.addResource(new Path("/etc/hadoop/conf/mapred-site.xml"))
    conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"))

    val fs = FileSystem.get(new Configuration)
    val rootDistDir = location
    val r = new Random
    val rootName = "/part-m-" + System.currentTimeMillis()
    val tmpPath = new Path(rootDistDir + rootName + ".tmp")
    val writer = new BufferedWriter(new OutputStreamWriter(fs.create(tmpPath)))
    writer.write(rowValue + eol)
    writer.close
  }
}

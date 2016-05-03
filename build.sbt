import AssemblyKeys._


name := "kafka-spark"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.8.2.1",
  "org.apache.kafka" % "kafka-clients" % "0.8.2.1",
  "org.apache.spark" %% "spark-core" % "1.4.0",
  "org.apache.spark" %% "spark-streaming" % "1.4.0",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.4.0",
  "com.typesafe" % "config" % "1.2.1",
  "net.ceedubs" %% "ficus" % "1.1.1",
  "org.slf4j" % "slf4j-api" % "1.7.10",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "container",
  "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container" artifacts Artifact("javax.servlet", "jar", "jar"),
  "org.apache.hbase" % "hbase-common" % "1.2.1",
  "org.apache.hbase" % "hbase-client" % "1.2.1",
  "org.apache.hbase" % "hbase-server" % "1.2.1"
  //"org.eclipse.jetty"  % "jetty-client" % "8.1.14.v20131031"
  )

    resolvers ++= Seq(
    "Spray repository" at "http://repo.spray.io",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"
    )

    assemblySettings

    seq(webSettings: _*)


    //atmosSettings

   //atmosSettings

//excludedJars in assembly <<= (fullClasspath in assembly) map { _ filter { cp =>
//      List("servlet-api", "guice-all", "junit", "uuid",
//        "jetty", "jsp-api-2.0", "antlr", "avro", "slf4j-log4j", "log4j-1.2",
//        "scala-actors","spark", "commons-cli", "stax-api", "mockito").exists(cp.data.getName.startsWith(_))
//    } }


mergeStrategy in assembly := {
      case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
      case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
      case "reference.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
}


//excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
//  cp filter {_.data.getName == "parquet-format-2.0.0.jar"}
//}



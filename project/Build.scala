import sbt._

import Keys._

import play.Project._

object Build extends sbt.Build {

  val appName = "base"

  val appVersion = "1.0.0-SNAPSHOT"

  val appDependencies = Seq(

    javaCore,
    javaJdbc,
    javaJpa,
    cache,

    // Google Guava
    "com.google.guava" % "guava" % "15.0",

    // Hibernate
    "org.hibernate" % "hibernate-entitymanager" % "4.2.7.SP1",

    // CDI
    "org.jboss.weld.se" % "weld-se" % "2.1.0.Final",
    "org.apache.deltaspike.core" % "deltaspike-core-api" % "0.5",
    "org.apache.deltaspike.core" % "deltaspike-core-impl" % "0.5",
    "org.apache.deltaspike.cdictrl" % "deltaspike-cdictrl-api" % "0.5",
    "org.apache.deltaspike.cdictrl" % "deltaspike-cdictrl-weld" % "0.5",

    // Metrics
    "com.codahale.metrics" % "metrics-core" % "3.0.1",
    "com.codahale.metrics" % "metrics-jvm" % "3.0.1",
    "com.codahale.metrics" % "metrics-json" % "3.0.1",

    // Camel
    "org.apache.camel" % "camel-core" % "2.12.2",
    "org.apache.camel" % "camel-jackson" % "2.12.2",
    "org.apache.camel" % "camel-cdi" % "2.12.2"
      exclude ("org.apache.deltaspike.core", "deltaspike-core-api")
      exclude ("org.apache.deltaspike.core", "deltaspike-core-impl")
      exclude ("org.apache.deltaspike.cdictrl", "deltaspike-cdictrl-api"))

  val main = play.Project(appName, appVersion, appDependencies).settings()

}
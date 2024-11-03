import sbt.Keys.version
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

name := "pwa.scalajs"

val catsVersion = "1.6.1"
val doobieVersion = "0.5.4"
val http4sVersion = "0.18.24"
val circeVersion = "0.11.1"

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.20"
)

lazy val root = project
  .in(file("."))
  .aggregate(sharedJS, sharedJVM, js, sw, jvm)
  .settings(commonSettings)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-core" % circeVersion
    )
  )

lazy val sharedJS = shared.js
lazy val sharedJVM = shared.jvm

lazy val js = (project in file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.8.0",
      "org.scala-js" %%% "scalajs-java-time" % "2.6.0",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion
    )
  ) dependsOn sharedJS

lazy val sw = (project in file("sw"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.8.0"
    )
  )

lazy val jvm = (project in file("jvm"))
  .settings(commonSettings)
  .settings(
    mainClass in reStart := Some("todo.TodoApp"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % "0.10.1",
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-server" % http4sVersion,
      "co.fs2" %% "fs2-core" % "0.10.7",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "com.github.pureconfig" %% "pureconfig" % "0.17.1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "ch.qos.logback" % "logback-classic" % "1.5.12",
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    ),
    (resources in Compile) += (fullOptJS in (sharedJS, Compile)).value.data,
    (resources in Compile) += (fullOptJS in (js, Compile)).value.data,
    (resources in Compile) += (fullOptJS in (sw, Compile)).value.data
  ) dependsOn (sharedJS, sharedJVM, js, sw)

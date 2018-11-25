name := "NowTvWatchList"

version := "1.0"

lazy val `nowtvwatchlist` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(guice) ++ Seq(
  "net.codingwell" %% "scala-guice" % "4.2.1",
  "org.typelevel" %% "cats-core" % "1.1.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)

scalacOptions += "-Ypartial-unification"

//unmanagedResourceDirectories in Test += baseDirectory(_ / "target/web/public/test")

disablePlugins(PlayLayoutPlugin)


      
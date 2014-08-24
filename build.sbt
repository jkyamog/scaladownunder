name := "scaladownunder"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  ws,
  cache,
  "com.pellucid" %% "datomisca" % "0.7-alpha-10",
  "com.pellucid" %% "play-datomisca" % "0.7-alpha-3",
  "com.datomic" % "datomic-free" % "0.9.4699" exclude("org.slf4j", "slf4j-nop"),
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.akka23-SNAPSHOT"
)

resolvers ++= Seq(
  "Pellucid Bintray" at "http://dl.bintray.com/content/pellucid/maven",
  "Couchbase Maven Repo" at "http://files.couchbase.com/maven2", // temporary, spy has changed from spy to net.spy, this is an archive copy.
  "clojars" at "http://clojars.org/repo/",
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
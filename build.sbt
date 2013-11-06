name := "faceSwapper"

version := "1.0-SNAPSHOT"

resolvers += "JavaCV maven repo" at "http://maven2.javacv.googlecode.com/git/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.googlecode.javacv" % "javacv" % "0.6"
)     

play.Project.playScalaSettings

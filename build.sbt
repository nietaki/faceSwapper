name := "faceSwapper"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.github.scaldi" %% "scaldi-play" % "0.2.2" 
)

play.Project.playScalaSettings

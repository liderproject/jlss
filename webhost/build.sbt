name := "jlss.webhost"

libraryDependencies ++= Seq(
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "javax.websocket" % "javax.websocket-api" % "1.1" % "provided",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2"
)

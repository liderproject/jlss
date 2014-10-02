name := "jlss.nif"

jetty()

libraryDependencies ++= Seq(
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "javax.websocket" % "javax.websocket-api" % "1.1" % "provided"
)

libraryDependencies ++= Seq(
"org.eclipse.jetty.websocket" % "javax-websocket-server-impl" % "9.2.2.v20140723" % "container" artifacts (Artifact("javax-websocket-server-impl", "jar", "jar"))
)

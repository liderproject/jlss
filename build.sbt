import AssemblyKeys._

name := "jlss"

version := "0.1"

organization in ThisBuild := "eu.liderproject"

scalaVersion in ThisBuild := "2.11.2"

libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

compileOrder in ThisBuild := CompileOrder.JavaThenScala

publishTo in ThisBuild := Some(Resolver.file("file",  new File("../jlss-gh-pages/releases")))

lazy val root = project in file(".") aggregate(core, java, codegen, nif, webhost)

lazy val java = project in file("java")

lazy val core = project in file("core") dependsOn (java)

lazy val codegen = project in file("codegen") settings (assemblySettings:_*) dependsOn (core)
  
lazy val nif = project in file("nif") settings (warSettings:_*) dependsOn(webhost)

lazy val webhost = project in file("webhost") dependsOn(core)

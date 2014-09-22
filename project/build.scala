/*import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object JLSSBuild extends Build {
  lazy val java = Project("java", file("java"))

  lazy val core = Project("core", file("core"), settings = Defaults.defaultSettings ++ 
    Seq(libraryDependencies ++= 
      Seq(
        "org.scalatest" %% "scalatest" % "2.2.1" % "test"
      )
    )
  ) dependsOn(java)

  lazy val codegen = Project("codegen", file("codegen"), settings = Defaults.defaultSettings ++ assemblySettings ++
    Seq(libraryDependencies ++=
      Seq(
        "org.apache.jena" % "jena-arq" % "2.12.0",
        "com.github.spullara.mustache.java" % "compiler" % "0.8.16"
      )
    )
  ) dependsOn(core)

  lazy val nif = Project("nif", file("nif"), settings = Defaults.defaultSettings ++ assemblySettings) dependsOn(core)

  lazy val webhost = Project("webhost", file("webhost"), settings = Defaults.defaultSettings ++ com.earldouglas.xwp.WarPlugin.warSettings)
}*/

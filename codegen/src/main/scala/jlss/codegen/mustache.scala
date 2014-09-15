package jlss.codegen

import com.github.mustachejava._
import java.io.File
import java.net.URI
import scala.collection.JavaConversions._

object MustacheCodeGen {
  import CodeGen._

  private class TypeConversion(map : Map[String,String]) {
    def apply(foo : String) = map.getOrElse(foo, foo)
  }

  private object TypeConversion {
    def apply(elems : (String,String)*) = new TypeConversion(Map(elems:_*))
  }

  private val typeConversions = Map(
    "java.mustache" -> TypeConversion(
      "anyURI" -> "Object",
      "any" -> "Object",
      "decimal" -> "double",
      "integer" -> "int",
      "string" -> "String"
    ),
    "scala.mustache" -> TypeConversion(
      "anyURI" -> "Object",
      "any" -> "Any",
      "decimal" -> "Double",
      "integer" -> "Int",
      "string" -> "String"
    )
  )

  private def uriToName(uri : URI) : String = uri.getFragment() match {
    case null => uri.getPath() match {
      case null => throw new RuntimeException("Cannot determine name for " + uri)
      case path => {
        val s = path.drop(path.lastIndexOf('/')+1)
        if(s.matches("\\w+")) {
          return s
        } else {
          throw new RuntimeException("Cannot determine name for " + uri)
        }
      }
    }
    case frag => if(frag.matches("\\w+")) {
      return frag
    } else {
      throw new RuntimeException("Cannot determine name for " + uri)
    }
  }

  private def uriToPackageName(uri : URI) : String = {
    if(uri.getFragment() != null && uri.getFragment() != "") {
      uri.getHost().split("\\.").filter(_ != "www").reverse.mkString(".") +
      uri.getPath().drop(1).split("/").map("." + _).mkString("")
    } else {
      uri.getHost().split("\\.").filter(_ != "www").reverse.mkString(".") +
      uri.getPath().drop(1).split("/").dropRight(1).map("." + _).mkString("")
    }
  }

  private def ucFirst(name : String) = name(0).toUpper + name.drop(1)
      

  private def clazzToMap(clazz : CodeGenClass, types : TypeConversion) : java.util.Map[String, Any] = mapAsJavaMap(Map(
    "type" -> uriToName(clazz.uri),
    "Type" -> ucFirst(uriToName(clazz.uri)),
    "package" -> uriToPackageName(clazz.uri),
    "uri" -> clazz.uri,
    "fields" -> seqAsJavaList(for((CodeGenField(name, uri, range, functional),index) <- clazz.fields.zipWithIndex) yield {
      mapAsJavaMap(Map(
        "name" -> name,
        "Name" -> ucFirst(name),
        "uri" -> uri,
        "range" -> (range match {
          case c2 : CodeGenClass => types(ucFirst(uriToName(c2.uri)))
          case v : CodeGenValue => types(ucFirst(v._type))
          case CodeGenAnyURI => types("anyURI")
          case CodeGenAny => types("any")
        }),
        "functional" -> functional,
        "last" -> (name == clazz.fields.last.name),
        "index" -> index
      ))
    })
  ))


  def generate(template : File, codeGen : CodeGenClass, target : File) = {
    val factory = new DefaultMustacheFactory()
    val mustache = factory.compile(new java.io.FileReader(template), "codegen")
    val out = new java.io.FileWriter(target)
    mustache.execute(out, clazzToMap(codeGen, typeConversions.getOrElse(template.getName(), TypeConversion())))
    out.flush
    out.close
  }
}

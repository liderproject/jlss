package jlss.services

import jlss._
import java.util.regex.Pattern

object FairlyGoodTokenizer extends StreamService {
  import JsonGen._

  private val pattern1 = Pattern.compile("(\\.\\.\\.+|[\\p{Po}\\p{Ps}\\p{Pe}\\p{Pi}\\p{Pf}\u2013\u2014\u2015&&[^'\\.]]|(?<!(\\.|\\.\\p{L}))\\.(?=[\\p{Z}\\p{Pf}\\p{Pe}]|\\Z)|(?<!\\p{L})'(?!\\p{L}))")
  private val pattern2 = Pattern.compile("\\p{C}|^\\p{Z}+|\\p{Z}+$")
    

  def context = obj(
    "lines" -> obj(
      "@container" -> "@list",
      "@id" -> "http://liderproject.github.io/jlss/lineTokenized"
    ),
    "tokens" -> obj(
      "@container" -> "@list",
      "@id" -> "http://liderproject.github.io/jlss/token"
    )
  )

  private def tokenize(s : String) = {
    val s1 = pattern1.matcher(s).replaceAll(" $1 ")
    val s2 = pattern2.matcher(s1).replaceAll("");
    s2.split("\\p{Z}+").toSeq
  }

  def apply(input : Json) : Json = input match {
    case JsonObject(v1) => v1.find(_.key == "lines") match {
      case Some(JsonField(_,JsonArray(v2))) => obj(
        "@context" -> context,
        "lines" -> JsonArray(v2.map(_ match {
          case JsonString(s) => obj(
            "tokens" -> tokenize(s)
          )
          case _ => throw new InputNotInSchema()
        })))
      case x => throw new InputNotInSchema(x.toString())
    }
    case _ => throw new InputNotInSchema()
  }
}

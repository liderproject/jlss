package jlss

import java.io.IOException
import java.net.{URL, URI, MalformedURLException, URISyntaxException}

class JsonLDSchema(val mappings : Map[String,JsonLDSchema.TermMapping], 
  val base : Option[URI], val vocab : Option[String], 
  val language : Option[String]) {
  def ++(schema : JsonLDSchema) : JsonLDSchema = new JsonLDSchema(
    mappings ++ schema.mappings,
    (base ++ schema.base).lastOption,
    (vocab ++ schema.vocab).lastOption,
    (language ++ schema.language).lastOption
  )
}

object JsonLDSchema {
  sealed trait TermMapping {
    def reverse = false
    def _type : Either[URI,Boolean] = Right(false)
  }

  case class TypedMapping(uri : URI, override val _type : Either[URI,Boolean], override val reverse : Boolean = false) extends TermMapping

  case class LangStringMapping(uri : URI, language : String) extends TermMapping

  case class LangContainerMapping(uri : URI) extends TermMapping

  case class ListMapping(uri : URI, override val _type : Either[URI,Boolean]) extends TermMapping

  case class AliasMapping(alias : String) extends TermMapping

  case class IndexMapping(uri : URI, override val _type : Either[URI,Boolean]) extends TermMapping

  private def mkURI(namespaces : Map[String,String], string : String) : URI = {
    try {
      val uri = if(string.contains(":")) {
        val prefix = string.take(string.indexOf(":"))
        namespaces.get(prefix) match {
          case Some(expansion) => expansion + string.drop(string.indexOf(":")+1)
          case None => string
        }
      } else {
        string
      }
      new URI(uri)
    } catch {
      case x : URISyntaxException => {
        namespaces.get("::base::") match {
          case Some(prefix) => try {
            try {
              return new URI(prefix + string)
            } catch {
              case x2 : URISyntaxException =>
            }
          }
          case None => 
        }
        throw new JsonLDException("Invalid base URI: %s" format string)
      }
    }
  }

  def fromJson(elem : Json) : JsonLDSchema = elem match {
    case JsonString(urlStr) => {
      try {
        val url = new URL(urlStr)
        val jsonParser = new services.JsonParser(JLSSUtils.readURL(url))
        return fromJson(jsonParser())
      } catch {
        case x : MalformedURLException => throw new JsonLDException("Invalid URL for schema %s" format urlStr)
        case x : IOException => throw new JsonLDException("Could not read schema at %s" format urlStr)
        case x : JsonException => throw new JsonLDException("Not a valid JSON object at %s" format urlStr)
      }
    }
    case JsonArray(elems) => elems.map(fromJson(_)).reduce((x,y) => x ++ y)
    case JsonObject(e) => {
      val elems = JsonObject(e).toObj
      // Step 1. Extract all mappings
      val namespaces2 : Map[String,String] = for {
        (k,v) <- elems 
        if !k.contains(":") && !k.startsWith("@") && v.isInstanceOf[JsonString] && !v.toString.startsWith("@")
      } yield {
        (k, v.toString)
      }

      // Step 2. Check @base, @vocab, @language
      val base = elems.get("@base").map(elem => elem match {
        case JsonString(s) => mkURI(namespaces2,s)
        case _ => throw new JsonLDException("@base should be a string")
      })
      val vocab = elems.get("@vocab").map(elem => elem match {
        case JsonString(s) => s
        case _ => throw new JsonLDException("@vocab should be a string")
      })
      val language = elems.get("@language").map(elem => elem match {
        case JsonString(s) => s
        case _ => throw new JsonLDException("@language should be a string")
      })

      val namespaces = vocab match {
        case Some(v) => namespaces2 + ("::base::" -> v)
        case None => namespaces2
      }
    
      // Step 3. Read mapping
      val mappings = for {
        (k,v) <- elems
        if !k.startsWith("@")
      } yield {
        v match {
          case JsonString(string) if string.startsWith("@") => {
            k -> AliasMapping(string)
          }
          case JsonString(string) => {
            k -> TypedMapping(mkURI(namespaces, string), Right(false)) 
          }
          case JsonObject(elems) => {
            def elemStr(id : String) = JsonObject(elems).toObj(id) match {
              case JsonString(s) => s
              case _ => throw new JsonLDException("%s should be a string" format id)
            }
            val id = if(elems.contains("@id")) {
              elemStr("@id")
            } else {
              k
            }
 
            val _type = if(elems.contains("@type")) {
              elemStr("@type") match {
                case "@id" => Right(true)
                case t => Left(mkURI(namespaces, t))
              }
            } else {
              Right(false)
            }

            if(elems.contains("@language")) {
              val language = elemStr("@language")

              k -> LangStringMapping(mkURI(namespaces, id), language)
            } else if(elems.contains("@container")) {
              elemStr("@container") match {
                case "@language" => k -> LangContainerMapping(mkURI(namespaces, id))
                case "@list" => k -> ListMapping(mkURI(namespaces, id), _type)
                case "@index" => k -> IndexMapping(mkURI(namespaces, id), _type)
                case "@set" => k -> TypedMapping(mkURI(namespaces, id), Right(false))
                case container => throw new JsonLDException("Unexpected container value %s" format container)
              }
            } else if(elems.contains("@reverse")) {
              val reverse = elemStr("@reverse")

              k -> TypedMapping(mkURI(namespaces, reverse), _type, true)

            } else {
              k -> TypedMapping(mkURI(namespaces, id), _type)
            }
          }
          case _ => throw new JsonLDException()
        }
      }

      return new JsonLDSchema(mappings, base, vocab, language)
    }
    case _ => throw new JsonLDException("Unexpected value %s" format elem.toString)
  }
}

class JsonLDException(val msg : String = "", val cause : Throwable = null) extends RuntimeException(msg, cause)

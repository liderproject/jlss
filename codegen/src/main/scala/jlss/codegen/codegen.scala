package jlss.codegen

import java.io.File
import jlss._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.vocabulary.{RDF, RDFS, OWL, XSD}
import java.net.URI
import org.apache.jena.riot.RDFDataMgr
import scala.collection.JavaConversions._

object CodeGen {
  import JsonLDSchema._

  sealed trait CodeGenType

  private[codegen] def ucFirst(name : String) = name(0).toUpper + name.drop(1)

  private[codegen] val reserved = Map[String, String](
    "String" -> "String_"
  )


  class CodeGenClass(val uri : URI) extends CodeGenType {
    var fields = List[CodeGenField]()
    lazy val packageName = {
      if(uri.getFragment() != null && uri.getFragment() != "") {
        uri.getHost().split("\\.").filter(_ != "www").map(_.replaceAll("\\W","")).reverse.mkString(".") +
        uri.getPath().drop(1).split("/").map(_.replaceAll("\\W","")).map("." + _).mkString("")
      } else {
        uri.getHost().split("\\.").filter(_ != "www").map(_.replaceAll("\\W","")).reverse.mkString(".") +
        uri.getPath().drop(1).split("/").map(_.replaceAll("\\W","")).dropRight(1).map("." + _).mkString("")
      }
    }
    lazy val name = {
      val n = _name
      reserved.getOrElse(n,n)
    }
    private def _name : String = ucFirst(uri.getFragment() match {
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
      })
  }

  class CodeGenValue(val _type : String) extends CodeGenType 

  object CodeGenAnyURI extends CodeGenType

  object CodeGenAny extends CodeGenType

  case class CodeGenField(name : String, uri : URI, range : CodeGenType, functional : Boolean = false)

  private def mappingToURI(m : TermMapping) = m match {
    case TypedMapping(uri,_,_) => Some(uri)
    case LangStringMapping(uri,_) => Some(uri)
    case LangContainerMapping(uri) => Some(uri)
    case ListMapping(uri,_) => Some(uri)
    case AliasMapping(_) => None
    case IndexMapping(uri,_) => Some(uri)
  }


  def buildModelForSchema(schema : JsonLDSchema) : Model = {
    def removeFrag(u : URI) = new URI(u.getScheme(), u.getSchemeSpecificPart(), null)
    var loadedURIs = collection.mutable.Set[URI]()
    val model = ModelFactory.createDefaultModel()
    def loadURI(u : URI) = {
      val fragless = removeFrag(u)
      if(!loadedURIs.contains(fragless)) {
        // TODO: Content Negotiation & Format checking
        RDFDataMgr.read(model, fragless.toString)
        loadedURIs += fragless
      }
    }

    schema.base match {
      case Some(base) => loadURI(base)
      case None => // no-op
    }

    for(mapping <- schema.mappings.values) {
      mappingToURI(mapping).map(loadURI(_))
    }

    return model
  }

  private case class ProcessedModelElement(
    domain : Option[URI],
    range : Option[URI],
    functional : Boolean,
    inverseFunctional : Boolean,
    objectProperty : Boolean,
    superClasses : List[URI]
  )
  private type ProcessedModel = Map[URI,ProcessedModelElement]

  private def processModel(schema : JsonLDSchema, model : Model) 
    : ProcessedModel = {
    for((key, mapping) <- schema.mappings if !mapping.isInstanceOf[AliasMapping]) yield {
      val uri = mappingToURI(mapping).get
      val stats = model.listStatements(model.createResource(uri.toString), null, null)
      var range : Option[URI] = None
      var domain : Option[URI] = None
      var functional = false
      var inverseFunctional = false
      var objectProperty = false
      var superclasses : List[URI] = Nil
      for(stat <- stats) {
        if(stat.getPredicate() == RDFS.range) {
          range = Some(new URI(stat.getObject().toString()))
        } else if(stat.getPredicate() ==  RDFS.domain) {
          domain = Some(new URI(stat.getObject().toString()))
        } else if(stat.getPredicate() == RDF.`type` && stat.getObject() == OWL.FunctionalProperty) {
          functional = true
        } else if(stat.getPredicate() == RDF.`type` && stat.getObject() == OWL.InverseFunctionalProperty) {
          inverseFunctional = true
        } else if(stat.getPredicate() == RDF.`type` && stat.getObject() == OWL.ObjectProperty) {
          objectProperty = true
        } else if(stat.getPredicate() == RDFS.subClassOf) {
          superclasses ::= new URI(stat.getObject().toString())
        }
      }
      uri -> ProcessedModelElement(domain, range, functional, inverseFunctional, objectProperty, superclasses)
    }
  }
    
  def type2type(_type : MappingType, range : Option[URI], 
    schema : JsonLDSchema, model : ProcessedModel, 
    clazzes : collection.mutable.Map[URI, CodeGenClass]) : CodeGenType = _type match {
      case TypedDataMapping(uri) => {
        if(uri.toString.startsWith(XSD.getURI())) {
          new CodeGenValue(uri.getFragment)
        } else {
          throw new RuntimeException("Unsupported datatype " + uri)
        }
      }
      case ObjectMapping => 
        range match {
          case Some(uri) => buildClass(schema, model, uri, clazzes)
          case None => range match {
            case Some(r) => buildClass(schema, model, r, clazzes)
            case None => CodeGenAnyURI
          }
      }
      case UntypedDataMapping =>
        range match {
          case Some(uri) =>
            if(uri.toString.startsWith(XSD.getURI())) {
              new CodeGenValue(uri.getFragment)
            } else {
              throw new RuntimeException("Unsupported datatype " + uri)
            }
          case None =>
           CodeGenAny
        }
  }

   def buildClass(schema : JsonLDSchema, rdfModel : ProcessedModel, clazz : URI, clazzes :
    collection.mutable.Map[URI, CodeGenClass])  : CodeGenClass = {
      clazzes.get(clazz) match {
        case Some(c) => c
        case None =>
          val c = new CodeGenClass(clazz)
          clazzes.put(clazz, c)
          for((name, mapping) <- schema.mappings) {
            mappingToURI(mapping) match {
              case Some(u) => rdfModel.get(u) match {
                case Some(ProcessedModelElement(domain,range,functional,inverseFunctional,objectProperty,superClasses)) => {
                  if(objectProperty && mapping._type != ObjectMapping) {
                    throw new JsonLDException("%s is an object property in the ontology but not the schema" format name)
                  }
                  if(!mapping.reverse && domain != None && domain.get == clazz) {    
                    val r = type2type(mapping._type, range, schema, rdfModel, clazzes)
                    c.fields ::= new CodeGenField(name, u, r, functional)
                  } else if(mapping.reverse && range != None && range.get == clazz) {
                    val r = type2type(mapping._type, domain, schema, rdfModel, clazzes)
                    c.fields ::= new CodeGenField(name, u, r, inverseFunctional)
                  }
                }
                case None => throw new RuntimeException("should be unreachable as processModel constructs from schema.mappings")
              }
              case None => // no op
            }
          }
          c
      }
    }
  
  def buildCodeGenModel(schema : JsonLDSchema, rootClass : URI) : CodeGenClass = {
    val clazzes = collection.mutable.Map[URI, CodeGenClass]()

    val rdfModel = buildModelForSchema(schema)

    val processedModel = processModel(schema, rdfModel)

    return buildClass(schema, processedModel, rootClass, clazzes)
  }

  def main(_args : Array[String]) {
    var args = _args.toList
    var i = 0
    var srcDir : Option[String] = None
    while(i < args.size) {
      if(args(i) == "-p") {
        srcDir = Some(args(i+1))
        args = args.take(i) ::: args.drop(i+2)
      } else {
        i += 1
      }
    }
    if(args.length != 3) {
      System.err.println("Usage: codegen -p srcDir language context classURI")
      System.exit(-1)
    }
    // 1. Load Json
    val json = new jlss.services.JsonParser(new java.io.InputStreamReader(new java.net.URL(args(1)).openStream()))()

    // 2. Read Context
    val context = JsonLDSchema.fromJson(json)

    // 3. Build Code Gen Classes
    val codeGen = buildCodeGenModel(context, new URI(args(2)))

    // 4. Find template
    val template = new java.io.InputStreamReader(this.getClass().getResource("/mustache/%s.mustache" format args(0)).openStream())

    // 5. Make directories 
    val dir = new File((srcDir match {
      case Some(s) => if(s.endsWith(System.getProperty("file.separator"))) {
        s
      } else {
        s + System.getProperty("file.separator")
      }
      case None => ""
    }) + codeGen.packageName.replaceAll("\\.",System.getProperty("file.separator")))
    dir.mkdirs()

    // 6. Write template
    MustacheCodeGen.generate(template, args(0), codeGen, new File(dir, codeGen.name + "." + args(0)))
  }

}

package jlss.webhost

import java.io.{StringReader, StringWriter}
import java.lang.reflect.{Modifier}
import java.util.{Set => JSet, HashSet => JHashSet}
import javax.servlet.ServletConfig
import javax.servlet.http._
import javax.websocket._
import javax.websocket.server._
import jlss.javajson.{JSONObject}
import jlss.services.DefaultJSONSerializer
import scala.collection.JavaConversions._

abstract class JLSSHost extends HttpServlet {
  def services : Map[String, JLSSService[_,_]]

  def getEndpointConfigs(set : JSet[Class[_ <: Endpoint]]) : JSet[ServerEndpointConfig] = {
    new JHashSet[ServerEndpointConfig]() {
      for((name,service) <- services) {
        System.err.println("Adding " + name)
        add(ServerEndpointConfig.Builder.create(classOf[JLSSWebsocketHost[_,_]], "/" + name).
          configurator(new ServerEndpointConfig.Configurator() {
            override def getEndpointInstance[T](clazz : Class[T]) = {
              if(clazz == classOf[JLSSWebsocketHost[_,_]]) {
                new JLSSWebsocketHost(service).asInstanceOf[T]
              } else {
                throw new InstantiationException()
              }
            }
          }).build())
      }
    }
  }

  def getAnnotatedEndpointClasses(set : java.util.Set[Class[_]]) : JSet[Class[_]] = {
    java.util.Collections.emptySet()
  }

  override def service(req : HttpServletRequest, resp : HttpServletResponse) {
    req.getPathInfo() match {
      case "/" => defaultPage(resp)
      case "" => defaultPage(resp)
      case "/index.html" => defaultPage(resp)
      case svc if services contains (svc.drop(1)) => handleService(req, resp, services(svc.drop(1)))
      case _ => notFound(resp)
    }
  }

  def defaultPage(resp : HttpServletResponse) {
    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("text/html")
    val out = resp.getWriter()
    val pp = new xml.PrettyPrinter(80,4)
    out.write(pp.format(
    <html>
      <head>
        <title>JLSS Webhost</title>
      </head>
      <body>
        <h1>JLSS Webhost</h1>
        <p>This is a generic JLSS webhost, hosting the following services</p>
        <ul>{
          for(name <- services.keys) yield {
            <li><a href={name}>{name}</a></li>
          }
        }
        </ul>
      </body>
    </html>))
    out.flush
    out.close
  }

  def notFound(resp : HttpServletResponse) {
    resp.sendError(HttpServletResponse.SC_NOT_FOUND)
  }

  def handleService[A,B](req : HttpServletRequest, resp : HttpServletResponse, svc : JLSSService[A,B]) {
    val serializer = new DefaultJSONSerializer()
    val m1 = svc.inClass.getMethod("fromJSON", classOf[JSONObject])
    val in = m1.invoke(null, serializer.read(req.getReader()))
    val m2 = svc.outClass.getMethod("toJSON")

    val out = svc.foo(in.asInstanceOf[A])
    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("application/json+ld")
    val writer = resp.getWriter()
    serializer.write(m2.invoke(out).asInstanceOf[JSONObject], writer)
    writer.flush()
    writer.close()
  }
}

case class JLSSService[A,B](val foo : A => B, val inClass : Class[_], val outClass : Class[_]) {
  private def check(condition : Boolean, failMessage : String) = {
    if(!condition)
      throw new JLSSServiceException(failMessage)
  }

  inClass match {
    case c if c == classOf[String] => 
    case _ => 
      try {
        val m = inClass.getMethod("fromJSON", classOf[JSONObject])
        check(Modifier.isPublic(m.getModifiers()), "%s.fromJSON must be public" format inClass.getName())
        check(Modifier.isStatic(m.getModifiers()), "%s.fromJSON must be static" format inClass.getName())
        check(m.getReturnType() == inClass, "%s.fromJSON must return %s" format (inClass.getName(), inClass.getName()))
      } catch {
        case x : NoSuchMethodException => throw new JLSSServiceException(
          "Class %s needs a method with the signature: public static %s fromJSON(JSONObject json)" format
            (inClass.getName(), inClass.getName()), x)
      }
  }
  outClass match {
    case c if c == classOf[String] =>
    case _ =>
      try {
        val m = inClass.getMethod("toJSON")
        check(Modifier.isPublic(m.getModifiers()), "%s.toJSON must be public" format inClass.getName())
        check(!Modifier.isStatic(m.getModifiers()), "%s.toJSON must not be static" format inClass.getName())
        check(m.getReturnType() == classOf[JSONObject], "%s.toJSON must return JSONObject but returns %s" format (inClass.getName(),
          m.getReturnType().getName()))
      } catch {
        case x : NoSuchMethodException => throw new JLSSServiceException(
          "Class %s needs a method with the signature: public JSONObject toJSON(%s object)" format
            (inClass.getName(), inClass.getName()), x)
      }
  }
}

object streamService {
  def apply[A,B](foo : A => B)(implicit m1 : Manifest[A], m2 : Manifest[B]) = 
    JLSSService(foo,m1.erasure,m2.erasure)
}

class JLSSWebsocketHost[A,B](service : JLSSService[A,B]) extends Endpoint {

  override def onOpen(session : Session, config : EndpointConfig) {
    session.addMessageHandler(new MessageHandler.Whole[String]() {
      override def onMessage(msg : String) {
        handleService(session, msg)
      }
    })
  }

  private def handleService(session : Session, msg : String) {
    try {
      val serializer = new DefaultJSONSerializer()
      val m1 = service.inClass.getMethod("fromJSON", classOf[JSONObject])
      val in = m1.invoke(null, serializer.read(new StringReader(msg)))
      val m2 = service.outClass.getMethod("toJSON")

      val out = service.foo(in.asInstanceOf[A])
      val writer = new StringWriter()
      serializer.write(m2.invoke(out).asInstanceOf[JSONObject], writer)
      writer.flush()
      writer.close()
      session.getBasicRemote().sendText(writer.toString)
    } catch {
      case x : Exception =>
        session.getBasicRemote().sendText("error: " + x.getMessage())
    }
  }

}

class JLSSServiceException(message : String = "", cause : Throwable = null) extends RuntimeException(message, cause)

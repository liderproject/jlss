package jlss

import jlss.javajson._

////////////////////////////////////////////////////////////////////////////////
// Exceptions

class InputNotInSchema(msg : String = "", cause : Throwable = null) extends JsonException(msg, cause)

class JsonStreamException(msg : String = "", cause : Throwable = null) extends JsonException(msg, cause)

////////////////////////////////////////////////////////////////////////////////
// Service connection

trait StreamSource {
  def |(ss : StreamService)(implicit context : ServiceContext) : StreamSource = {
    context.connect(this, ss)
  }
  def |(ss : StreamSink)(implicit context : ServiceContext) : Unit = {
    context.connect(this, ss)
  }

  def apply() : Json
}

trait StreamSink {
  def apply(input : Json) : Unit
}

trait StreamService {
  def apply(input : Json) : Json
}

trait ServiceContext {
  def connect(src : StreamSource, svc : StreamService) : StreamSource
  def connect(src : StreamSource, snk : StreamSink) : Unit
}

object DefaultServiceContext extends ServiceContext {
  def connect(src : StreamSource, svc : StreamService) = new StreamSource {
    def apply() = svc(src())
  }
  def connect(src : StreamSource, snk : StreamSink) = snk(src())
}

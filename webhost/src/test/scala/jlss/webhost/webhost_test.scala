package jlss.webhost

import jlss.javajson.{JSON}
import org.scalatest._

class WebhostTest extends FlatSpec with Matchers {


  "service" should "be able to see parameter classes" in {
    class Bar {
      def foo(x : String) = "bar"
    }

    val s = streamService(new Bar().foo)
    s.inClass should be (classOf[String])
    s.outClass should be (classOf[String])
  }

/*  This doesn't work as Scala doesn't compile objects to static methods in this context
 *  "service" should "validate Java-like classes" in {
    class Bar {
      def toJSON() : JSON = null
    }
    object Bar {
      def fromJSON(json : JSON) : Bar = null
    }
    class Baz {
      def foo(b : Bar) : Bar = null
    }
    val s = service(new Baz().foo)
  }*/
}


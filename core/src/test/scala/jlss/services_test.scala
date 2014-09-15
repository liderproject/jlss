package jlss

import org.scalatest._
import jlss.services._

class TestServices extends FlatSpec with Matchers {
  "simple services" should "produce output" in {
    val source = new LineReader(io.Source.fromFile("core/src/test/resources/simple.txt"))
    val sw = new java.io.StringWriter()
    val sink = new Outputter(sw)
    implicit val context = DefaultServiceContext

    source | sink

    sw.toString should be
    ("""{"@context":{"lines":{"@container":"@list","@id":"http://liderproject.github.io/jlss/line"}},"lines":["foo","bar","baz"]}""")
  }

  "simple source-service-sink pipeline" should "function" in {
    val source = new LineReader(io.Source.fromFile("core/src/test/resources/simple.txt"))
    val sw = new java.io.StringWriter()
    val sink = new Outputter(sw)
    implicit val context = DefaultServiceContext
    
    source | FairlyGoodTokenizer | sink

    sw.toString should be
    ("""{"@context":{"lines":{"@container":"@list","@id":"http://liderproject.github.io/jlss/lineTokenized"},"tokens":{"@container":"@list","@id":"http://liderproject.github.io/jlss/token"}},"lines":[{"tokens":["foo"]},{"tokens":["bar"]},{"tokens":["baz"]}]}""") 
  }
}


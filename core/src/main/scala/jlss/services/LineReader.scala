package jlss.services

import jlss._

class LineReader(source : io.Source) extends StreamSource {
  import JsonGen._

  def genContext : Json = obj(
    "lines" -> obj(
      "@container" -> "@list",
      "@id" -> "http://liderproject.github.io/jlss/line"
    )
  )

  def apply() : JsonObject = obj(
    "@context" -> genContext,
    "lines" -> source.getLines
  )
}

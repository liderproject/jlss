package jlss


object JsonGen {

  def obj(elems : (String, Json)*) = JsonObject(
    (elems.map {
      case (x,y) => JsonField(x,y)
    }).iterator
  )

  def array(elems : Json*) = JsonArray(elems.iterator)

  implicit class StringJsonGen(s : String) extends JsonString(s)
  implicit class IteratorStringJsonGen(ss : Iterator[String]) extends JsonArray(ss.map(JsonString(_)))
  implicit class SeqStringJsonGen(ss : Seq[String]) extends JsonArray(ss.map(JsonString(_)).iterator)

  implicit class IntJsonGen(s : Int) extends JsonInt(s)
  implicit class IteratorIntJsonGen(ss : Iterator[Int]) extends JsonArray(ss.map(JsonInt(_)))
  implicit class SeqIntJsonGen(ss : Seq[Int]) extends JsonArray(ss.map(JsonInt(_)).iterator)


  implicit class NumberJsonGen(s : Double) extends JsonNumber(s)
  implicit class IteratorNumberJsonGen(ss : Iterator[Double]) extends JsonArray(ss.map(JsonNumber(_)))
  implicit class SeqNumberJsonGen(ss : Seq[Double]) extends JsonArray(ss.map(JsonNumber(_)).iterator)


  implicit class BooleanJsonGen(s : Boolean) extends JsonBoolean(s)
  implicit class IteratorBooleanJsonGen(ss : Iterator[Boolean]) extends JsonArray(ss.map(JsonBoolean(_)))
  implicit class SeqBooleanJsonGen(ss : Seq[Boolean]) extends JsonArray(ss.map(JsonBoolean(_)).iterator)
}

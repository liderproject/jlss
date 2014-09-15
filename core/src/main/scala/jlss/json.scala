package jlss

import jlss.javajson._


sealed trait Json extends JSON {
  def toObj : Object
}

object Json {
  def fromJava(java : JSON) : Json = java match {
    case j : Json => j
    case ja : JSONArray => JsonArray(new Iterator[Json] {
      def next = Json.fromJava(ja.next())
      def hasNext = ja.hasNext
    })
    case jo : JSONObject => JsonObject(new Iterator[JsonField] {
      def next = {
        val jf = jo.next()
        JsonField(jf.key, Json.fromJava(jf.value))
      }
      def hasNext = jo.hasNext
    })
    case js : JSONString => JsonString(js.value())
    case jn : JSONNumber => JsonNumber(jn.value())
    case ji : JSONInt => JsonInt(ji.value())
    case jb : JSONBoolean => JsonBoolean(jb.value())
    case jn : JSONNull => JsonNull
    case _ => throw new RuntimeException("Please do not extend jlss.javajson.JSON directly!")
  }
}

case class JsonArray(val values : Iterator[Json]) extends Json with JSONArray {
  def toObj : List[Object] = (values map (_.toObj)).toList
  def hasNext = values.hasNext
  def next = values.next
  def toObject = scala.collection.JavaConversions.asJavaCollection(toObj)
}

case class JsonObject(val values : Iterator[JsonField]) extends Json with JSONObject {
  def toObj : Map[String, Object] = (values map ({ case JsonField(k,v) => k -> v.toObj })).toMap
  def hasNext = values.hasNext
  def next = {
    val jf = values.next()
    new JSONObject.JSONField(jf.key, jf.value)
  }
  def toObject = scala.collection.JavaConversions.asJavaMap(toObj)
}

case class JsonField(val key : String, val value : Json)

case class JsonString(val value : String) extends Json with JSONString {
  def toObj = value
  def toObject = toObj
}

case class JsonInt(val value : Int) extends Json with JSONInt {
  def toObj = new Integer(value)
  def toObject = toObj
}

case class JsonNumber(val value : Double) extends Json with JSONNumber {
  def toObj = new java.lang.Double(value)
  def toObject = toObj
}

case class JsonBoolean(val value : Boolean) extends Json with JSONBoolean {
  def toObj = new java.lang.Boolean(value)
  def toObject = toObj
}

object JsonNull extends Json {
  def toObj = null
  def toObject = toObj
}



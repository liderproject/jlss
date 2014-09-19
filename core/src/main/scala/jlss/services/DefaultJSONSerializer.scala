package jlss.services

import java.io.{InputStream, InputStreamReader, OutputStream, OutputStreamWriter, Reader, Writer}
import jlss.Json
import jlss.javajson.{JSON, JSONSerializer}

class DefaultJSONSerializer extends JSONSerializer {

  def read(stream : InputStream) = read(new InputStreamReader(stream))
  def write(json : JSON, stream : OutputStream) = write(json, new OutputStreamWriter(stream))

  def read(reader : Reader) = new JsonParser(reader).apply()

  def write(json : JSON, writer : Writer) = {
    new Outputter(writer).apply(Json.fromJava(json))
    writer.write("\n")
    writer.flush()
  }
}

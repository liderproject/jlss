package jlss.codegen

import java.io.File
import java.net.URI
import org.scalatest._

class CodeGenTest extends FlatSpec with Matchers {
  import CodeGen._

  "CodeGen" should "generate valid java" in {
    val outFile = File.createTempFile("test",".java")
    outFile.deleteOnExit()
    val codeGenClass = new CodeGenClass(new URI("http://www.example.com/test"))
    codeGenClass.fields ::= CodeGenField("field1", new URI("http://www.example.com/test#field1"),
      new CodeGenValue("string"), true)
    codeGenClass.fields ::= CodeGenField("field2", new URI("http://www.example.com/test#field2"),
      CodeGenAnyURI)
    val out = MustacheCodeGen.generate(new File("codegen/src/main/resources/mustache/java.mustache"), 
      codeGenClass,
      outFile)
    val content = scala.io.Source.fromFile(outFile).getLines().mkString("\n")
    //println(content)
    content.contains("final Set<String> field2") should be (true)
  }

  "CodeGen" should "generate valid scala" in {
    val outFile = File.createTempFile("test",".scala")
    outFile.deleteOnExit()
    val codeGenClass = new CodeGenClass(new URI("http://www.example.com/test"))
    codeGenClass.fields ::= CodeGenField("field1", new URI("http://www.example.com/test#field1"),
      new CodeGenValue("string"), true)
    codeGenClass.fields ::= CodeGenField("field2", new URI("http://www.example.com/test#field2"),
      CodeGenAnyURI)
    val out = MustacheCodeGen.generate(new File("codegen/src/main/resources/mustache/scala.mustache"), 
      codeGenClass,
      outFile)
    val content = scala.io.Source.fromFile(outFile).getLines().mkString("\n")
    content.contains("field2 : Set[String]") should be (true)
  }

}

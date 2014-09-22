# Creating a simple JLSS service

    git clone git@github.com:earldouglas/xwp-template simple-tokenizer
    rm src/main/scala/XwpTemplateServlet.scala
    mkdir src/main/java
    mkdir src/main/resources
    rm -fr src/test
    
Create `src/main/resources/context.json`

    {
        "anchorOf": "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#anchorOf",
        "subString": {
            "@id": "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#subString",
            "@type": "@id"
        }
    } 

Generate code

    wget http://liderproject.github.io/jlss/releases/jlss.codegen-assembly-0.1-SNAPSHOT.jar
    java -jar jlss.codegen-assembly-0.1-SNAPSHOT.jar -p src/main/java java file:src/main/resources/context.json http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String

Write tokenizer to `src/main/java/Tokenizer.java`

    import org.unileipzig.persistence.nlp2rdf.ontologies.nifcore.String_;
    
    public class Tokenizer {
       public String_ tokenize(String_ s) {
          final String[] tokens = s.getAnchorOf().iterator().next().split("\\s+");
          for(String t : tokens) {
              final String_ sub = new String_();
              sub.addAnchorOf(t);
              s.addSubString(sub);
          }
          return s;
       }
    }

Write servlet to `src/main/scala/ExampleServlet.scala`

    import jlss.webhost._

    class ExampleServlet extends JLSSHost {
       def services = Map(
          "tokenizer" -> streamService { new Tokenizer().tokenize }
       )
    }
   
Modify `src/main/webapp/WEB-INF/web.xml`

    <servlet>
      <servlet-name>example</servlet-name>
      <servlet-class>ExampleServlet</servlet-class>
    </servlet>
  
    <servlet-mapping>
      <servlet-name>example</servlet-name>
      <url-pattern>/*</url-pattern>
    </servlet-mapping>

Write `project/plugins.sbt`

    addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "1.0.0-M2")

Write `build.sbt`

    name := "xwp-template"
    
    scalaVersion := "2.11.2"
    
    jetty()
    
    libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
    
    resolvers += "Lider Project" at "file:/home/jmccrae/projects/lider/jlss-gh-pages/releases/"
    
    libraryDependencies ++= Seq( // test
        "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "test"
      , "org.eclipse.jetty" % "jetty-plus" % "9.1.0.v20131115" % "test"
      , "javax.servlet" % "javax.servlet-api" % "3.1.0" % "test"
      , "eu.liderproject" %% "jlss-webhost" % "0.1-SNAPSHOT"
    )

Run:

    $ sbt
    > container:start 

Test:

    $ curl -d '{"anchorOf":["this is a test"]}' http://localhost:8080/tokenizer

package jlss.niftokenizer

import jlss.webhost._
import org.unileipzig.persistence.nlp2rdf.ontologies.nifcore.String_

/*class TokenizerConfig extends javax.websocket.server.ServerApplicationConfig {
  override def getEndpointConfigs(set : java.util.Set[Class[_ <: javax.websocket.Endpoint]]) = {
    new java.util.HashSet[javax.websocket.server.ServerEndpointConfig]() {
      add(javax.websocket.server.ServerEndpointConfig.Builder.create(classOf[TokenizerSocket], "/").build())
    }
  }

  override def getAnnotatedEndpointClasses(set : java.util.Set[Class[_]]) = {
    java.util.Collections.emptySet()
  }
}*/


class TokenizerServlet extends JLSSHost with javax.websocket.server.ServerApplicationConfig {
  def services = Map(
    "tokenizer" -> streamService { new NIFTokenizer().tokenize }
  )
}

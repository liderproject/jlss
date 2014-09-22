package jlss.niftokenizer

import jlss.webhost._
import org.unileipzig.persistence.nlp2rdf.ontologies.nifcore.String_

class TokenizerServlet extends JLSSHost {
  def services = Map(
    "tokenizer" -> streamService { new NIFTokenizer().tokenize }
  )
}

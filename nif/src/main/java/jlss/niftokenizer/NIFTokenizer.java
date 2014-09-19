package jlss.niftokenizer;

import java.util.regex.Pattern;
import jlss.javajson.*;
import jlss.services.DefaultJSONSerializer;
import org.unileipzig.persistence.nlp2rdf.ontologies.nifcore.String_;

public class NIFTokenizer {
  private final Pattern pattern1 = Pattern.compile("(\\.\\.\\.+|[\\p{Po}\\p{Ps}\\p{Pe}\\p{Pi}\\p{Pf}\u2013\u2014\u2015&&[^'\\.]]|(?<!(\\.|\\.\\p{L}))\\.(?=[\\p{Z}\\p{Pf}\\p{Pe}]|\\Z)|(?<!\\p{L})'(?!\\p{L}))");
  private final Pattern pattern2 = Pattern.compile("\\p{C}|^\\p{Z}+|\\p{Z}+$");
 
  private String[] tokenize(String s) {
    final String s1 = pattern1.matcher(s).replaceAll(" $1 ");
    final String s2 = pattern2.matcher(s1).replaceAll("");
    return s2.split("\\p{Z}+");
  }

  private String_ tokenize(String_ s) {
      final String[] tokens = tokenize(s.getAnchorOf().iterator().next());
      for(String t : tokens) {
          final String_ sub = new String_();
          sub.addAnchorOf(t);
          s.addSubString(sub);
      }
      return s;
  }
      

  public static void main(String[] args) {
      final JSONSerializer serializer = new DefaultJSONSerializer();
      final NIFTokenizer tokenizer = new NIFTokenizer();

      final String_ result = tokenizer.tokenize(String_.fromJSON((JSONObject)serializer.read(System.in)));
      System.out.println(result.toJSON().toString());
      serializer.write(result.toJSON(), System.out);
  }

}



package org.unileipzig.persistence.nlp2rdf.ontologies.nifcore;

import java.util.Collections.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jlss.jsonjava.*;

/**
 * Automatically generated POJOs for ontology at http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String.
 * DO NOT EDIT! 
 */
public class String {
    private final Set<String> anchorOf = new HashSet<String>();

    public Set<String> getAnchorOf() {
        return unmodifiableSet(this.anchorOf);
    }

    public void setAnchorOf(Set<String> anchorOf) {
        this.anchorOf.clear();
        this.anchorOf.addAll(anchorOf);
    }

    public void addAnchorOf(String anchorOf) {
        this.anchorOf.add(anchorOf);
    }

    public boolean removeAnchorOf(String anchorOf) {
        return this.anchorOf.remove(anchorOf);
    }

    public JSON toJSON() {
        return toJSON(DefaultJSONFactory.newInstance());
    }

    public JSONObject toJSON(JSONFactory factory) {
        final JSONFactory.JSONObjectBuilder fields = factory.startObject();
        
        {
            final JSONFactory.JSONArrayBuilder elements = factory.startArray();
            for(String elem : getAnchorOf()) {
                elements.add(factory.mkString(elem));
            }
            fields.add("anchorOf", elements.end());
        }
        
        return fields.end();
    }

    public static String fromJSON(JSONObject json) {
        final String object = new String();

        while(json.hasNext()) {
            final JSONField field = json.next();
            if(field.key.equals("anchorOf")) {
                if(!(field.value instanceof JSONArray)) {
                    throw new JSONException("anchorOf should be an array but was " + field.value);
                }
                final JSONArray array = (JSONArray)field.value;
                while(array.hasNext()) {
                    final JSON value = array.next();

                    if(!(value instanceof JSONString)) {
                        throw new JSONException("anchorOf should be a string but was " + value);
                    }
                    object.addAnchorOf(((JSONString)value).value);
                }
            }
        }

        return object;
    }   
}

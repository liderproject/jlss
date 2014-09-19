package org.unileipzig.persistence.nlp2rdf.ontologies.nifcore;

import static java.util.Collections.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jlss.JsonException;
import jlss.javajson.*;

/**
 * Automatically generated POJOs for ontology at http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String.
 * DO NOT EDIT! 
 */
public class String_ {
    private final Set<String_> subString = new HashSet<String_>();

    public Set<String_> getSubString() {
        return unmodifiableSet(this.subString);
    }

    public void setSubString(Set<String_> subString) {
        this.subString.clear();
        this.subString.addAll(subString);
    }

    public void addSubString(String_ subString) {
        this.subString.add(subString);
    }

    public boolean removeSubString(String_ subString) {
        return this.subString.remove(subString);
    }
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
            if(!getSubString().isEmpty()) {
                final JSONFactory.JSONArrayBuilder elements = factory.startArray();
                for(String_ elem : getSubString()) {
                    elements.add(elem == null ? factory.mkNull() : elem.toJSON());
                }
                fields.put("subString", elements.end());
            }
        }
        {
            if(!getAnchorOf().isEmpty()) {
                final JSONFactory.JSONArrayBuilder elements = factory.startArray();
                for(String elem : getAnchorOf()) {
                    elements.add(factory.mkString(elem));
                }
                fields.put("anchorOf", elements.end());
            }
        }
        
        return fields.end();
    }

    public static String_ fromJSON(JSONObject json) {
        final String_ object = new String_();

        while(json.hasNext()) {
            final JSONObject.JSONField field = json.next();
            if(field.key.equals("subString")) {
                if(!(field.value instanceof JSONArray)) {
                    throw new JsonException("subString should be an array but was " + field.value);
                }
                final JSONArray array = (JSONArray)field.value;
                while(array.hasNext()) {
                    final JSON value = array.next();

                    if(value instanceof JSONNull) {
                        object.addSubString(null);
                    }                    
                    if(!(value instanceof JSONObject)) {
                        throw new JsonException("subString should be an object or null but was " + value);
                    }
                    object.addSubString(String_.fromJSON((JSONObject)value));
                }
            }
            if(field.key.equals("anchorOf")) {
                if(!(field.value instanceof JSONArray)) {
                    throw new JsonException("anchorOf should be an array but was " + field.value);
                }
                final JSONArray array = (JSONArray)field.value;
                while(array.hasNext()) {
                    final JSON value = array.next();

                    if(!(value instanceof JSONString)) {
                        throw new JsonException("anchorOf should be a string but was " + value);
                    }
                    object.addAnchorOf(((JSONString)value).value());
                }
            }
        }

        return object;
    }   
}

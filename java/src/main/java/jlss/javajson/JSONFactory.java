package jlss.javajson;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Creates JSON elements as required
 * @author John McCrae
 */
public interface JSONFactory {

    /**
     * Create an array object from a collection
     */
    JSONArray mkArray(Collection<JSON> elements);

    /**
     * Create a (JSON) object from a map
     */
    JSONObject mkObject(Map<String, JSON> elements);

    /**
     * Create a (JSON) string
     */
    JSONString mkString(String value);

    /**
     * Create a (JSON) integer
     */
    JSONInt mkInt(int value);

    /**
     * Create a (JSON) floating-point number
     */
    JSONNumber mkNumber(double value);

    /**
     * Create a (JSON) boolean
     */
    JSONBoolean mkBool(boolean value);

    /**
     * Create a (JSON) null
     */
    JSONNull mkNull();

    /**
     * Start creating a JSON object
     */
    JSONObjectBuilder startObject();

    /**
     * Start creating a JSON array
     */
    JSONArrayBuilder startArray();

    /**
     * An stream based JSON object builder
     */
    public interface JSONObjectBuilder {
        /**
         * Put a value into this object
         * @throws IllegalStateException If {@link end()} has already been called
         * @return <code>this</code>
         */
        JSONObjectBuilder put(String name, JSON value);
        /**
         * Close this object 
         * @return The JSON object
         */
        JSONObject end();
    }

    public interface JSONArrayBuilder {
        /**
         * Add a value to this array
         * @throws IllegalStateException If {@link end()} has already been called
         * @return <code>this</code>
         */
        JSONArrayBuilder add(JSON value);
        /**
         * Close this object
         * @return The JSON array
         */
        JSONArray end();
    }
   
}


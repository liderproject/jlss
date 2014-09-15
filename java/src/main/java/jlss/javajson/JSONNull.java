package jlss.javajson;

/**
 * A null value in a JSON document
 * @author John McCrae
 */
public interface JSONNull extends JSON {
    /**
     * Read the Json Element completely and convert it to a Java Type recursively.
     * This implementation must return <code>null</code>
     */
    public Object toObject();
}
 

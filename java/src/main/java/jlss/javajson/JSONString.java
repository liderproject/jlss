package jlss.javajson;

/**
 * A string in a Json document
 * @author John McCrae
 */
public interface JSONString extends JSON {
    /**
     * Get the value of the string
     */
    public String value();

    /**
     * Read the Json Element completely and convert it to a Java Type recursively
     */
    public String toObject();
}

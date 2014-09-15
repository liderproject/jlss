package jlss.javajson;

/**
 * A boolean value in a Json document
 * @author John McCrae
 */
public interface JSONBoolean extends JSON {
    /**
     * Get the value of the boolean
     */
    public boolean value();

    /**
     * Read the Json Element completely and convert it to a Java Type recursively
     */
    public Boolean toObject();
}

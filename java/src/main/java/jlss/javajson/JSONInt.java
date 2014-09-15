package jlss.javajson;

/**
 * An integer in a Json document
 * @author John McCrae
 */
public interface JSONInt extends JSON {
    /**
     * Get the value of the integer
     */
    public int value();

    /**
     * Read the Json Element completely and convert it to a Java Type recursively
     */
    public Integer toObject();
}

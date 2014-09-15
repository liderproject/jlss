package jlss.javajson;

/**
 * A floating-point value in a Json document
 * @author John McCrae
 */
public interface JSONNumber extends JSON {
    /**
     * Get the value of the double
     */
    public double value();

    /**
     * Read the Json Element completely and convert it to a Java Type recursively
     */
    public Double toObject();
}

package jlss.javajson;

/**
 * A JSON Array
 * @author John McCrae
 */
public interface JSONArray extends JSON {
    /**
     * Returns true if there are more elements in the array
     */
    boolean hasNext();
    /**
     * Get the next object in the array.
     * @throws NoSuchElementException If there is no next element
     */
    JSON next();
    /**
     * Read the Json Element completely and convert it to a Java Type recursively
     */
    java.util.Collection<Object> toObject();
}

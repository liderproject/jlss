package jlss.javajson;

/**
 * A JSON element in a document. Can be either an object, array or value
 */
public interface JSON {
    /**
     * Read the Json Element completely and convert it to a Java Type recursively
     */
     public Object toObject();
}

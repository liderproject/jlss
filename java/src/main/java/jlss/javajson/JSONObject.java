package jlss.javajson;

/**
 * An object in a Json document
 * @author John McCrae
 */
public interface JSONObject extends JSON {
    /**
     * Returns true if there are more fields
     */
    boolean hasNext();
    /**
     * Get the next field
     * @throws NoSuchElementException If there is no next field
     */
    JSONField next();
    /**
     * Read the Json Element completely and convert it to a Java Type recursively
     */
    java.util.Map<String,Object> toObject();

    /**
     * A field in a JSON object
     */
    public static class JSONField {
        /** 
         * The key for the field
         */
        public final String key;
        /**
         * The value of the field
         */
        public final JSON value;

        public JSONField(String key, JSON value) {
            this.key = key;
            this.value = value;
        }

        @Override public int hashCode() {
            int hash = 1;
            hash = hash * 17 + key.hashCode();
            hash = hash * 31 + value.hashCode();
            return hash;
        }

        @Override public String toString() {
            return String.format("\"%s\":%s", key.replaceAll("\"","\\\\\""), value.toString());
        }
    }
} 

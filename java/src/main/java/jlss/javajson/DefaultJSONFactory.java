package jlss.javajson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default in-memory creation of JSON object
 * @author John McCrae
 */
public class DefaultJSONFactory implements JSONFactory {
    private DefaultJSONFactory() { }

    /**
     * Create a new instance
     */
    public static JSONFactory newInstance() { return new DefaultJSONFactory(); }

    static class JSONArrayImpl implements JSONArray {

        private final Iterator<JSON> elements;

        public JSONArrayImpl(Iterator<JSON> elements) {
            this.elements = elements;
        }

        @Override
        public boolean hasNext() {
            return elements.hasNext();
        }

        @Override
        public JSON next() {
            return elements.next();
        }

        @Override
        public Collection<Object> toObject() {
            final HashSet<Object> objs = new HashSet<>();
            while (elements.hasNext()) {
                objs.add(elements.next().toObject());
            }
            return objs;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 37 * hash + Objects.hashCode(this.elements);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final JSONArrayImpl other = (JSONArrayImpl) obj;
            if (!Objects.equals(this.elements, other.elements)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JSONArrayImpl{" + "elements=" + elements + '}';
        }

    }

    static class JSONBooleanImpl implements JSONBoolean {

        private final boolean value;

        public JSONBooleanImpl(boolean value) {
            this.value = value;
        }

        @Override
        public Boolean toObject() {
            return value;
        }

        @Override
        public boolean value() {
            return value;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 73 * hash + (this.value ? 1 : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final JSONBooleanImpl other = (JSONBooleanImpl) obj;
            if (this.value != other.value) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JSONBooleanImpl{" + "value=" + value + '}';
        }

    }

    static class JSONIntImpl implements JSONInt {

        private final int value;

        public JSONIntImpl(int value) {
            this.value = value;
        }

        @Override
        public Integer toObject() {
            return value;
        }

        @Override
        public int value() {
            return value;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 23 * hash + this.value;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final JSONIntImpl other = (JSONIntImpl) obj;
            if (this.value != other.value) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JSONIntImpl{" + "value=" + value + '}';
        }

    }

    static class JSONNullImpl implements JSONNull {

        @Override
        public Object toObject() {
            return null;
        }

        @Override
        public String toString() {
            return "JSONNullImpl{" + '}';
        }
    }
    
    final JSONNull NULL = new JSONNullImpl();
    
    static class JSONNumberImpl implements JSONNumber {
        private final double value;

        public JSONNumberImpl(double value) {
            this.value = value;
        }

        @Override
        public Double toObject() {
            return value;
        }

        @Override
        public double value() {
            return value;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final JSONNumberImpl other = (JSONNumberImpl) obj;
            if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JSONNumberImpl{" + "value=" + value + '}';
        }
        
        
    }
    
    static class JSONObjectImpl implements JSONObject {
        private final Iterator<Map.Entry<String,JSON>> iterator;

        public JSONObjectImpl(Map<String,JSON> map) {
            this.iterator = map.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public JSONField next() {
            final Map.Entry<String, JSON> next = iterator.next();
            return new JSONField(next.getKey(), next.getValue());
        }

        @Override
        public Map<String, Object> toObject() {
            final Map<String, Object> m = new HashMap<>();
            while(iterator.hasNext()) {
                final Map.Entry<String, JSON> next = iterator.next();
                m.put(next.getKey(), next.getValue().toObject());
            }
            return m;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.iterator);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final JSONObjectImpl other = (JSONObjectImpl) obj;
            if (!Objects.equals(this.iterator, other.iterator)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JSONObjectImpl{" + "iterator=" + iterator + '}';
        }
        
    }
    
    static class JSONStringImpl implements JSONString {
        private final String value;

        public JSONStringImpl(String value) {
            this.value = value;
        }

        @Override
        public String toObject() {
            return value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final JSONStringImpl other = (JSONStringImpl) obj;
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JSONStringImpl{" + "value=" + value + '}';
        }
        
        
    }

    @Override
    public JSONArray mkArray(Collection<JSON> elements) {
        return new JSONArrayImpl(elements.iterator());
    }

    @Override
    public JSONObject mkObject(Map<String, JSON> elements) {
        return new JSONObjectImpl(elements);
    }

    @Override
    public JSONBoolean mkBool(boolean value) {
        return new JSONBooleanImpl(value);
    }

    @Override
    public JSONInt mkInt(int value) {
        return new JSONIntImpl(value);
    }

    @Override
    public JSONNull mkNull() {
        return NULL;
    }

    @Override
    public JSONNumber mkNumber(double value) {
        return new JSONNumberImpl(value);
    }

    @Override
    public JSONString mkString(String value) {
        return new JSONStringImpl(value);
    }
    
    class JSONObjectBuilderImpl implements JSONObjectBuilder {
        private final Map<String, JSON> map = new HashMap<String, JSON>();
        private boolean finished = false;

        @Override
        public JSONObjectBuilder put(String name, JSON value) {
            if(finished) throw new IllegalStateException();
            map.put(name, value);
            return this;
        }

        @Override
        public JSONObject end() {
            finished = true;
            return mkObject(map);
        }
    }

    @Override
    public JSONObjectBuilder startObject() {
        return new JSONObjectBuilderImpl();
    }
    
    class JSONArrayBuilderImpl implements JSONArrayBuilder {
        private final List<JSON> set = new ArrayList<JSON>();
        private boolean finished = false;

        @Override
        public JSONArrayBuilder add(JSON value) {
            if(finished) throw new IllegalStateException();
            set.add(value);
            return this;
        }

        @Override
        public JSONArray end() {
            finished = true;
            return mkArray(set);
        }
    }

    @Override
    public JSONArrayBuilder startArray() {
        return new JSONArrayBuilderImpl();
    }
}

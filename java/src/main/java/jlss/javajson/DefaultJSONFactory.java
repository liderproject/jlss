package jlss.javajson;

import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jlss.JsonException;

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

    
/*    private class SimpleJSONReader {
        private static final int BUF_SIZE = 4096;
        private final char[] buf = new char[BUF_SIZE];
        private int pos = 0;
        private int read = 0;
        private final Reader reader;

        public SimpleJSONReader(Reader reader) {
            this.reader = reader;
        }

        public void skipWhitespace() {
            while(read >= 0) {
                while(pos >= read && read >= 0) {
                    pos = 0;
                    read = reader.read(buf);
                }
                if(read < 0) {
                    throw new EOFException();
                }
                if(buf[pos] == ' ' ||
                    buf[pos] == '\t' ||
                    buf[pos] == '\n' ||
                    buf[pos] == '\r') {
                    pos += 1;
                } else {
                    return;
                }
            }
            throw new EOFException();
        }
        
        public char peekChar() {
            while(pos >= read && read >= 0) {
                pos = 0;
                read = reader.read(buf);
            }
            if(read < 0) {
                throw new EOFException();
            }
            return buf[pos];
        }

        public char popChar() {
            char c = peekChar();
            pos++;
            return c;
        }

        public String seekEndOfString() {
            boolean escaped = true;
            final StringBuilder sb = new StringBuilder();
            while(read >= 0) {
                if(pos >= read && read >= 0) {
                    pos = 0;
                    read = reader.read(buf);
                }
                if(read < 0) {
                    throw new EOFException();
                }
                if(buf[pos] == '\\') {
                    if(!escaped) {
                        escaped = true;
                    }
                } else if(buf[pos] == '"' && !escaped) {
                    pos++;
                    return sb.toString();
                }
                escaped = false;
                sb.append(buf[pos++]);
            }
            throw new EOFException();
        }

        public String seekDelimiter() {
            final StringBuilder sb = new StringBuilder();
            while(read >= 0) {
                while(pos >= read && read >= 0) {
                    pos = 0;
                    read = reader.read(buf);
                }
                if(read < 0) {
                    throw new EOFException();
                }
                if(buf[pos] == ',' ||
                        buf[pos] == '}' ||
                        buf[pos] == ']' ||
                        buf[pos] == ' ' ||
                        buf[pos] == '\t' ||
                        buf[pos] == '\n' ||
                        buf[pos] == '\r') {
                    return sb.toString();
                }
                sb.append(buf[pos++]);
            }
            throw new EOFException();
        }


        public JSON read() {
            skipWhitespace();
            char c = popChar();
            if(c == '{') {
                return readObject();
            } else if(c == '[') {
                return readArray();
            } else {
                throw new JsonException("Expected '[' or '{'");
            }
        }

        private JSON readObject() {
            final JSONObjectBuilder builder = startObject();
            while(true) {
                skipWhitespace();
                popChar();
                if(popChar() != '"') {
                    throw new JsonException("Expected field");
                }
                final String fieldName = seekEndOfString();
                skipWhitespace();
                if(popChar() != ':') {
                    throw new JsonException("Expected field separator");
                }
                skipWhitespace();
                final Json json = readAny();
                builder.put(fieldName, json);
                skipWhitespace();
                char c = peekChar();
                if(c == '}') {
                    popChar();
                    return build.end();
                } else if(c != ',') {
                    throw new JsonException("Expected end of field");
                }
                popChar();
            }
            return builder.end();
        }

        private JSON readArray() {
            final JSONArrayBuilder builder = startArray();
            while(true) {
                skipWhitespace();
                final Json json = readAny();
                builder.add(json);
                skipWhitespace();
                char c = peekChar();
                if(c == '}') {
                    popChar();
                    return build.end();
                } else if(c != ',') {
                    throw new JsonException("Expected end of field");
                }
                popChar();
            }
            return builder.end();
        }

        private JSON readAny() {
            skipWhitespace();
            char c = peekChar();
            if(c == '{') {
                popChar();
                return readObject();
            } else if(c == '[') {
                popChar();
                return readObject();
            } else if(c == '"') {
                popChar();
                return mkString(seekEndOfString());
            } else {
                final String value = seekDelimiter();
                if(value.equalsIgnoreCase("true")) {
                    return mkBool(true);
                } else if(value.equalsIgnoreCase("false")) {
                    return mkBool(false);
                } else if(value.equalsIgnoreCase("null")) {
                    return mkNull();
                } else if(value.matches("[+-]?\\d+")) {
                    return mkInt(Integer.parseInt(value));
                } else if(value.matches("[+-]?\\d*\\.\\d*([eE][+-]\\d+)?")) {
                    return mkNumber(Double.parseDouble(value));
                } else {
                    throw new JsonException("Bad value " + value);
                }
            }
        }
    }*/
}

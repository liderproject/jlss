package jlss.javajson;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface JSONSerializer {

    /**
     * Read an object from an input stream
     */
    JSON read(InputStream stream);
    /**
     * Read an object from a reader
     */
    JSON read(Reader reader);

    /**
     * Write an object to an output stream
     */
    void write(JSON json, OutputStream stream);
    
    /**
     * Write an object to a writer
     */
    void write(JSON json, Writer writer);

 
}

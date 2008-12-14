package de.unisb.cs.st.sequitur.output;

import java.io.IOException;
import java.io.ObjectOutputStream;

public interface ObjectWriter<T> {

    public void writeObject(T object, ObjectOutputStream outputStream) throws IOException;

}

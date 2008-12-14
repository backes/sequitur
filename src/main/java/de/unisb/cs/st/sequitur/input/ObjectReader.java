package de.unisb.cs.st.sequitur.input;

import java.io.IOException;
import java.io.ObjectInputStream;

public interface ObjectReader<T> {

    public T readObject(ObjectInputStream inputStream) throws IOException;

}

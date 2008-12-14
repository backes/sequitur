package de.unisb.cs.st.sequitur.output;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SharedOutputGrammar<T> {

    protected final Grammar<T> grammar;
    protected final ObjectWriter<? super T> objectWriter;

    public SharedOutputGrammar() {
        this(null);
    }

    public SharedOutputGrammar(final ObjectWriter<? super T> objectWriter) {
        this(new Grammar<T>(), objectWriter);
    }

    protected SharedOutputGrammar(final Grammar<T> grammar, final ObjectWriter<? super T> objectWriter) {
        if (grammar == null)
            throw new NullPointerException();
        this.grammar = grammar;
        this.objectWriter = objectWriter;
    }

    public void writeOut(final ObjectOutputStream objOut) throws IOException {
        this.grammar.writeOut(objOut, this.objectWriter);
    }

    @Override
    public String toString() {
        return this.grammar.toString();
    }

}

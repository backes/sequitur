/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.output
 *    Class:     SharedOutputGrammar
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/output/SharedOutputGrammar.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a
 * letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
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

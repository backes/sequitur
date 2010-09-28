/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.input
 *    Class:     SharedInputGrammar
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/input/SharedInputGrammar.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a
 * letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package de.unisb.cs.st.sequitur.input;

import java.io.IOException;
import java.io.ObjectInputStream;

public class SharedInputGrammar<T> {

    protected final Grammar<T> grammar;

    protected SharedInputGrammar(final Grammar<T> grammar) {
        if (grammar == null)
            throw new NullPointerException();
        this.grammar = grammar;
    }

    public static SharedInputGrammar<?> readFrom(final ObjectInputStream objIn) throws IOException, ClassNotFoundException {
        return new SharedInputGrammar<Object>(Grammar.readFrom(objIn, null, null));
    }

    public static <T> SharedInputGrammar<T> readFrom(final ObjectInputStream objIn,
            final ObjectReader<? extends T> objectReader) throws IOException, ClassNotFoundException {
        return new SharedInputGrammar<T>(Grammar.readFrom(objIn, objectReader, null));
    }

    public static <T> SharedInputGrammar<T> readFrom(final ObjectInputStream objIn,
            final Class<? extends T> checkInstance) throws IOException, ClassNotFoundException {
        return new SharedInputGrammar<T>(Grammar.readFrom(objIn, null, checkInstance));
    }

    public static <T> SharedInputGrammar<T> readFrom(final ObjectInputStream objIn,
            final ObjectReader<? extends T> objectReader, final Class<? extends T> checkInstance) throws IOException, ClassNotFoundException {
        return new SharedInputGrammar<T>(Grammar.readFrom(objIn, objectReader, checkInstance));
    }

}

/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.input
 *    Class:     SharedInputGrammar
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/input/SharedInputGrammar.java
 *
 * This file is part of the Sequitur library developed by Clemens Hammacher
 * at Saarland University. It has been developed for use in the JavaSlicer
 * tool. See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * Sequitur is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sequitur is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Sequitur. If not, see <http://www.gnu.org/licenses/>.
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

/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.output
 *    Class:     Symbol
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/output/Symbol.java
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
package de.unisb.cs.st.sequitur.output;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Queue;

import de.unisb.cs.st.sequitur.output.Rule.Dummy;

// package-private
abstract class Symbol<T> implements Cloneable {

    public Symbol<T> next = null;
    public Symbol<T> prev = null;
    protected int count;

    protected Symbol(final int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    /**
     * Rawly inserts the given Symbol before this Symbol in the implicit linked list.
     * Does <b>not</b> check any invariants or manipulate the grammar.
     *
     * @param newPrev the new Symbol to insert
     */
    public void insertBefore(final Symbol<T> newPrev) {
        linkTogether(this.prev, newPrev);
        linkTogether(newPrev, this);
    }

    protected static <T> void linkTogether(final Symbol<T> first, final Symbol<T> second) {
        first.next = second;
        second.prev = first;
    }

    public void substituteDigram(final Rule<T> rule, final Grammar<T> grammar) {
        if (!(this.prev instanceof Dummy<?>))
            grammar.removeDigram(this.prev);
        grammar.removeDigram(this);
        if (!(this.next.next instanceof Dummy<?>))
            grammar.removeDigram(this.next);
        this.remove();
        this.next.remove();
        final NonTerminal<T> newSymbol = new NonTerminal<T>(rule);
        this.next.next.insertBefore(newSymbol);

        // if the digram starting at the preceeding symbol is substituted, then
        // the digram starting at this symbol is already checked
        if (!grammar.checkDigram(newSymbol.prev))
            grammar.checkDigram(newSymbol);
    }

    /**
     * Removes this symbol from the implicit linked list.
     * Does no checking of digrams of something else.
     */
    public void remove() {
        linkTogether(this.prev, this.next);
        this.count = 0;
    }

    /**
     * Tries to melt this symbol with it's successor. Only possible if the successor is equal
     * to this symbol.
     * In that case, the count for this symbol is increased by the count of the successor and
     * the successor is removed.
     *
     * @return whether this symbol could be melt with it's successor
     */
    public abstract boolean meltDigram(final Grammar<T> grammar);

    // return a 2-bit header for this symbol
    public abstract int getHeader();

    private int digramHashcode() {
        return this.next == this ? 32*singleHashcode()
            : (singleHashcode() + 31*this.next.singleHashcode());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Symbol<T> clone() {
        try {
            return (Symbol<T>) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException("Symbol should be clonable", e);
        }
    }

    protected abstract int singleHashcode();

    private boolean digramEquals(final Symbol<?> obj) {
        return singleEquals(obj) && this.next.singleEquals(obj.next);
    }

    protected abstract boolean singleEquals(Symbol<?> obj);

    /*
     * WARNING: hashCode() returns a hashCode not only for this symbol, but for the
     * digram of this and the next symbol!
     */
    @Override
    public int hashCode() {
        return digramHashcode();
    }

    /*
     * WARNING: equals() does not only check for equality of the two symbols, but also
     * for the two successor symbols!
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Symbol<?> ? digramEquals((Symbol<?>)obj) : false;
    }

    public abstract void writeOut(final ObjectOutputStream objOut, Grammar<T> grammar,
            ObjectWriter<? super T> objectWriter, Queue<Rule<T>> queue)
        throws IOException;

}

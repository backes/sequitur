/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.output
 *    Class:     NonTerminal
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/output/NonTerminal.java
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
class NonTerminal<T> extends Symbol<T> {

    private final Rule<T> rule;

    public NonTerminal(final Rule<T> rule) {
        super(1);
        assert rule != null;
        this.rule = rule;
        rule.incUseCount();
    }

    @Override
    public void remove() {
        super.remove();
        this.rule.decUseCount();
    }

    public Rule<T> getRule() {
        return this.rule;
    }

    @Override
    protected boolean singleEquals(final Symbol<?> obj) {
        if (obj.getClass() != this.getClass())
            return false;
        final NonTerminal<?> other = (NonTerminal<?>) obj;
        return this.count == other.count && this.rule.equals(other.rule);
    }

    @Override
    protected int singleHashcode() {
        return this.rule.hashCode() + 31*this.count;
    }

    public void checkExpand(final Grammar<T> grammar) {
        assert this.count >= 1;
        if (this.count == 1 && this.rule.getUseCount() == 1) {
            if (!(this.prev instanceof Dummy<?>))
                grammar.removeDigram(this.prev);
            if (!(this.next instanceof Dummy<?>))
                grammar.removeDigram(this);
            remove();
            linkTogether(this.prev, this.rule.dummy.next);
            linkTogether(this.rule.dummy.prev, this.next);
            grammar.checkDigram(this.prev);
            grammar.checkDigram(this.rule.dummy.prev);
        }
    }

    public boolean checkSubstRule(final Grammar<T> grammar) {
        assert this.count >= 1;
        if (this.rule.dummy.next.next != this.rule.dummy)
            return false;

        if (!(this.prev instanceof Dummy<?>))
            grammar.removeDigram(this.prev);
        if (!(this.next instanceof Dummy<?>))
            grammar.removeDigram(this);
        final Symbol<T> newSymbol = this.rule.dummy.next.clone();
        newSymbol.count *= this.count;
        remove();
        this.next.insertBefore(newSymbol);
        if (!grammar.checkDigram(this.prev))
            grammar.checkDigram(this);
        return true;
    }

    @Override
    public boolean meltDigram(final Grammar<T> grammar) {
        if (this.next.getClass() != this.getClass())
            return false;

        final NonTerminal<T> otherNonT = (NonTerminal<T>) this.next;
        if (otherNonT.rule.equals(this.rule)) {
            final boolean hasPrev = !(this.prev instanceof Dummy<?>);
            final boolean hasNextNext = !(otherNonT.next instanceof Dummy<?>);
            if (hasPrev)
                grammar.removeDigram(this.prev);
            if (hasNextNext)
                grammar.removeDigram(otherNonT);
            this.count += otherNonT.count;
            otherNonT.remove();
            if (hasPrev)
                grammar.checkDigram(this.prev);
            if (hasNextNext)
                grammar.checkDigram(this);
            return true;
        }
        return false;
    }

    @Override
    public int getHeader() {
        assert this.count >= 1;
        return this.count == 1 ? 0 : 1;
    }

    @Override
    public void writeOut(final ObjectOutputStream objOut, final Grammar<T> grammar,
            final ObjectWriter<? super T> objectWriter, final Queue<Rule<T>> queue) throws IOException {
        assert this.count >= 1;
        if (this.count != 1) {
            DataOutput.writeInt(objOut, this.count);
        }
        DataOutput.writeLong(objOut, grammar.getRuleNr(this.rule, queue));
    }

    @Override
    protected NonTerminal<T> clone() {
        final NonTerminal<T> clone = (NonTerminal<T>) super.clone();
        clone.rule.incUseCount();
        return clone;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('R').append(this.rule.hashCode());
        assert this.count >= 1;
        if (this.count > 1) {
            sb.append('^').append(this.count);
        }
        return sb.toString();
    }

}

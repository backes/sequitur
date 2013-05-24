/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.input
 *    Class:     NonTerminal
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/input/NonTerminal.java
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


// package-private
class NonTerminal<T> extends Symbol<T> {

    // only needed while reading in the grammar
    private static class RuleReference<T> extends Rule<T> {

        protected final long ruleNr;

        public RuleReference(final long ruleNr) {
            super(null);
            this.ruleNr = ruleNr;
        }

    }

    private final Rule<T> rule;

    public NonTerminal(final Rule<T> rule, final int count) {
        super(count);
        assert rule != null;
        this.rule = rule;
    }

    public Rule<T> getRule() {
        return this.rule;
    }

    @Override
    public long getLength(final boolean ignoreCount) {
        return ignoreCount ? this.rule.getLength() : this.count * this.rule.getLength();
    }

    public NonTerminal<T> substituteRealRules(final Grammar<T> grammar) {
        if (this.rule instanceof RuleReference<?>) {
            return new NonTerminal<T>(grammar.getRule(((RuleReference<T>)this.rule).ruleNr), this.count);
        }
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('R').append(this.rule.hashCode());
        if (this.count != 1) {
            sb.append('^').append(this.count);
        }
        return sb.toString();
    }

    public static <T> NonTerminal<T> readFrom(final ObjectInputStream objIn, final boolean counted) throws IOException {
        final int count = counted ? DataInput.readInt(objIn) : 1;
        final long ruleNr = DataInput.readLong(objIn);
        return new NonTerminal<T>(new RuleReference<T>(ruleNr), count);
    }

}

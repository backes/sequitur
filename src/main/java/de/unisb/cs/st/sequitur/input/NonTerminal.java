/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.input
 *    Class:     NonTerminal
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/input/NonTerminal.java
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

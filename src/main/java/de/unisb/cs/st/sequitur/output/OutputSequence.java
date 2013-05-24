/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.output
 *    Class:     OutputSequence
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/output/OutputSequence.java
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
import java.util.Set;

public class OutputSequence<T> {

    private final Grammar<T> grammar;
    protected final Rule<T> firstRule;
    private final ObjectWriter<? super T> objectWriter;
    private T lastValue = null;
    private int lastValueCount = 0;

    public OutputSequence() {
        this(new Rule<T>(false), new Grammar<T>(), null);
    }

    public OutputSequence(final SharedOutputGrammar<T> g) {
        this(new Rule<T>(false), g.grammar, g.objectWriter);
    }

    public OutputSequence(final ObjectWriter<? super T> objectWriter) {
        this(new Rule<T>(false), new Grammar<T>(), objectWriter);
    }

    private OutputSequence(final Rule<T> firstRule, final Grammar<T> grammar,
            final ObjectWriter<? super T> objectWriter) {
        this.grammar = grammar;
        this.firstRule = firstRule;
        this.objectWriter = objectWriter;
        grammar.newSequence(this);
    }

    public void append(final T obj) {
        if (this.lastValueCount == 0) {
            this.lastValue = obj;
            this.lastValueCount = 1;
        } else if (this.lastValue == null ? obj == null : this.lastValue.equals(obj)) {
            if (++this.lastValueCount == Integer.MAX_VALUE) {
                this.firstRule.append(new Terminal<T>(this.lastValue, this.lastValueCount), this.grammar);
                this.lastValue = null;
                this.lastValueCount = 0;
            }
        } else {
            this.firstRule.append(new Terminal<T>(this.lastValue, this.lastValueCount), this.grammar);
            this.lastValue = obj;
            this.lastValueCount = 1;
        }
    }

    public long getStartRuleNumber() {
        return this.grammar.getRuleNr(this.firstRule);
    }

    public void writeOut(final ObjectOutputStream objOut, final boolean includeGrammar) throws IOException {
        flush();
        if (includeGrammar)
            writeOutGrammar(objOut);
        DataOutput.writeLong(objOut, getStartRuleNumber());
    }

    public void writeOutGrammar(final ObjectOutputStream objOut) throws IOException {
        flush();
        this.grammar.writeOut(objOut, this.objectWriter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.firstRule.dummy.next != this.firstRule.dummy) {
            sb.append(this.firstRule.dummy.next);
            for (Symbol<T> s = this.firstRule.dummy.next.next; s != this.firstRule.dummy; s = s.next)
                sb.append(' ').append(s);
        }
        if (this.lastValueCount > 0) {
            sb.append("  + ").append(this.lastValueCount).append('x').append(this.lastValue);
        }

        Set<Rule<T>> rules = this.firstRule.getUsedRules();
        for (Rule<T> r: rules)
            sb.append(System.getProperty("line.separator")).append(r);
        return sb.toString();
    }

    public void ensureInvariants() {
        this.firstRule.ensureInvariants(this.grammar);
    }

    public void flush() {
        if (this.lastValueCount > 0) {
            this.firstRule.append(new Terminal<T>(this.lastValue, this.lastValueCount), this.grammar);
            this.lastValue = null;
            this.lastValueCount = 0;
        }
    }

}

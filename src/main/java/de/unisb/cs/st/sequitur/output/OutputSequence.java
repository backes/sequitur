/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.output
 *    Class:     OutputSequence
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/output/OutputSequence.java
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
        finish();
        if (includeGrammar)
            writeOutGrammar(objOut);
        DataOutput.writeLong(objOut, getStartRuleNumber());
    }

    public void writeOutGrammar(final ObjectOutputStream objOut) throws IOException {
        finish();
        this.grammar.writeOut(objOut, this.objectWriter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.firstRule.dummy.next != this.firstRule.dummy) {
            sb.append(this.firstRule.dummy.next);
            for (Symbol<T> s = this.firstRule.dummy.next.next; s != this.firstRule.dummy; s = s.next)
                sb.append(" ").append(s);
        }
        if (this.lastValueCount > 0) {
            sb.append("  + ").append(this.lastValueCount).append("x").append(this.lastValue);
        }

        Set<Rule<T>> rules = this.firstRule.getUsedRules();
        for (Rule<T> r: rules)
            sb.append(System.getProperty("line.separator")).append(r);
        return sb.toString();
    }

    public void ensureInvariants() {
        this.firstRule.ensureInvariants(this.grammar);
    }

    public void finish() {
        if (this.lastValueCount > 0) {
            this.firstRule.append(new Terminal<T>(this.lastValue, this.lastValueCount), this.grammar);
            this.lastValue = null;
            this.lastValueCount = 0;
        }
    }

}

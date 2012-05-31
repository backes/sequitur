/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.input
 *    Class:     InputSequence
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/input/InputSequence.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a
 * letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package de.unisb.cs.st.sequitur.input;

import gnu.trove.map.TObjectLongMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import de.hammacher.util.LongHolder;

public class InputSequence<T> implements Iterable<T> {

    public static class Itr<T> implements ListIterator<T> {

        private long pos;
        private final long seqLength;
        private final List<Rule<T>> ruleStack = new ArrayList<Rule<T>>(2);
        private int[] rulePos;
        private int[] count;

        public Itr(final long position, final Rule<T> firstRule) {
            this.pos = position;
            this.seqLength = firstRule.getLength();
            if (position == 0) {
                if (this.seqLength > 0) {
                    Rule<T> rule = firstRule;
                    while (true) {
                        this.ruleStack.add(rule);
                        final Symbol<T> sym = rule.symbols[0];
                        if (sym instanceof Terminal<?>)
                            break;
                        rule = ((NonTerminal<T>)sym).getRule();
                    }
                    final int depth = Math.max(Integer.highestOneBit(this.ruleStack.size()-1)*2, 2);
                    this.rulePos = new int[depth];
                    this.count = new int[depth];
                }
            } else if (position == firstRule.getLength()) {
                Rule<T> rule = firstRule;
                this.rulePos = new int[2];
                this.count = new int[2];
                int i = 0;
                while (true) {
                    this.ruleStack.add(rule);
                    final int ruleSymLength = rule.symbols.length;
                    final Symbol<T> sym = rule.symbols[ruleSymLength - 1];
                    if (this.rulePos.length == i) {
                        int[] newRulePos = new int[2*i];
                        System.arraycopy(this.rulePos, 0, newRulePos, 0, i);
                        this.rulePos = newRulePos;
                        int[] newCount = new int[2*i];
                        System.arraycopy(this.count, 0, newCount, 0, i);
                        this.count = newCount;
                    }
                    if (sym instanceof Terminal<?>) {
                        // move behind the last symbol:
                        this.rulePos[i] = ruleSymLength;
                        break;
                    }
                    this.rulePos[i] = ruleSymLength - 1;
                    this.count[i++] = sym.count - 1;
                    rule = ((NonTerminal<T>)sym).getRule();
                }
            } else {
                Rule<T> rule = firstRule;
                this.rulePos = new int[2];
                this.count = new int[2];
                long after = 0;
                int i = 0;
                while (true) {
                    this.ruleStack.add(rule);
                    final LongHolder afterHolder = new LongHolder(0);
                    final int ruleOffset = position == after ? 0 : rule.findOffset(position - after, afterHolder);
                    after += afterHolder.longValue();
                    if (this.rulePos.length == i) {
                        int[] newRulePos = new int[2*i];
                        System.arraycopy(this.rulePos, 0, newRulePos, 0, i);
                        this.rulePos = newRulePos;
                        int[] newCount = new int[2*i];
                        System.arraycopy(this.count, 0, newCount, 0, i);
                        this.count = newCount;
                    }
                    this.rulePos[i] = ruleOffset;
                    final Symbol<T> sym = rule.symbols[ruleOffset];
                    if (sym.count > 1) {
                        final long oneLength = sym.getLength(true);
                        this.count[i] = (int) ((position - after) / oneLength);
                        if (this.count[i] > 0)
                            after += this.count[i] * oneLength;
                        assert this.count[i] >= 0 && this.count[i] < sym.count;
                    }
                    if (sym instanceof Terminal<?>)
                        break;
                    rule = ((NonTerminal<T>)sym).getRule();
                    ++i;
                }
                assert after == position;
            }
        }

        public void add(final T e) {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return this.pos != this.seqLength;
        }

        public boolean hasPrevious() {
            return this.pos != 0;
        }

        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            int depth = this.ruleStack.size()-1;
            Symbol<T>[] ruleSymbols = this.ruleStack.get(depth).symbols;
            Symbol<T> sym = ruleSymbols[this.rulePos[depth]];
            final T value = ((Terminal<T>)sym).getValue();

            while (true) {
                if (this.count[depth]+1 < sym.count) {
                    ++this.count[depth];
                    break;
                }
                if (this.rulePos[depth] != ruleSymbols.length - 1) {
                    sym = ruleSymbols[++this.rulePos[depth]];
                    this.count[depth] = 0;
                    break;
                }
                if (depth == 0) {
                    assert this.pos == this.seqLength - 1;
                    ++this.rulePos[0];
                    this.count[0] = 0;
                    ++this.pos;
                    return value;
                }
                this.ruleStack.remove(depth);
                ruleSymbols = this.ruleStack.get(--depth).symbols;
                sym = ruleSymbols[this.rulePos[depth]];
            }
            while (sym instanceof NonTerminal<?>) {
                final Rule<T> rule = ((NonTerminal<T>)sym).getRule();
                this.ruleStack.add(rule);
                if (this.rulePos.length == ++depth) {
                    int[] newRulePos = new int[2*depth];
                    System.arraycopy(this.rulePos, 0, newRulePos, 0, depth);
                    this.rulePos = newRulePos;
                    int[] newCount = new int[2*depth];
                    System.arraycopy(this.count, 0, newCount, 0, depth);
                    this.count = newCount;
                }
                this.rulePos[depth] = 0;
                this.count[depth] = 0;
                sym = rule.symbols[0];
            }
            ++this.pos;
            return value;
        }

        public int nextIndex() {
            if (this.pos >= Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
            return (int) this.pos;
        }

        public T previous() {
            if (!hasPrevious())
                throw new NoSuchElementException();

            int depth = this.ruleStack.size()-1;

            Symbol<T> sym;
            while (true) {
                if (this.count[depth] != 0) {
                    --this.count[depth];
                    sym = this.ruleStack.get(depth).symbols[this.rulePos[depth]];
                    break;
                }
                if (this.rulePos[depth] != 0) {
                    sym = this.ruleStack.get(depth).symbols[--this.rulePos[depth]];
                    this.count[depth] = sym.count-1;
                    break;
                }
                this.ruleStack.remove(depth--);
            }
            while (sym instanceof NonTerminal<?>) {
                final Rule<T> rule = ((NonTerminal<T>)sym).getRule();
                this.ruleStack.add(rule);
                if (this.rulePos.length == ++depth) {
                    int[] newRulePos = new int[2*depth];
                    System.arraycopy(this.rulePos, 0, newRulePos, 0, depth);
                    this.rulePos = newRulePos;
                    int[] newCount = new int[2*depth];
                    System.arraycopy(this.count, 0, newCount, 0, depth);
                    this.count = newCount;
                }
                this.rulePos[depth] = rule.symbols.length - 1;
                sym = rule.symbols[this.rulePos[depth]];
                this.count[depth] = sym.count-1;
            }
            --this.pos;
            return ((Terminal<T>)sym).getValue();
        }

        public int previousIndex() {
            if (this.pos > Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
            return (int) (this.pos-1);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(final T e) {
            throw new UnsupportedOperationException();
        }

    }

    private final Rule<T> firstRule;

    private InputSequence(final Rule<T> firstRule) {
        this.firstRule = firstRule;
    }

    public InputSequence(final long startRuleNumber, final SharedInputGrammar<T> grammar) {
        this(getStartRule(startRuleNumber, grammar));
    }

    private static <T> Rule<T> getStartRule(final long startRuleNumber, final SharedInputGrammar<T> grammar) {
        final Rule<T> rule = grammar.grammar.getRule(startRuleNumber);
        if (rule == null)
            throw new IllegalArgumentException("Unknown start rule number");
        return rule;
    }

    public ListIterator<T> iterator() {
        return iterator(0);
    }

    public ListIterator<T> iterator(final long position) {
        return new Itr<T>(position, this.firstRule);
    }

    public long getLength() {
        return this.firstRule.getLength();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.firstRule.symbols.length > 0) {
            sb.append(this.firstRule.symbols[0]);
            for (int i = 1; i < this.firstRule.symbols.length; ++i)
                sb.append(' ').append(this.firstRule.symbols[i]);
        }

        final TObjectLongMap<Rule<T>> rules = this.firstRule.getUsedRules();
        @SuppressWarnings("unchecked")
        Rule<T>[] keys = rules.keys((Rule<T>[]) new Rule<?>[rules.size()]);
        // sort by number of uses descending, then length (in symbols) descending
        Arrays.sort(keys, new Comparator<Rule<T>>() {
            public int compare(Rule<T> r1, Rule<T> r2) {
                long numUses1 = rules.get(r1);
                long numUses2 = rules.get(r2);
                if (numUses1 != numUses2)
                    return numUses1 > numUses2 ? -1 : 1;
                long length1 = r1.symbols.length;
                long length2 = r2.symbols.length;
                return length1 == length2 ? 0 : length1 > length2 ? -1 : 1;
            }
        });
        final String newline = System.getProperty("line.separator");
        char[] blanks = new char[9];
        Arrays.fill(blanks, ' ');
        for (Rule<T> rule : keys) {
            long numUses = rules.get(rule);
            assert numUses >= 1;
            byte fillBlanks = numUses < 10       ? 7
                            : numUses < 100      ? 6
                            : numUses < 1000     ? 5
                            : numUses < 10000    ? 4
                            : numUses < 100000   ? 3
                            : numUses < 1000000  ? 2
                            : numUses < 10000000 ? 1
                            : (byte)0;
            sb.append(newline).append(numUses).append('x').append(blanks,  0, fillBlanks + 2).append(rule);
            /* print the whole expansion of the rule: */
            /*
            sb.append(newline).append("==> ");
            Itr<T> itr = new Itr<T>(0, rule);
            while (itr.hasNext())
                sb.append(itr.next());
            */
        }

        return sb.toString();
    }

    public static InputSequence<?> readFrom(final ObjectInputStream objIn)
            throws IOException, ClassNotFoundException {
        return readFrom(objIn, SharedInputGrammar.readFrom(objIn));
    }

    public static <T> InputSequence<T> readFrom(final ObjectInputStream objIn,
            final Class<? extends T> checkInstance) throws IOException, ClassNotFoundException {
        return readFrom(objIn, SharedInputGrammar.readFrom(objIn, checkInstance));
    }

    public static <T> InputSequence<T> readFrom(final ObjectInputStream objIn,
            final ObjectReader<? extends T> objectReader) throws IOException, ClassNotFoundException {
        return readFrom(objIn, SharedInputGrammar.readFrom(objIn, objectReader));
    }

    public static <T> InputSequence<T> readFrom(final ObjectInputStream objIn,
            final ObjectReader<? extends T> objectReader, final Class<? extends T> checkInstance)
            throws IOException, ClassNotFoundException {
        return readFrom(objIn, SharedInputGrammar.readFrom(objIn, objectReader, checkInstance));
    }

    public static <T> InputSequence<T> readFrom(final ObjectInputStream objIn, final SharedInputGrammar<T> sharedGrammar) throws IOException {
        if (sharedGrammar == null)
            throw new NullPointerException();
        final long startRuleNr = DataInput.readLong(objIn);
        final Rule<T> rule = sharedGrammar.grammar.getRule(startRuleNr);
        if (rule == null)
            throw new IOException("Unknown rule number");
        return new InputSequence<T>(rule);
    }

}

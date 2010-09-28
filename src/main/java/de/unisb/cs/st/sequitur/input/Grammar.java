/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.input
 *    Class:     Grammar
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/input/Grammar.java
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

import de.hammacher.util.LongArrayList;

// package-private
class Grammar<T> {

    private final LongArrayList<Rule<T>> rules;

    protected Grammar(final LongArrayList<Rule<T>> rules) {
        this.rules = rules;
    }

    public static <T> Grammar<T> readFrom(final ObjectInputStream objIn, final ObjectReader<? extends T> objectReader,
            final Class<? extends T> checkInstance) throws IOException, ClassNotFoundException {
        final LongArrayList<Rule<T>> rules = Rule.readAll(objIn, objectReader, checkInstance);
        final Grammar<T> grammar = new Grammar<T>(rules);
        for (final Rule<T> rule: rules)
            rule.substituteRealRules(grammar);
        boolean ready = false;
        while (!ready) {
            ready = true;
            for (final Rule<T> rule: rules)
                if (!rule.computeLength())
                    ready = false;
        }
        return grammar;
    }

    public Rule<T> getRule(final long ruleNr) {
        if (ruleNr < 0 || ruleNr >= this.rules.longSize())
            return null;
        return this.rules.get(ruleNr);
    }

}

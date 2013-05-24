/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.input
 *    Class:     Grammar
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/input/Grammar.java
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

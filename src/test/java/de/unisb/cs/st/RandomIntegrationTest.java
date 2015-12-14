/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st
 *    Class:     RandomIntegrationTest
 *    Filename:  sequitur/src/test/java/de/unisb/cs/st/RandomIntegrationTest.java
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
package de.unisb.cs.st;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.unisb.cs.st.sequitur.input.InputSequence;
import de.unisb.cs.st.sequitur.input.SharedInputGrammar;
import de.unisb.cs.st.sequitur.output.OutputSequence;
import de.unisb.cs.st.sequitur.output.SharedOutputGrammar;


@RunWith(Parameterized.class)
public class RandomIntegrationTest {

    private final static int numTests = 2000;

    private final int length;
    private final long seed;


    public RandomIntegrationTest(int length, long seed) {
        this.length = length;
        this.seed = seed;
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        Collection<Object[]> params = new AbstractList<Object[]>() {

            private final Random random = new Random();

            @Override
            public Object[] get(int index) {
                return new Object[] { this.random.nextInt(1+index / 10), this.random.nextLong() };
            }

            @Override
            public int size() {
                return numTests;
            }

        };
        return params;
    }

    @Test
    public void privateGrammar() {
        try {
            Random rand = new Random(this.seed);
            OutputSequence<Integer> outSeq = new OutputSequence<Integer>();
            int[] ints = new int[this.length];
            for (int i = 0; i < this.length; ++i) {
                ints[i] = rand.nextInt(1+this.length/20);
                outSeq.append(ints[i]);
            }
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            outSeq.writeOut(objOut, true);
            objOut.close();
            byte[] bytes = byteOut.toByteArray();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            InputSequence<Integer> inSeq = InputSequence.readFrom(objIn, Integer.class);

            assertEquals(this.length, inSeq.getLength());
            Iterator<Integer> inIt = inSeq.iterator();
            for (int i = 0; i < this.length; ++i) {
                assertTrue(inIt.hasNext());
                assertEquals(ints[i], inIt.next().intValue());
            }
            assertFalse(inIt.hasNext());

        } catch (Throwable e) {
            throw new RuntimeException("Exception in sequitur test for length " + this.length + "; seed " + this.seed, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sharedGrammar() {
        try {
            Random rand = new Random(this.seed);
            int numSequences = this.length / 10;
            SharedOutputGrammar<Integer> sharedGrammar = new SharedOutputGrammar<Integer>();

            OutputSequence<Integer>[] outSeqs = (OutputSequence<Integer>[]) new OutputSequence<?>[numSequences];
            for (int k = 0; k < numSequences; ++k) {
                outSeqs[k] = new OutputSequence<Integer>(sharedGrammar);
            }
            int[][] ints = new int[numSequences][this.length];

            long overall = numSequences * this.length;
            int[] written = new int[numSequences];

            for (long i = 0; i < overall; ) {
            	int seq = rand.nextInt(numSequences);
            	if (written[seq] < this.length) {
                    ints[seq][written[seq]] = rand.nextInt(this.length/5);
                    outSeqs[seq].append(ints[seq][written[seq]]);
                    ++written[seq];
                    ++i;
            	}
            }
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            sharedGrammar.writeOut(objOut);
            for (int k = 0; k < numSequences; ++k)
                outSeqs[k].writeOut(objOut, false);
            objOut.close();
            byte[] bytes = byteOut.toByteArray();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            InputSequence<Integer>[] inSeqs = (InputSequence<Integer>[]) new InputSequence<?>[numSequences];
            SharedInputGrammar<Integer> inGrammar = (SharedInputGrammar<Integer>)SharedInputGrammar.readFrom(objIn);
            for (int k = 0; k < numSequences; ++k) {
                inSeqs[k] = InputSequence.readFrom(objIn, inGrammar);
            }

            assertTrue("expected EOF", objIn.read() == -1);

            for (int k = 0; k < numSequences; ++k) {
                assertEquals("(internal check) sequence length", this.length, written[k]);
                assertEquals("sequence length", this.length, inSeqs[k].getLength());
                Iterator<Integer> inIt = inSeqs[k].iterator();
                for (int i = 0; i < this.length; ++i) {
                    assertTrue("iterator should have more elements", inIt.hasNext());
                    assertEquals("value in sequence", ints[k][i], inIt.next().intValue());
                }
                assertFalse(inIt.hasNext());
            }

        } catch (Throwable e) {
            throw new RuntimeException("Exception in sequitur test with shared grammar for length " + this.length + "; seed " + this.seed, e);
        }
    }

}

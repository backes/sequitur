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
import de.unisb.cs.st.sequitur.output.OutputSequence;


@RunWith(Parameterized.class)
public class RandomIntegrationTest {

    private final static int numTests = 10000;

    private final int length;
    private final long seed;


    public RandomIntegrationTest(int length, long seed) {
        super();
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
    public void test() {
        try {
            Random rand = new Random(this.seed);
            OutputSequence<Integer> outSeq = new OutputSequence<Integer>();
            int[] ints = new int[this.length];
            for (int i = 0; i < this.length; ++i) {
                ints[i] = rand.nextInt(this.length);
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

}

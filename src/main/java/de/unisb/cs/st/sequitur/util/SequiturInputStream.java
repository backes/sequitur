/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.util
 *    Class:     SequiturInputStream
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/util/SequiturInputStream.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a
 * letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package de.unisb.cs.st.sequitur.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ListIterator;

import de.unisb.cs.st.sequitur.input.InputSequence;
import de.unisb.cs.st.sequitur.input.ObjectReader;


public class SequiturInputStream extends InputStream {

    private static enum ByteReader implements ObjectReader<Object> {

        asByte(false),
        asChar(true);

        private boolean printAsChar;

        ByteReader(boolean printAsChar) {
            this.printAsChar = printAsChar;
        }

        public Object readObject(ObjectInputStream inputStream)
                throws IOException {
            byte b = inputStream.readByte();
            if (this.printAsChar)
                return Character.valueOf((char) b);
            return Byte.valueOf(b);
        }

    }


    private final InputSequence<Object> inSeq;
    private ListIterator<Object> inputIterator;

    public SequiturInputStream(InputStream in, boolean printAsCharacter) throws IOException {
        super();
        ObjectInputStream objIn = new ObjectInputStream(in);
        try {
            this.inSeq = InputSequence.readFrom(objIn, printAsCharacter ? ByteReader.asChar : ByteReader.asByte);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.toString());
        }
        this.inputIterator = this.inSeq.iterator();
    }

    @Override
    public int read() throws IOException {
        if (!this.inputIterator.hasNext())
            return -1;
        Object nextObj = this.inputIterator.next();
        if (nextObj instanceof Byte)
            return ((Byte) nextObj).intValue();
        return ((Character) nextObj).charValue();
    }

    @Override
    public int available() throws IOException {
        int index = this.inputIterator.nextIndex();
        if (index == Integer.MAX_VALUE)
            return -1;
        return (int) Math.min(Integer.MAX_VALUE, this.inSeq.getLength() - index);
    }

    @Override
    public long skip(long n) throws IOException {
        int index = this.inputIterator.nextIndex();
        if (index == Integer.MAX_VALUE)
            return super.skip(n);
        this.inputIterator = this.inSeq.iterator(index + n);
        return n;
    }

    @Override
    public String toString() {
        return this.inSeq.toString();
    }

}

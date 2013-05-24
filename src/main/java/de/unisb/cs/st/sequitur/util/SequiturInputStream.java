/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.util
 *    Class:     SequiturInputStream
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/util/SequiturInputStream.java
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

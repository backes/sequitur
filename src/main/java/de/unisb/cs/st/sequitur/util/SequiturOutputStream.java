/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.util
 *    Class:     SequiturOutputStream
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/util/SequiturOutputStream.java
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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import de.unisb.cs.st.sequitur.output.ObjectWriter;
import de.unisb.cs.st.sequitur.output.OutputSequence;


public class SequiturOutputStream extends FilterOutputStream {

    private static enum ByteWriter implements ObjectWriter<Object> {

        instance;

        public void writeObject(Object object, ObjectOutputStream outputStream)
                throws IOException {
            byte b;
            if (object instanceof Byte)
                b = ((Byte) object).byteValue();
            else
                b = (byte) ((Character) object).charValue();
            outputStream.writeByte(b);
        }

    }


    private final OutputSequence<Object> outSeq;
    private final boolean printAsChars;

    public SequiturOutputStream(OutputStream out, boolean printAsChars) {
        super(out);
        this.outSeq = new OutputSequence<Object>(ByteWriter.instance);
        this.printAsChars = printAsChars;
    }

    @Override
    public void write(int b) throws IOException {
        Object obj;
        if (this.printAsChars)
            obj = Character.valueOf((char) (b & 0xff));
        else
            obj = Byte.valueOf((byte) b);
        this.outSeq.append(obj);
    }

    @Override
    public void flush() throws IOException {
        this.outSeq.flush();
    }

    @Override
    public void close() throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(this.out);
        this.outSeq.writeOut(objOut, true);
        objOut.close();
        super.close();
    }

    @Override
    public String toString() {
        return this.outSeq.toString();
    }

}

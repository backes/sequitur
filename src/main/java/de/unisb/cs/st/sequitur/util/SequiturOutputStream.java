/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.util
 *    Class:     SequiturOutputStream
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/util/SequiturOutputStream.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a
 * letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
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

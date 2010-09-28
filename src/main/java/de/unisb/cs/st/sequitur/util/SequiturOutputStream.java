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

    private static final class ByteWriter implements ObjectWriter<Byte> {

        public static final ByteWriter instance = new ByteWriter();

        public void writeObject(Byte object, ObjectOutputStream outputStream)
                throws IOException {
            outputStream.writeByte(object.intValue());
        }

    }


    private final OutputSequence<Byte> outSeq;

    public SequiturOutputStream(OutputStream out) {
        super(out);
        this.outSeq = new OutputSequence<Byte>(ByteWriter.instance);
    }

    @Override
    public void write(int b) throws IOException {
        this.outSeq.append(Byte.valueOf((byte)b));
    }

    @Override
    public void close() throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(this.out);
        this.outSeq.writeOut(objOut, true);
        objOut.close();
        super.close();
    }

}

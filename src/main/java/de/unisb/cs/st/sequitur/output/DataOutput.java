/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.output
 *    Class:     DataOutput
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/output/DataOutput.java
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
package de.unisb.cs.st.sequitur.output;

import java.io.IOException;
import java.io.OutputStream;

// package-private
class DataOutput {

    private static final byte MAGIC_1BYTE      = (byte) 255;
    private static final byte MAGIC_2BYTES     = (byte) 254;
    private static final byte MAGIC_3BYTES     = (byte) 253;
    private static final byte MAGIC_4BYTES     = (byte) 252;
    private static final byte MAGIC_5BYTES     = (byte) 251;
    private static final byte MAGIC_6BYTES     = (byte) 250;
    private static final byte MAGIC_7BYTES     = (byte) 249;
    private static final byte MAGIC_8BYTES     = (byte) 248;
    private static final int  LOWER_8_BITS  = 0xff;
    private static final int  LOWER_16_BITS = 0xffff;
    private static final int  LOWER_24_BITS = 0xffffff;
    private static final long LOWER_32_BITS = 0xffffffffL;
    private static final long LOWER_40_BITS = 0xffffffffffL;
    private static final long LOWER_48_BITS = 0xffffffffffffL;
    private static final long LOWER_56_BITS = 0xffffffffffffffL;

    private DataOutput() {
        // cannot be instantiated
    }

    public static void writeInt(final OutputStream out, final int value) throws IOException {
        if ((value & LOWER_8_BITS) == value) {
            if (value >= 252)
                out.write(MAGIC_1BYTE);
            out.write(value);
        } else if ((value & LOWER_16_BITS) == value) {
            out.write(MAGIC_2BYTES);
            out.write(value >>> 8);
            out.write(value);
        } else if ((value & LOWER_24_BITS) == value) {
            out.write(MAGIC_3BYTES);
            out.write(value >>> 16);
            out.write(value >>> 8);
            out.write(value);
        } else {
            out.write(MAGIC_4BYTES);
            out.write(value >>> 24);
            out.write(value >>> 16);
            out.write(value >>> 8);
            out.write(value);
        }
    }

    public static void writeLong(final OutputStream str, final long value) throws IOException {
        if ((value & LOWER_8_BITS) == value) {
            if (value >= 248)
                str.write(MAGIC_1BYTE);
            str.write((int) value);
        } else if ((value & LOWER_16_BITS) == value) {
            str.write(MAGIC_2BYTES);
            str.write((int)(value >>> 8));
            str.write((int)value);
        } else if ((value & LOWER_24_BITS) == value) {
            str.write(MAGIC_3BYTES);
            str.write((int)(value >>> 16));
            str.write((int)(value >>> 8));
            str.write((int)value);
        } else if ((value & LOWER_32_BITS) == value) {
            str.write(MAGIC_4BYTES);
            str.write((int)(value >>> 24));
            str.write((int)(value >>> 16));
            str.write((int)(value >>> 8));
            str.write((int)value);
        } else if ((value & LOWER_40_BITS) == value) {
            str.write(MAGIC_5BYTES);
            str.write((int)(value >>> 32));
            str.write((int)(value >>> 24));
            str.write((int)(value >>> 16));
            str.write((int)(value >>> 8));
            str.write((int)value);
        } else if ((value & LOWER_48_BITS) == value) {
            str.write(MAGIC_6BYTES);
            str.write((int)(value >>> 40));
            str.write((int)(value >>> 32));
            str.write((int)(value >>> 24));
            str.write((int)(value >>> 16));
            str.write((int)(value >>> 8));
            str.write((int)value);
        } else if ((value & LOWER_56_BITS) == value) {
            str.write(MAGIC_7BYTES);
            str.write((int)(value >>> 48));
            str.write((int)(value >>> 40));
            str.write((int)(value >>> 32));
            str.write((int)(value >>> 24));
            str.write((int)(value >>> 16));
            str.write((int)(value >>> 8));
            str.write((int)value);
        } else {
            str.write(MAGIC_8BYTES);
            str.write((int)(value >>> 56));
            str.write((int)(value >>> 48));
            str.write((int)(value >>> 40));
            str.write((int)(value >>> 32));
            str.write((int)(value >>> 24));
            str.write((int)(value >>> 16));
            str.write((int)(value >>> 8));
            str.write((int)value);
        }
    }

}

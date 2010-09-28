/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.input
 *    Class:     Terminal
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/input/Terminal.java
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


// package-private
class Terminal<T> extends Symbol<T> {

    private final T value;

    public Terminal(final T value, final int count) {
        super(count);
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public long getLength(final boolean ignoreCount) {
        return ignoreCount ? 1 : this.count;
    }

    @Override
    public String toString() {
        if (this.count == 1)
            return String.valueOf(this.value);

        return new StringBuilder().append(this.value)
            .append('^').append(this.count).toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> Terminal<T> readFrom(final ObjectInputStream objIn, final boolean counted,
            final ObjectReader<? extends T> objectReader, final Class<? extends T> checkInstance) throws IOException, ClassNotFoundException {
        final int count = counted ? DataInput.readInt(objIn) : 1;
        if (objectReader == null) {
            final Object value = objIn.readObject();
            if (checkInstance != null && !checkInstance.isInstance(value))
                throw new ClassCastException(value.getClass().getName()+" not assignment-compatible with "+checkInstance.getName());
            return new Terminal<T>((T) value, count);
        }
        final T value = objectReader.readObject(objIn);
        return new Terminal<T>(value, count);
    }

}

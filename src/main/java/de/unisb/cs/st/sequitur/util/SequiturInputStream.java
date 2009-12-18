package de.unisb.cs.st.sequitur.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ListIterator;

import de.unisb.cs.st.sequitur.input.InputSequence;
import de.unisb.cs.st.sequitur.input.ObjectReader;


public class SequiturInputStream extends InputStream {

    private static final class ByteReader implements ObjectReader<Byte> {

        public static final ByteReader instance = new ByteReader();

        public Byte readObject(ObjectInputStream inputStream)
                throws IOException {
            return Byte.valueOf(inputStream.readByte());
        }

    }


    private final InputSequence<Byte> inSeq;
    private ListIterator<Byte> inputIterator;

    public SequiturInputStream(InputStream in) throws IOException {
        super();
        ObjectInputStream objIn = new ObjectInputStream(in);
        try {
            this.inSeq = InputSequence.readFrom(objIn, ByteReader.instance);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.toString());
        }
        this.inputIterator = this.inSeq.iterator();
    }

    @Override
    public int read() throws IOException {
        return this.inputIterator.hasNext() ? this.inputIterator.next() : -1;
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

}

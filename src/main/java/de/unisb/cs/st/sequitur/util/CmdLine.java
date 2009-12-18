package de.unisb.cs.st.sequitur.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class CmdLine {

    public static void main(String[] args) {
        String input = null, output = null;
        boolean decompress = false;
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if ("-i".equals(arg)) {
                ensureMore(args, i, 1);
                input = args[++i];
            } else if ("-o".equals(arg)) {
                ensureMore(args, i, 1);
                output = args[++i];
            } else if ("-d".equals(arg)) {
                decompress = true;
            } else if ("-h".equals(arg)) {
                help(System.out);
                System.exit(-1);
            }
        }

        if (input == null || output == null) {
            help(System.err);
            System.exit(-1);
        }

        File inputFile = new File(input);
        if (!inputFile.exists()) {
            System.err.format("Input file \"%s\" does not exist!%n",
                input);
            System.exit(-1);
        }

        File outputFile = new File(output);
        FileInputStream fIn = null;
        FileOutputStream fOut = null;
        try {
            fIn = new FileInputStream(inputFile);
            fOut = new FileOutputStream(outputFile);
            if (decompress)
                decompress(fIn, fOut);
            else
                compress(fIn, fOut);
        } catch (IOException e) {
            System.err.format("An error occured while %scompressing: %s%n",
                decompress ? "de" : "", e);
            System.exit(-1);
        } finally {
            if (fIn != null)
                try {
                    fIn.close();
                } catch (IOException e) { /* ignore */ }
            if (fOut != null)
                try {
                    fOut.close();
                } catch (IOException e) { /* ignore */ }
        }
    }

    private static void compress(FileInputStream fIn, FileOutputStream fOut) throws IOException {
        SequiturOutputStream seqOut = new SequiturOutputStream(fOut);
        byte[] buf = new byte[8*1024];
        int read;
        while ((read = fIn.read(buf, 0, 8*1024)) != -1)
            seqOut.write(buf, 0, read);
        seqOut.close();
    }

    private static void decompress(FileInputStream fIn, FileOutputStream fOut) throws IOException {
        SequiturInputStream seqIn = new SequiturInputStream(fIn);
        byte[] buf = new byte[8*1024];
        int read;
        while ((read = seqIn.read(buf, 0, 8*1024)) != -1)
            fOut.write(buf, 0, read);
        seqIn.close();
    }

    private static void help(PrintStream out) {
        out.println("Usage: java " + CmdLine.class.getName() + " [options]");
        out.println("  where [options] is");
        out.println("    -i <filename>       the input file");
        out.println("    -o <filename>       the output file");
        out.println("    -d                  to decompress (default is compressing)");
        out.println("    -h                  to print this help");
    }

    private static void ensureMore(String[] args, int pos, int additional) {
        if (args.length <= pos + additional) {
            System.err.format("\"%s\" needs %d more argument%s%n",
                args[pos], additional, additional == 1 ? "" : "s");
            System.exit(-1);
        }
    }

}

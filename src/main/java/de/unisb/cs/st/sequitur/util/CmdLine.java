/** License information:
 *    Component: sequitur
 *    Package:   de.unisb.cs.st.sequitur.util
 *    Class:     CmdLine
 *    Filename:  sequitur/src/main/java/de/unisb/cs/st/sequitur/util/CmdLine.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class CmdLine {

    public static void main(String[] args) {
        String input = null, output = null;
        boolean decompress = false;
        boolean printGrammar = false;
        boolean printAsChars = false;
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
            } else if ("-g".equals(arg)) {
                printGrammar = true;
            } else if ("-t".equals(arg)) {
                printAsChars = true;
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
                decompress(fIn, fOut, printGrammar, printAsChars);
            else
                compress(fIn, fOut, printGrammar, printAsChars);
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

    private static void compress(FileInputStream fIn, FileOutputStream fOut, boolean printGrammar, boolean printAsChars) throws IOException {
        SequiturOutputStream seqOut = new SequiturOutputStream(fOut, printAsChars);
        byte[] buf = new byte[8*1024];
        int read;
        while ((read = fIn.read(buf, 0, 8*1024)) != -1)
            seqOut.write(buf, 0, read);

        if (printGrammar) {
            seqOut.flush();
            System.out.println(seqOut);
        }

        seqOut.close();
    }

    private static void decompress(FileInputStream fIn, FileOutputStream fOut, boolean printGrammar, boolean printAsCharacters) throws IOException {
        SequiturInputStream seqIn = new SequiturInputStream(fIn, printAsCharacters);

        if (printGrammar)
            System.out.println(seqIn);

        byte[] buf = new byte[8*1024];
        int read;
        while ((read = seqIn.read(buf, 0, 8*1024)) != -1)
            fOut.write(buf, 0, read);
        seqIn.close();
    }

    private static void help(PrintStream out) {
        out.println("Usage: java " + CmdLine.class.getName() + " [options]");
        out.println("  where [options] is");
        out.println("    -d                  to decompress (default is compressing)");
        out.println("    -h                  to print this help");
        out.println("    -g                  to print the sequitur grammar before decompression / after compression");
        out.println("    -i <filename>       the input file");
        out.println("    -o <filename>       the output file");
        out.println("    -t                  print grammar as text (raw bytes), rather than integers");
    }

    private static void ensureMore(String[] args, int pos, int additional) {
        if (args.length <= pos + additional) {
            System.err.format("\"%s\" needs %d more argument%s%n",
                args[pos], additional, additional == 1 ? "" : "s");
            System.exit(-1);
        }
    }

}

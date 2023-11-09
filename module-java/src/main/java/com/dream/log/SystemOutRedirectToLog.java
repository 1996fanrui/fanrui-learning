package com.dream.log;

import org.apache.commons.io.output.NullPrintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

/**
 * Test for ignore System.out and redirect System.out to LOG.
 */
public class SystemOutRedirectToLog {

    private static final Logger LOG = LoggerFactory.getLogger(SystemOutRedirectToLog.class);

    public static void main(String[] args) {
        // Normal
        sysout();

        // Discard all output
        System.setOut(new NullPrintStream());
//        System.setOut(new PrintStream(NullOutputStream.INSTANCE));
        sysout();

        // Redirect all System.out to LOG.
        System.setOut(new LoggingPrintStream(LOG));
        sysout();
    }

    private static void sysout() {
        System.out.println("aa1bb");
        System.out.println("jsfkjskjl " + System.lineSeparator() + "fjskdfsvnmx");
        System.out.print(1);
        System.out.print('c');
        System.out.println("aa2bb");
        System.out.println();
        System.out.println();
        System.out.println("aa3bb");
        System.out.println("aa4bb");
    }

    /**
     * Cache current line context, generateContext() and reset() after the line is ended.
     */
    private static class LoggingOutputStreamHelper extends ByteArrayOutputStream {

        private static final byte[] LINE_SEPARATOR_BYTES = System.lineSeparator().getBytes();
        private static final int LINE_SEPARATOR_LENGTH = LINE_SEPARATOR_BYTES.length;

        public synchronized Optional<String> tryGenerateContext() {
            if (!isLineEnded()) {
                return Optional.empty();
            }
            try {
                return Optional.of(new String(buf, 0, count - LINE_SEPARATOR_LENGTH));
            } finally {
                reset();
            }
        }

        private synchronized boolean isLineEnded() {
            if (count < LINE_SEPARATOR_LENGTH) {
                return false;
            }

            if (LINE_SEPARATOR_LENGTH == 1) {
                return LINE_SEPARATOR_BYTES[0] == buf[count - 1];
            }

            for (int i = 0; i < LINE_SEPARATOR_LENGTH; i++) {
                if (LINE_SEPARATOR_BYTES[i] == buf[count - LINE_SEPARATOR_LENGTH + i]) {
                    continue;
                }
                return false;
            }
            return true;
        }
    }

    /**
     * Redirect the PrintStream to Logger.
     */
    private static class LoggingPrintStream extends PrintStream {

        private final Logger logger;

        private final LoggingOutputStreamHelper helper;

        private LoggingPrintStream(Logger logger) {
            super(new LoggingOutputStreamHelper());
            helper = (LoggingOutputStreamHelper) super.out;
            this.logger = logger;
        }

        public void write(int b) {
            super.write(b);
            tryLogCurrentLine();
        }

        public void write(byte[] b, int off, int len) {
            super.write(b, off, len);
            tryLogCurrentLine();
        }

        private void tryLogCurrentLine() {
            synchronized (this) {
                helper.tryGenerateContext().ifPresent(logger::info);
            }
        }
    }

}

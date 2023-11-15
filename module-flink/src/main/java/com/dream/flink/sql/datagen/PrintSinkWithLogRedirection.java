package com.dream.flink.sql.datagen;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

public class PrintSinkWithLogRedirection {
    private static final Logger LOG = LoggerFactory.getLogger(PrintSinkWithLogRedirection.class);

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(3);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        System.setOut(new LoggingPrintStream(LOG));


        String sourceDDL = "CREATE TABLE orders (\n" +
                "  id           INT,\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='2',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='10',\n" +
                "   'fields.channel.min'='21',\n" +
                "   'fields.channel.max'='30',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        String sinkDDL = "create table print_sink ( \n" +
                "  id           INT,\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3)\n" +
                ") with ('connector' = 'print' )";
        tableEnv.executeSql(sinkDDL);

        String dml = "insert into print_sink\n" +
                "select id" +
                "       ,app" +
                "       ,channel" +
                "       ,user_id" +
                "       ,ts" +
                "   from orders";

        tableEnv.executeSql(dml);
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
                return Optional.of("ThreadName: " + Thread.currentThread().getName() + "   logContext : " + new String(buf, 0, count - LINE_SEPARATOR_LENGTH));
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

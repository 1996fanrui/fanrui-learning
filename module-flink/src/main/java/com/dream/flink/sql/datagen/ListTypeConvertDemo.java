package com.dream.flink.sql.datagen;

import com.dream.flink.sql.FlinkSqlUtil;
import com.dream.flink.sql.profile.sink.ComplexSink;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.ListSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Arrays;
import java.util.List;

/**
 * @author fanrui
 * @date 2022-03-03 14:11:02
 */
public class ListTypeConvertDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        Item item = new Item();
        item.setItemId(10L);
        item.setLabelIDs(new long[]{1L, 2L});
//        item.setLabelIDs(Arrays.asList(1L, 2L));

        DataStreamSource<Item> itemDataStreamSource = env.fromElements(Item.class, item);
        Table table = tableEnv.fromDataStream(itemDataStreamSource);

        DataStream<Item> stream = tableEnv.toDataStream(table, Item.class);
        stream.print();

        env.execute(ListTypeConvertDemo.class.getSimpleName());
    }

    public static class Item {

        private long itemId;

        // Don't support List struct.
//        @DataTypeHint(rawSerializer = LongSerializer.class)
//        private List<Long> labelIDs;
        private long[] labelIDs;

        public long getItemId() {
            return itemId;
        }

        public void setItemId(long itemId) {
            this.itemId = itemId;
        }

//        public List<Long> getLabelIDs() {
//            return labelIDs;
//        }
//
//        public void setLabelIDs(List<Long> labelIDs) {
//            this.labelIDs = labelIDs;
//        }

        public long[] getLabelIDs() {
            return labelIDs;
        }

        public void setLabelIDs(long[] labelIDs) {
            this.labelIDs = labelIDs;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "itemId=" + itemId +
                    ", labelIDs=" + labelIDs +
                    '}';
        }
    }

}

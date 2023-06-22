package com.dream.flink.sql.connector;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/*

use flink_test_job;
create table if not exists `city_info` (
  `city_id` int not null,
  `city_name` varchar(255) not null
);

insert into `city_info` values (1, 'Beijing');
insert into `city_info` values (2, 'Shanghai');
insert into `city_info` values (3, 'Guangzhou');
insert into `city_info` values (4, 'Shenzhen');
insert into `city_info` values (5, 'Hangzhou');
insert into `city_info` values (6, 'Nanjing');
insert into `city_info` values (7, 'Chengdu');
insert into `city_info` values (8, 'Wuhan');
insert into `city_info` values (9, 'Tianjin');
insert into `city_info` values (10, 'Foshan');

 */
public class JdbcLookupJoin {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        env.disableOperatorChaining();

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  city_id      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts AS PROCTIME()\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='100000',\n" +
                "   'fields.app.min'='100',\n" +
                "   'fields.app.max'='200',\n" +
                "   'fields.city_id.min'='1',\n" +
                "   'fields.city_id.max'='10',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        String mysqlDDL = "CREATE TABLE city_info (\n" +
                "  city_id INT,\n" +
                "  city_name STRING\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://localhost:3306/flink_test_job',\n" +
                "   'table-name' = 'city_info',\n" +
//                "   'lookup.cache.max-rows' = '1050',\n" +
//                "   'lookup.cache.ttl' = '5 min',\n" +
                "   'username' = 'root',\n" +
                "   'password' = '123456'\n" +
                ")";

        String sinkDDL = "CREATE TABLE `print_table` (\n" +
                "  `app` INT,\n" +
                "  `city` INT,\n" +
                "  `user_id` STRING,\n" +
                "  `ts` TIMESTAMP(3),\n" +
                "  `app_name` STRING\n" +
                ")\n" +
                "WITH (\n" +
//                "  'connector' = 'print'\n" +
                "  'connector' = 'blackhole'\n" +
                ")";

        String joinSQL = "insert into print_table\n" +
                "select orders.app,\n" +
                "  orders.city_id,\n" +
                "  orders.user_id,\n" +
                "  orders.ts,\n" +
                "  c.city_name\n" +
                "  from orders\n" +
                "join city_info for system_time as of orders.ts as c\n" +
                "on orders.city_id = c.city_id";

        System.out.println(sourceDDL);
        System.out.println(mysqlDDL);
        System.out.println(sinkDDL);
        System.out.println(joinSQL);

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(mysqlDDL);
        tableEnv.executeSql(sinkDDL);
        tableEnv.executeSql(joinSQL);
    }

}

package com.dream.flink.sql.hive;

import com.dream.flink.sql.FlinkSqlUtil;
import com.dream.flink.util.file.FileReaderUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanrui03
 * @date 2020/12/22 10:59
 */
public class FlinkSqlExecutor {

    private static final String CATALOG_KEY = "catalog";
    private static final String DATABASE_KEY = "database";
    private static final String SQL_KEY = "sql";
    private static final String SQL_PATH_KEY = "sqlPath";

    public static void main(String[] args) {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv();

        String sqlStr = parameterTool.get(SQL_KEY);
        if (sqlStr == null) {
            String sqlPath = parameterTool.get(SQL_PATH_KEY);
            sqlStr = FileReaderUtil.readFileContent(sqlPath);
        }

        Preconditions.checkNotNull(sqlStr, "必须传入 sql 或 sqlPath.");
        System.out.println("SQL: " + sqlStr);

        String catalogName = parameterTool.get(CATALOG_KEY, "myhive");
        String database = parameterTool.get(DATABASE_KEY, "default");

        // flink client 端 hive conf
        String hiveConfDir = System.getenv("FLINK_HOME") + "/conf";
        HiveCatalog catalog = new HiveCatalog(catalogName, database, hiveConfDir);
        tableEnv.registerCatalog(catalogName, catalog);

        String[] catalogs = tableEnv.listCatalogs();
        System.out.println(Arrays.toString(catalogs));

        tableEnv.useCatalog(catalogName);
        tableEnv.useDatabase(database);

        String currentCatalog = tableEnv.getCurrentCatalog();
        System.out.println("当前 Catalog: " + currentCatalog);

        List<String> sqls = Arrays.stream(sqlStr.split(";"))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());

        int sqlCount = sqls.size();
        if (sqlCount == 1) {
            System.out.println("executeSql : " + sqls.get(0));
            tableEnv.executeSql(sqls.get(0));
        } else if (sqlCount > 1) {
            StatementSet stmtSet = tableEnv.createStatementSet();
            for (String sql : sqls) {
                System.out.println("sub sql: " + sql);
                stmtSet.addInsertSql(sql);
            }
            stmtSet.execute();
        }

    }

}

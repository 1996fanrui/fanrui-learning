package com.dream.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * refer:
 * https://logging.apache.org/log4j/2.x/manual/json-template-layout.html
 * https://logging.apache.org/log4j/2.x/manual/lookups.html
 * https://logging.apache.org/log4j/2.x/manual/filters.html
 *
 * <dependency>
 *     <groupId>org.apache.logging.log4j</groupId>
 *     <artifactId>log4j-layout-template-json</artifactId>
 *     <version>2.20.0</version>
 * </dependency>
 *
 * appender.console.json.type = JsonTemplateLayout
 * appender.console.json.eventTemplateUri = classpath:EcsLayout.json
 *
 * EcsLayout.json 中可以使用 "${env:USER}" 打印环境变量。
 */
public class JsonTemplateLayoutTest {

    protected static final Logger LOG = LoggerFactory.getLogger(JsonTemplateLayoutTest.class);

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            LOG.info("Test for json layout, {}", i);
            Thread.sleep(500);
        }
    }
}

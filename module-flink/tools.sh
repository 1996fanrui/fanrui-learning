
# Flink 源码打包命令

mvn clean install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Drat.skip=true -Dcheckstyle.skip=true

mvn -T 8 clean install -DskipTests -Dmaven.javadoc.skip=true -Dcheckstyle.skip=true
mvn -T 8 clean install -DskipTests -Dmaven.javadoc.skip=true -Drat.skip=true -Dcheckstyle.skip=true

mvn -T 8 clean install -DskipTests -Dhadoop.version=2.7.6 -Dmaven.javadoc.skip=true -Drat.skip=true -Dcheckstyle.skip=true


-B -DskipTests -Dmaven.javadoc.skip=true -Drat.skip=true -Dmaven.test.skip=true  -Dgpg.skip=true -Dgpg.useagent=false -X clean deploy

# 跳过 apache License 检查
-Drat.skip=true

# 忽略 checkstyle
-Dcheckstyle.skip=true

# 多线程打包
-T 8

# 不执行测试用例，但编译测试用例类生成相应的class文件至target/test-classes下。
-DskipTests

# 不执行测试用例，也不编译测试用例类。
-Dmaven.test.skip=true


mvn clean install -Dmaven.test.skip=true -Dhadoop.version=2.7.6 -Dmaven.javadoc.skip=true -Dcheckstyle.skip=true


https://blog.csdn.net/baifanwudi/article/details/99564142







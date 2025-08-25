#!/bin/bash

set -e  # 遇到错误立即退出
set -x  # 会在执行每个命令之前，先打印出这个命令以及其所有参数。方便排查具体执行到哪里了

# cd flink-tests
../mvnw -T 20 clean install -DskipTests -Dmaven.javadoc.skip=true -Drat.skip=true -Dcheckstyle.skip=true -Denforcer.skip=true -P java11,java11-target --no-snapshot-updates
../mvnw -am -Dtest=org.apache.flink.test.checkpointing.UnalignedCheckpointRescaleWithMixedExchangesITCase test -Dmaven.javadoc.skip=true -Drat.skip=true -Dcheckstyle.skip=true -Denforcer.skip=true -P java11,java11-target --no-snapshot-updates

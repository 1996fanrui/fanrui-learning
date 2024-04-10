```shell
docker-compose up
docker-compose up -d
# 
docker-compose up -d  --force-recreate

docker-compose down
# 退出 container 时，清理 volume
docker-compose down -v

docker exec -it kafka /bin/bash

cd /opt/kafka/bin

kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic quickstart-events

docker volume ls
```
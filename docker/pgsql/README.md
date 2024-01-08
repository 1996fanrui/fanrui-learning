```shell
docker-compose up
docker-compose down

docker exec -it pgsql /bin/bash
psql -h localhost -p 5432 -U root -W

# list all databases
\l

# use flink_autoscaler database;
\c flink_autoscaler;

# list all tables of current database
\dt


# show table schema 
\d table_name
\d t_flink_autoscaler_state_store

```
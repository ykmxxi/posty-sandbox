#!/bin/bash
set -e

# Replica 초기화 시 실행: Source에 GTID 기반 복제 연결
mysql -u root -proot <<-EOSQL
    CHANGE REPLICATION SOURCE TO
        SOURCE_HOST='mysql-source',
        SOURCE_PORT=3306,
        SOURCE_USER='repl',
        SOURCE_PASSWORD='repl',
        SOURCE_AUTO_POSITION=1;
    START REPLICA;
EOSQL

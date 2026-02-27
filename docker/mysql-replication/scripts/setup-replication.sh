#!/bin/bash
set -e

# Source 초기화 시 실행: 복제용 유저 생성
mysql -u root -proot <<-EOSQL
    CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED BY 'repl';
    GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
    FLUSH PRIVILEGES;
EOSQL

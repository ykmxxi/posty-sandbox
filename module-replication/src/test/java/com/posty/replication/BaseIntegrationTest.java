package com.posty.replication;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * module-replication 통합 테스트 베이스 클래스.
 *
 * 이 모듈은 Source-Replica 구성이 필요하므로 Docker Compose가 주력이다.
 * Testcontainers는 단일 MySQL만 제공하므로 복제 시나리오에는 적합하지 않다.
 * Docker Compose로 mysql-source(3307), mysql-replica(3308)를 먼저 띄운 후 테스트를 실행한다.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseIntegrationTest {
}

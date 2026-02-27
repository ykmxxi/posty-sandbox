package com.posty.lock.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NamedLockRepository {

    @Query(value = "SELECT GET_LOCK(:lockName, :timeout)", nativeQuery = true)
    Integer getLock(@Param("lockName") String lockName, @Param("timeout") int timeout);

    @Query(value = "SELECT RELEASE_LOCK(:lockName)", nativeQuery = true)
    Integer releaseLock(@Param("lockName") String lockName);
}

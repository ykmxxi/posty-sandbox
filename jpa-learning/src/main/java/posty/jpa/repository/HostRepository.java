package posty.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import posty.jpa.domain.Host;

@Repository
public interface HostRepository extends JpaRepository<Host, Long> {

    @Query("""
        SELECT DISTINCT h
        FROM Host h JOIN FETCH h.spaceHostMaps s
        WHERE h.id IN :ids
        """)
    List<Host> findAllByIdInWithSpaces(List<Long> content);
}

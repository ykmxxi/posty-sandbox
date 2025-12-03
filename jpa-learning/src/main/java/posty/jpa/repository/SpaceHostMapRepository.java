package posty.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import posty.jpa.domain.Host;
import posty.jpa.domain.SpaceHostMap;

public interface SpaceHostMapRepository extends JpaRepository<SpaceHostMap, Long> {

    List<SpaceHostMap> findAllByHost(Host host);
}

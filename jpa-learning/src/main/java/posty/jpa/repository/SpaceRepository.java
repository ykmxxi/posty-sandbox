package posty.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import posty.jpa.domain.Space;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
}

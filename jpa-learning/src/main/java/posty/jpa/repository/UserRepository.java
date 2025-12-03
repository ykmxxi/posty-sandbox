package posty.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import posty.jpa.domain.example.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

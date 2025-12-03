package posty.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import posty.jpa.domain.example.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}

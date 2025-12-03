package cataloginteractionservice.repository.like;

import cataloginteractionservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByCatalogIdAndUserId(Long catalogId, String userId);
}

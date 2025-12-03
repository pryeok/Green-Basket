package cataloginteractionservice.repository.like;

import cataloginteractionservice.entity.LikeCount;
import jakarta.persistence.LockModeType;
import org.hibernate.LockMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeCountRepository extends JpaRepository<LikeCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LikeCount> findLockedByCatalogId(Long catalogId);
}

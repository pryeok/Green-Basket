package cataloginteractionservice.repository.view;

import cataloginteractionservice.entity.ViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewCountBackUpRepository extends JpaRepository<ViewCount, Long> {
    @Query(
            value = "update catalog_view_count set view_count = :viewCount " +
                    "where catalog_id = :catalogId and view_count < :viewCount",
            nativeQuery = true
    )
    @Modifying
    int updateViewCount(
            @Param("catalogId") Long catalogId,
            @Param("viewCount") Long viewCount
    );
}

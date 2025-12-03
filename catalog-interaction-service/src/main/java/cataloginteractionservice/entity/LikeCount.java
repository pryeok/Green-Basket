package cataloginteractionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "catalog_like_count")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeCount {
    @Id
    private Long catalogId; // Shard Key

    @Column(nullable = false)
    private Long likeCount;

    public static LikeCount init(Long catalogId, Long likeCount) {
        LikeCount likeCountEntity = new LikeCount();
        likeCountEntity.catalogId = catalogId;
        likeCountEntity.likeCount = likeCount;
        return likeCountEntity;
    }

    public void increase() {
        this.likeCount++;
    }

    public void decrease() {
        this.likeCount--;
    }
}

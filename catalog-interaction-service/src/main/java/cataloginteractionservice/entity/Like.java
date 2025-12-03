package cataloginteractionservice.entity;

import cataloginteractionservice.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "catalog_like")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {
    @Id
    private Long likeId;

    @Column(nullable = false) // Shard Key
    private Long catalogId;
    @Column(nullable = false)
    private String userId;

    public static Like create(Long likeId, Long catalogId, String userId) {
        Like like = new Like();
        like.likeId = likeId;
        like.catalogId = catalogId;
        like.userId = userId;
        return like;
    }
}

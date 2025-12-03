package cataloginteractionservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "catalog_view_count")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewCount {
    @Id
    private Long catalogId;
    private Long viewCount;

    public static ViewCount init(Long catalogId, Long viewCount) {
        ViewCount viewCountEntity = new ViewCount();
        viewCountEntity.catalogId = catalogId;
        viewCountEntity.viewCount = viewCount;
        return viewCountEntity;
    }
}

package catalogreadservice.model;

import catalogreadservice.client.response.CatalogResponse;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * CQRS Read Model
 * Catalog 기본 정보 + Interaction 데이터 (댓글수, 좋아요수, 조회수)를 통합한 비정규화 모델
 */
@Getter
public class CatalogQueryModel {
    private Long id;
    private String productId;
    private String productName;
    private Integer stock;
    private Integer unitPrice;
    private String userId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Interaction 데이터
//    private Long commentCount;
    private Long likeCount;
    private Long viewCount;

    /**
     * Catalog 기본 정보 + Interaction 데이터로 Read Model 생성 (Cache Miss 시)
     */
    public static CatalogQueryModel create(
            CatalogResponse catalog,
//            Long commentCount,
            Long likeCount,
            Long viewCount
    ) {
        CatalogQueryModel model = new CatalogQueryModel();
        model.id = catalog.getId();
        model.productId = catalog.getProductId();
        model.productName = catalog.getProductName();
        model.stock = catalog.getStock();
        model.unitPrice = catalog.getUnitPrice();
        model.userId = catalog.getUserId();
        model.categoryId = catalog.getCategoryId();
//        model.commentCount = commentCount;
        model.likeCount = likeCount;
        model.viewCount = viewCount;
        return model;
    }

    /**
     * 기본값으로 생성 (Interaction 데이터 0으로 초기화)
     */
    public static CatalogQueryModel create(CatalogResponse catalog) {
        return create(catalog, 0L, 0L);
//        return create(catalog, 0L, 0L, 0L);
    }

    /**
     * 상품 정보 업데이트 (재고, 가격 등)
     */
    public void updateCatalogInfo(CatalogResponse catalog) {
        this.productName = catalog.getProductName();
        this.stock = catalog.getStock();
        this.unitPrice = catalog.getUnitPrice();
        this.userId = catalog.getUserId();
        this.categoryId = catalog.getCategoryId();
    }

    /**
     * 댓글 수 업데이트
     */
//    public void updateCommentCount(Long commentCount) {
//        this.commentCount = commentCount;
//    }

    /**
     * 좋아요 수 업데이트
     */
    public void updateLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * 조회 수 업데이트
     */
    public void updateViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
}

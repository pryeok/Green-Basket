package catalogreadservice.service.response;

import catalogreadservice.model.CatalogQueryModel;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Catalog 조회 응답 DTO
 */
@Getter
public class CatalogReadResponse {
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
    private Long commentCount;
    private Long likeCount;
    private Long viewCount;

    /**
     * CatalogQueryModel로부터 응답 생성
     */
    public static CatalogReadResponse from(CatalogQueryModel catalogQueryModel, Long viewCount) {
        CatalogReadResponse response = new CatalogReadResponse();
        response.id = catalogQueryModel.getId();
        response.productId = catalogQueryModel.getProductId();
        response.productName = catalogQueryModel.getProductName();
        response.stock = catalogQueryModel.getStock();
        response.unitPrice = catalogQueryModel.getUnitPrice();
        response.userId = catalogQueryModel.getUserId();
        response.categoryId = catalogQueryModel.getCategoryId();
        response.createdAt = catalogQueryModel.getCreatedAt();
        response.updatedAt = catalogQueryModel.getUpdatedAt();
//        response.commentCount = catalogQueryModel.getCommentCount();
        response.likeCount = catalogQueryModel.getLikeCount();
        response.viewCount = viewCount;  // ViewClient에서 실시간 조회
        return response;
    }
}

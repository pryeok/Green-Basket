package userservice.service.token;

public interface RefreshTokenService {

    /**
     * RefreshToken 저장
     */
    void saveRefreshToken(String userId, String refreshToken);

    /**
     * RefreshToken 조회
     */
    String getRefreshToken(String userId);

    /**
     * RefreshToken 검증
     */
    boolean validateRefreshToken(String userId, String refreshToken);

    /**
     * RefreshToken 삭제 (로그아웃)
     */
    void deleteRefreshToken(String userId);
}

package userservice.service.auth;

import userservice.service.auth.request.LoginRequest;
import userservice.service.auth.response.TokenResponse;

public interface AuthService {

    /**
     * 로그인
     */
    TokenResponse login(LoginRequest request);

    /**
     * AccessToken 재발급
     */
    TokenResponse refreshAccessToken(String refreshToken);

    /**
     * 로그아웃
     */
    void logout(String userId);
}

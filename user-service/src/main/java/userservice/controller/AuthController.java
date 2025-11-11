package userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import userservice.service.auth.AuthService;
import userservice.service.auth.request.LoginRequest;
import userservice.service.auth.response.TokenResponse;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        log.info("로그인 요청: userId={}", request.getUserId());
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * AccessToken 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("AccessToken 재발급 요청");
        TokenResponse response = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal String userId) {
        log.info("로그아웃 요청: userId={}", userId);
        authService.logout(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

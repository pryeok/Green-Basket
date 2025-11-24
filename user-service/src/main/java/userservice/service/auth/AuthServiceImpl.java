package userservice.service.auth;

import com.greenbasket.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.entity.User;
import userservice.repository.UserRepository;
import userservice.service.auth.request.LoginRequest;
import userservice.service.auth.response.TokenResponse;
import userservice.service.token.RefreshTokenService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getEncryptedPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 3. AccessToken & RefreshToken 생성
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), "USER", user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        // 4. RefreshToken을 Redis에 저장
        refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken);

        log.info("로그인 성공: userId={}", user.getUserId());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        // 1. RefreshToken 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 RefreshToken입니다.");
        }

        // 2. UserId 추출
        String userId = jwtUtil.getUserIdFromToken(refreshToken);

        // 3. Redis에 저장된 RefreshToken과 비교
        if (!refreshTokenService.validateRefreshToken(userId, refreshToken)) {
            throw new RuntimeException("RefreshToken이 일치하지 않습니다.");
        }

        // 4. 사용자 조회하여 userPk 획득
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 5. 새로운 AccessToken 생성
        String newAccessToken = jwtUtil.generateAccessToken(userId, "USER", user.getId());

        log.info("AccessToken 재발급 성공: userId={}", userId);

        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Override
    @Transactional
    public void logout(String userId) {
        refreshTokenService.deleteRefreshToken(userId);
        log.info("로그아웃 완료: userId={}", userId);
    }
}

package userservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import userservice.entity.base.BaseEntity;

import java.time.LocalDateTime;

@Table(name = "refresh_tokens")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public static RefreshToken create(String userId, String token, LocalDateTime expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.userId = userId;
        refreshToken.token = token;
        refreshToken.expiryDate = expiryDate;
        return refreshToken;
    }

    public void updateToken(String token, LocalDateTime expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}

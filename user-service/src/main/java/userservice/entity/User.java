package userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import userservice.entity.base.BaseEntity;

@Table(name = "users")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false, unique = true)
    private String encryptedPwd;

    public static User create (Long id,String email, String name, String userId, String encryptedPwd) {
        User user = new User();
        user.id = id;
        user.email = email;
        user.name = name;
        user.userId = userId;
        user.encryptedPwd = encryptedPwd;
        return user;
    }

}

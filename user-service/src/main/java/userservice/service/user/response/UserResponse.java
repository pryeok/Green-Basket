package userservice.service.user.response;

import lombok.Getter;
import userservice.entity.User;

@Getter
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String userId;
    private String encryptedPwd;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.email = user.getEmail();
        response.name = user.getName();
        response.userId = user.getUserId();
        return response;
    }
}

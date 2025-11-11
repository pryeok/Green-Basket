package userservice.service.user.request;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String userId;
    private String email;
    private String name;
    private String pwd;
}

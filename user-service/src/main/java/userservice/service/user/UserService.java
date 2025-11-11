package userservice.service.user;

import userservice.dto.UserDto;
import userservice.service.user.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserDto userDto);
    UserResponse getUserByUserId(String userId);
}

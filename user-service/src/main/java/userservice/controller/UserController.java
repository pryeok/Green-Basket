package userservice.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import userservice.dto.UserDto;
import userservice.service.user.UserService;
import userservice.service.user.request.UserCreateRequest;
import userservice.service.user.response.UserResponse;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UserResponse create(@RequestBody UserCreateRequest userCreateRequest) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(userCreateRequest, UserDto.class);
        return userService.createUser(userDto);
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUserByUserId(
            @PathVariable("userId") String userId,
            @RequestHeader(value = "X-User-Id", required = false) String currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        // 본인이거나 관리자만 조회 가능
        if (currentUserId != null) {
            if (!currentUserId.equals(userId) && !"ADMIN".equals(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 정보만 조회할 수 있습니다.");
            }
        }

        return userService.getUserByUserId(userId);
    }
}

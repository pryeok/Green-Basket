package userservice.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.web.bind.annotation.*;
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
        // Request → DTO 변환
        UserDto userDto = mapper.map(userCreateRequest, UserDto.class);
        return userService.createUser(userDto);
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUserByUserId(@PathVariable("userId") String userId) {
        return userService.getUserByUserId(userId);
    }
}

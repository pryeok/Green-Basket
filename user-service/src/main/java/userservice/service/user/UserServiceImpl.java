package userservice.service.user;

import com.greenbasket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.dto.UserDto;
import userservice.entity.User;
import userservice.repository.UserRepository;
import userservice.service.user.response.UserResponse;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Snowflake snowflake;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserDto userDto) {
        User user = User.create(
                snowflake.nextId(),
                userDto.getEmail(),
                userDto.getName(),
                userDto.getUserId(),
                passwordEncoder.encode(userDto.getPwd())
        );
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    public UserResponse getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.from(user);
    }

}

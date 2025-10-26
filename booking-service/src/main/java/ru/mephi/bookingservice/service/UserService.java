package ru.mephi.bookingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mephi.bookingservice.exception.UserNotFoundException;
import ru.mephi.bookingservice.mapper.UserMapper;
import ru.mephi.bookingservice.model.User;
import ru.mephi.bookingservice.model.enums.Role;
import ru.mephi.bookingservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public void deleteUserById(Long userId) {
        User user = getUserById(userId);

        userRepository.delete(user);
    }

    public User updateUserById(Long userId, User user) {
        User findedUser = getUserById(userId);
        User updatedUser = userMapper.userToUser(findedUser, user);

        return createUser(updatedUser);
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        return userRepository.findByUsernameAndPassword(username, encodedPassword)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с указанным именем и паролем не найден."));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с указанным идентификатором не найден."));
    }
}

package com.guidev1911.expl_world_api.auth.service;

import com.guidev1911.expl_world_api.auth.dto.CreateUserRequest;
import com.guidev1911.expl_world_api.auth.entity.User;
import com.guidev1911.expl_world_api.auth.repository.UserRepository;
import com.guidev1911.expl_world_api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(CreateUserRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already registered");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role("ADMIN")
                .active(true)
                .build();

        return userRepository.save(user);
    }
}
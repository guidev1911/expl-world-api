package com.guidev1911.expl_world_api.auth.service;

import com.guidev1911.expl_world_api.auth.dto.CreateUserRequest;
import com.guidev1911.expl_world_api.auth.dto.LoginRequest;
import com.guidev1911.expl_world_api.auth.entity.User;
import com.guidev1911.expl_world_api.auth.repository.UserRepository;
import com.guidev1911.expl_world_api.auth.security.JwtService;
import com.guidev1911.expl_world_api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(
                request.email().trim().toLowerCase()
        ).orElseThrow(() ->
                new BusinessException("Invalid email or password")
        );

        if (!user.getActive()) {
            throw new BusinessException("User is inactive");
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new BusinessException("Invalid email or password");
        }

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );
    }
}
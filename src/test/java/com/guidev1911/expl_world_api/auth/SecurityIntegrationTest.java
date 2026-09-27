package com.guidev1911.expl_world_api.auth;

import com.guidev1911.expl_world_api.auth.entity.User;
import com.guidev1911.expl_world_api.auth.repository.UserRepository;
import com.guidev1911.expl_world_api.auth.security.JwtService;
import com.guidev1911.expl_world_api.category.dto.request.CreateCategoryRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CategoryRepository categoryRepository;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        User admin = userRepository.save(
                User.builder()
                        .name("Test Admin")
                        .email("security-admin@explworld.com")
                        .password(passwordEncoder.encode("12345678"))
                        .role("ADMIN")
                        .active(true)
                        .build()
        );

        User user = userRepository.save(
                User.builder()
                        .name("Test User")
                        .email("security-user@explworld.com")
                        .password(passwordEncoder.encode("12345678"))
                        .role("USER")
                        .active(true)
                        .build()
        );

        adminToken = jwtService.generateToken(
                admin.getEmail(),
                admin.getRole()
        );

        userToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {

        CreateCategoryRequest request = new CreateCategoryRequest(
                "Animals",
                "animals-security",
                "Animal category",
                null
        );

        mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "name": "Animals",
                            "slug": "animals-security",
                            "description": "Animal category"
                        }
                        """)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403ForUserWithoutAdminRole() throws Exception {

        CreateCategoryRequest request = new CreateCategoryRequest(
                "Animals",
                "animals-security-user",
                "Animal category",
                null
        );

        mockMvc.perform(
                        post("/api/v1/categories")
                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "name": "Animals",
                            "slug": "animals-security-user",
                            "description": "Animal category"
                        }
                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToCreateCategory() throws Exception {

        String slug = "security-admin-create-" + UUID.randomUUID();

        mockMvc.perform(
                        post("/api/v1/categories")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Animals",
                                "slug": "%s",
                                "description": "Animal category"
                            }
                            """.formatted(slug))
                )
                .andExpect(status().isCreated());
    }
    @Test
    void shouldReturn401WithInvalidToken() throws Exception {

        mockMvc.perform(
                        post("/api/v1/categories")
                                .header(
                                        "Authorization",
                                        "Bearer token-invalido"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Animals",
                                "slug": "animals-invalid-token-test",
                                "description": "Animal category"
                            }
                            """)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WithExpiredToken() throws Exception {

        String expiredToken = jwtService.generateExpiredToken(
                "security-admin@explworld.com",
                "ADMIN"
        );

        mockMvc.perform(
                        post("/api/v1/categories")
                                .header(
                                        "Authorization",
                                        "Bearer " + expiredToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Animals",
                                "slug": "security-expired-token-%s",
                                "description": "Animal category"
                            }
                            """.formatted(UUID.randomUUID()))
                )
                .andExpect(status().isUnauthorized());
    }
    @Test
    void shouldAllowAuthenticatedUserToReadCategories() throws Exception {

        mockMvc.perform(
                        get("/api/v1/categories")
                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                )
                .andExpect(status().isOk());
    }
}
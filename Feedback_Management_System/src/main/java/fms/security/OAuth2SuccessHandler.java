package fms.security;

import fms.entity.Role;
import fms.entity.User;
import fms.repository.RoleRepository;
import fms.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public OAuth2SuccessHandler(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 1 Find or create user
        User user = userRepository.findByEmail(email).orElseGet(() -> {

            Role userRole = roleRepository.findByRoleName("USER")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName("USER");
                        return roleRepository.save(role);
                    });

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword("OAUTH2_USER"); // dummy
            newUser.setRoles(Collections.singleton(userRole));

            return userRepository.save(newUser);
        });

        // 2️ Generate JWT
        String role = user.getRoles().iterator().next().getRoleName();
        String token = jwtUtil.generateToken(user.getEmail(), role);

        // 3️ Redirect to React with token
        response.sendRedirect(
            "http://localhost:5173/oauth2/redirect?token=" + token
        );
    }
}

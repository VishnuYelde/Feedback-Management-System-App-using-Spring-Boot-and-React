package fms.controller;

import fms.entity.Role;
import fms.entity.User;
import fms.repository.RoleRepository;
import fms.repository.UserRepository;
import fms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	// REGISTER
	@PostMapping("/register")
	public String register(@RequestBody User user) {

		if (userRepository.existsByEmail(user.getEmail())) {
			return "Email already exists";
		}

		Role userRole = roleRepository.findByRoleName("USER").orElseGet(() -> {
			Role role = new Role();
			role.setRoleName("USER");
			return roleRepository.save(role);
		});

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles(Collections.singleton(userRole));

		userRepository.save(user);
		return "User registered successfully";
	}

	// LOGIN
	@PostMapping("/login")
	public Map<String, String> login(@RequestBody User user) {

		User dbUser = userRepository.findByEmail(user.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}

		String token = jwtUtil.generateToken(dbUser.getEmail());

		return Map.of("token", token);
	}
}

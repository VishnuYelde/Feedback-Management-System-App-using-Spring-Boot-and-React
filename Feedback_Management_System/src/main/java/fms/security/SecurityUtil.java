package fms.security;

import org.springframework.security.core.context.SecurityContextHolder;

import fms.entity.Role;
import fms.entity.User;

public class SecurityUtil {

	public static User getCurrentUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	
	public static boolean isAdmin(User user) {
		return user.getRoles().stream().map(Role::getRoleName).anyMatch(role -> role.equalsIgnoreCase("ADMIN"));
	}
}

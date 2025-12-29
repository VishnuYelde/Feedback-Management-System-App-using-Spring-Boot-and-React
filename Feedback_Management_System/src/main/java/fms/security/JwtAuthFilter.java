package fms.security;

import fms.entity.User;
import fms.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		// Skip auth APIs
		if (path.startsWith("/api/auth")) {
			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			try {
				String email = jwtUtil.extractEmail(token);

				if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

					User user = userRepository.findByEmail(email).orElse(null);

					if (user != null) {
						UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,
								null);

						auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(auth);
					}
				}
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}

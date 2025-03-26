package task_management_system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import task_management_system.service.UserService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Try to get JWT from Cookies
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT-TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        //  If JWT is not found in cookies, check Authorization header
        if (token == null) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        //  If no token found, continue filter chain
        if (token == null) {
            System.out.println("[DEBUG] No JWT token found in request.");
            filterChain.doFilter(request, response);
            return;
        }

        //  Validate JWT
        if (!jwtUtil.validateToken(token)) {
            System.out.println("[ERROR] Invalid or Expired Token");
            filterChain.doFilter(request, response);
            return;
        }

        //  Extract email from token
        String email = jwtUtil.getEmailFromToken(token);
        System.out.println("[DEBUG] Token belongs to: " + email);

        // Load user details from the database
        UserDetails userDetails = userService.loadUserByUsername(email);
        if (userDetails == null) {
            System.out.println("[ERROR] No user found with email: " + email);
            filterChain.doFilter(request, response);
            return;
        }

        // Set authentication in Spring Security if not already set
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println("[SUCCESS] Authentication Set: " 
                + SecurityContextHolder.getContext().getAuthentication());
        }

        //  Continue with filter chain
        filterChain.doFilter(request, response);
    }
}
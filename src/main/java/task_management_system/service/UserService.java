package task_management_system.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task_management_system.entity.User;
import task_management_system.repository.UserRepository;

/**
 * Handles user registration, lookup, and implements UserDetailsService.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepo,
            @Lazy PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user, encodes password, sets either ROLE_ADMIN or ROLE_USER.
     */
    public User registerUser(String email, String rawPassword, boolean isAdmin) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists with email: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Set roles
        if (isAdmin) {
            user.setRoles(Collections.singleton("ROLE_ADMIN"));
        } else {
            user.setRoles(Collections.singleton("ROLE_USER"));
        }

        return userRepo.save(user);
    }

    /**
     * Load user by email for Spring Security (UserDetailsService).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userEntity = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        System.out.println("User found: " + userEntity.getEmail());
        System.out.println("Roles from DB: " + userEntity.getRoles());

        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
            .map(SimpleGrantedAuthority::new) // role strings -> SimpleGrantedAuthority
            .collect(Collectors.toList());

        System.out.println("Final Authorities: " + authorities);

        return new org.springframework.security.core.userdetails.User(
            userEntity.getEmail(),
            userEntity.getPassword(),
            authorities
        );
    }

    /**
     * Load a user entity by email directly. Returns null if not found.
     */
    public User loadUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    /**
     * Find user by email. Throws exception if not found.
     */
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}

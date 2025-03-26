package task_management_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import task_management_system.config.JwtUtil;
import task_management_system.entity.User;
import task_management_system.service.UserService;

/**
 * AuthController for register and login
 */
@Controller
@RequestMapping("/auth")
@Tag(name = "Tasks", description = "Endpoints for managing authentication.")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    // Show the login page (GET)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // thymeleaf => templates/login.html
    }

    // Process login (POST)
    @PostMapping("/login")
    public String processLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response,
            Model model
    ) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Set JWT as an HTTP cookie
            Cookie jwtCookie = new Cookie("JWT-TOKEN", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // Set to true if using HTTPS
            jwtCookie.setPath("/"); // Available for entire application
            jwtCookie.setMaxAge(60 * 60 * 24); // 1-day expiration

            response.addCookie(jwtCookie);

            return "redirect:/tasks/all";

        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid Credentials!");
            return "login";
        }
    }



    // Show register form
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    // Process register form
    @PostMapping("/register")
    public String processRegister(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(defaultValue = "false") boolean admin,
            Model model
    ) {
        try {
            User newUser = userService.registerUser(email, password, admin);
            model.addAttribute("message", "Registered user: " + newUser.getEmail());
            return "register";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }
}

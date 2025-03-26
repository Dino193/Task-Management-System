package task_management_system.controller;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import task_management_system.entity.User;
import task_management_system.repository.UserRepository;
import task_management_system.service.UserService;

/**
 * UserController that uses Thymeleaf templates for UI.
 */
@Controller
@RequestMapping("/users")
@Tag(name = "Tasks", description = "Endpoints for managing users.")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    /**
     * 1) List all users (ADMIN only) - "user-list.html"
     */
    @GetMapping
    public String getAllUsers(Authentication auth, Model model) {
        User currentUser = userService.findByEmail(auth.getName());

        // Check if user is admin
        if (!currentUser.getRoles().contains("ROLE_ADMIN")) {
            return "error-403"; // render 403 page
        }

        List<User> allUsers = userRepo.findAll();
        model.addAttribute("users", allUsers);
        return "user-list";
    }

    /**
     * 2) Get user by ID (ADMIN or the user) - "user-detail.html"
     */
    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id, Authentication auth, Model model) {
        User currentUser = userService.findByEmail(auth.getName());
        User targetUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only admin or the same user can view this
        if (!currentUser.getRoles().contains("ROLE_ADMIN") &&
            !targetUser.getId().equals(currentUser.getId())) {
            return "error-403";
        }

        model.addAttribute("user", targetUser);
        return "user-detail";
    }

    /**
     * 3) Admin: Show form to update user roles - "edit-roles.html"
     */
    @GetMapping("/{id}/edit-roles")
    public String editRolesForm(@PathVariable Long id, Authentication auth, Model model) {
        User currentUser = userService.findByEmail(auth.getName());
        if (!currentUser.getRoles().contains("ROLE_ADMIN")) {
            return "error-403";
        }

        User targetUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", targetUser);
        return "edit-roles";
    }

    /**
     * 4) Admin: Update user roles (handles form submission)
     */
    @PostMapping("/{id}/edit-roles")
    public String updateRoles(
            @PathVariable Long id,
            @RequestParam("roles") Set<String> roles,
            Authentication auth,
            Model model
    ) {
        User currentUser = userService.findByEmail(auth.getName());
        if (!currentUser.getRoles().contains("ROLE_ADMIN")) {
            return "error-403";
        }

        User targetUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // e.g. roles = ["ROLE_ADMIN", "ROLE_USER"]
        targetUser.setRoles(roles);
        userRepo.save(targetUser);

        model.addAttribute("message", "Updated roles for user: " + targetUser.getEmail());
        return "redirect:/users";
    }

    /**
     * 5) Admin: Promote user to ADMIN
     */
    @PostMapping("/{id}/promote")
    public String promoteUser(@PathVariable Long id, Authentication auth, Model model) {
        User currentUser = userService.findByEmail(auth.getName());
        if (!currentUser.getRoles().contains("ROLE_ADMIN")) {
            return "error-403";
        }

        User targetUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Add "ROLE_ADMIN"
        Set<String> roles = targetUser.getRoles();
        roles.add("ROLE_ADMIN");
        targetUser.setRoles(roles);
        userRepo.save(targetUser);

        model.addAttribute("message", "User " + targetUser.getEmail() + " is now ADMIN");
        return "redirect:/users";
    }
}

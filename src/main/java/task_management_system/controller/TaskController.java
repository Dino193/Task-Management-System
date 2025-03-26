package task_management_system.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import task_management_system.entity.Task;
import task_management_system.entity.User;
import task_management_system.service.TaskService;
import task_management_system.service.UserService;

@Controller
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Endpoints for managing tasks.")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    /**
     * Show tasks:
     * - ADMIN sees all tasks
     * - Other users see only tasks they authored or are assigned
     */
    @GetMapping("/all")
    public String showAllTasks(Model model, Authentication auth) {
    	
    	
        User currentUser = userService.findByEmail(auth.getName());
        
        
        System.out.println("Current User: " + currentUser.getEmail());
	    System.out.println("Roles: " + currentUser.getRoles()); // Debugging roles
        
        List<Task> tasks;

        // We rely on DB storing "ROLE_ADMIN" if user is admin
        // Ensure roles are compared correctly
        if (currentUser.getRoles().stream().anyMatch(role -> role.equals("ROLE_ADMIN"))) {
           
        	tasks = taskService.findAll();
        	
        } else {
        	
            tasks = taskService.findAllForUser(currentUser.getId());
        }
        
        model.addAttribute("tasks", tasks);

        return "task-list"; // thymeleaf => templates/tasks/task-list.html
    }

    /**
     * Create task form (ADMIN)
     */
    @GetMapping("/admin/create")
    public String createTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    /**
     * Process create task form (ADMIN)
     */
    @PostMapping("/admin/save")
    public String saveTask(@ModelAttribute("task") Task task, Authentication auth) {
        User currentUser = userService.findByEmail(auth.getName());
        taskService.createTask(task, currentUser);
        return "redirect:/tasks/all";
    }

    /**
     * Edit existing task (ADMIN)
     */
    @GetMapping("/admin/edit/{taskId}")
    public String editTaskForm(@PathVariable Long taskId, Model model) {
        Task existing = taskService.findById(taskId);
        model.addAttribute("task", existing);
        return "task-form";
    }

    /**
     * Process update (ADMIN)
     */
    @PostMapping("/admin/update/{taskId}")
    public String updateTaskAsAdmin(@PathVariable Long taskId,
                                    @ModelAttribute("task") Task updated) {
        taskService.updateTaskAsAdmin(taskId, updated);
        return "redirect:/tasks/all";
    }

    /**
     * Delete task (ADMIN)
     */
    @GetMapping("/admin/delete/{taskId}")
    public String deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/tasks/all";
    }

    /**
     * Update as user => e.g., change status, add comment
     */
    @PostMapping("/user/update/{taskId}")
    public String updateTaskAsUser(
            @PathVariable Long taskId,
            @RequestParam("status") String status,
            @RequestParam(value = "comment", required = false) String comment,
            Authentication auth
    ) {
        try {
            Task updated = new Task();
            updated.setStatus(status);
            if (comment != null && !comment.isEmpty()) {
                updated.getComments().add(comment);
            }
            taskService.updateTaskAsUser(taskId, updated, auth.getName());
            return "redirect:/tasks/all";
        } catch (RuntimeException e) {
            return "redirect:/error/403";
        }
    }
}

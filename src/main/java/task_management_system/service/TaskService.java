package task_management_system.service;

import org.springframework.stereotype.Service;

import task_management_system.entity.Task;
import task_management_system.entity.User;
import task_management_system.repository.TaskRepository;
import task_management_system.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class TaskService {
	
	
	

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;
    
    

    // Create new task
    public Task createTask(Task task, User author) {
    	
        task.setAuthor(author);
        
        return taskRepository.save(task);
    }

    
    // Administrator menja status, prioritet, dodeljuje assignee
    public Task updateTaskAsAdmin(Long taskId, Task updated) {
    	
        Task existing = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setPriority(updated.getPriority());
        
        // give assignee
        if (updated.getAssignee() != null) {
        	
            User assignee = userRepository.findById(updated.getAssignee().getId())
                .orElseThrow(() -> new RuntimeException("Assignee not found"));
            existing.setAssignee(assignee);
        }
        
        return taskRepository.save(existing);
    }

    // Korisnik moze da menja samo "svoje" zadatke
    public Task updateTaskAsUser(Long taskId, Task updated, String userEmail) {
    	
    	
        Task existing = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        // dohvati user-a
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Provera da li je user assignee?
        if (existing.getAssignee() == null || !existing.getAssignee().getId().equals(user.getId())) {
            throw new RuntimeException("No permission to update this task");
        }

        // user moze menjati status i dodavati komentare
        existing.setStatus(updated.getStatus());
        
        if (updated.getComments() != null && !updated.getComments().isEmpty()) {
            existing.getComments().addAll(updated.getComments());
        }
        return taskRepository.save(existing);
    }
    
    
    

    // Brisanje zadatka (admin)
    public void deleteTask(Long taskId) {
    	
        taskRepository.deleteById(taskId);
    }
    
    

    // Filtriranje
    public List<Task> findTasksByAuthor(Long authorId) {
    	
        User author = userRepository.findById(authorId).orElse(null);
        
        if (author == null) return List.of();
        
        return taskRepository.findByAuthor(author);
    }

    public List<Task> findTasksByAssignee(Long assigneeId) {
    	
        User assignee = userRepository.findById(assigneeId).orElse(null);
        
        if (assignee == null) return List.of();
        
        return taskRepository.findByAssignee(assignee);
    }


    public List<Task> findAll() {
    	
        return taskRepository.findAll(); //  Fetch all tasks from the database
    }


    public List<Task> findAllForUser(Long id) {
    	
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        return taskRepository.findByUser(user); //  Fetch tasks by user
    }


	public Task findById(Long taskId) {
		
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
    }
	
	
	}
    




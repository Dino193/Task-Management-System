package task_management_system.repository;


import java.util.List;
import task_management_system.entity.Task;
import task_management_system.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
	
    List<Task> findByAuthor(User author);
    
    List<Task> findByAssignee(User assignee);

	List<Task> findByUser(User user);
    
}


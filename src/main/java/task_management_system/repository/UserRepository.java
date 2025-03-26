package task_management_system.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import task_management_system.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	
	
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    
    
}
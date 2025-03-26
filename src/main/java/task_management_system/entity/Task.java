package task_management_system.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="tasks")
public class Task {
	
	
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;     // npr. "pending", "in_progress", "done"
    private String priority;   // npr. "low", "medium", "high"

    @ManyToOne
    @JoinColumn(name="author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name="assignee_id")
    private User assignee;  // izvršilac (moze biti null)

    // Komentari
    @ElementCollection
    @CollectionTable(name="task_comments", joinColumns=@JoinColumn(name="task_id"))
    @Column(name="comment")
    private List<String> comments = new ArrayList<>();

	

    @ManyToOne
    @JoinColumn(name = "user_id") 
    private User user; 
    
    public Task() {
		
	}

	public Task(String title, String description, String status, String priority, User author, User assignee,
			List<String> comments, User user) {
		super();
		this.title = title;
		this.description = description;
		this.status = status;
		this.priority = priority;
		this.author = author;
		this.assignee = assignee;
		this.comments = comments;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title + ", description=" + description + ", status=" + status
				+ ", priority=" + priority + ", author=" + author + ", assignee=" + assignee + ", comments=" + comments
				+ ", user=" + user + "]";
	}

}
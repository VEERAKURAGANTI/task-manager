package com.taskmanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="tasks")
public class Task {
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Title is requeired")
	@Size(max=100,message="Title max 100 characters")
	@Column(nullable = false)
	private String title;
	
	@Column(length = 500)
	private String description;
	
	private String assignee;
	
	private String tags;
	
	private LocalDate dueDate;


	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TaskPriority priority = TaskPriority.MEDIUM;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TaskStatus status = TaskStatus.TODO;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDate createdAt;
	
	@UpdateTimestamp
	private LocalDate updatedAt;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	@JsonIgnore
	private Task parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
	           orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Task> subtask = new ArrayList<>();

	public enum TaskPriority {
		LOW, MEDIUM, HIGH, CRITICAL
	}

	public enum TaskStatus {
		TODO, IN_PROGRESS, DONE
	}

	// Helper method (Composite)
	public boolean isComposite() {
		return subtask != null && !subtask.isEmpty();
	}

	public Task() {
	}

	public Task(String title, String description, String assignee, String tags, LocalDate dueDate,
			TaskPriority priority, TaskStatus status) {

		this.title = title;
		this.description = description;
		this.assignee = assignee;
		this.tags = tags;
		this.dueDate = dueDate;
		this.priority = priority;
		this.status = status;
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

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public TaskPriority getPriority() {
		return priority;
	}

	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Task getParent() {
		return parent;
	}

	public void setParent(Task parent) {
		this.parent = parent;
	}

	public List<Task> getSubtask() {
		return subtask;
	}

	public void setSubtask(List<Task> subtask) {
		this.subtask = subtask;
	}
	

}

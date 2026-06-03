package com.taskmanager.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskStatus;
import com.taskmanager.observer.TaskEvent;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.state.TaskState;
import com.taskmanager.strategy.TaskSortStrategy;



@Service
@Transactional
public class TaskService {
	private final TaskRepository taskRepository;
	private final ApplicationEventPublisher eventPublisher;

	private final TaskSortStrategy sortByDueDate;
	private final TaskSortStrategy sortByPriority;

	private final TaskState todoState;
	private final TaskState inProgressState;
	private final TaskState doneState;

	public TaskService(TaskRepository taskRepository, ApplicationEventPublisher eventPublisher,
			@Qualifier("sortByDueDate") TaskSortStrategy sortByDueDate,
			@Qualifier("sortByPriority") TaskSortStrategy sortByPriority, @Qualifier("todoState") TaskState todoState,
			@Qualifier("inProgressState") TaskState inProgressState, @Qualifier("doneState") TaskState doneState) {

		this.taskRepository = taskRepository;
		this.eventPublisher = eventPublisher;
		this.sortByDueDate = sortByDueDate;
		this.sortByPriority = sortByPriority;
		this.todoState = todoState;
		this.inProgressState = inProgressState;
		this.doneState = doneState;
	}

	
	public List<Task> getAllTasks(String sortBy) {
		List<Task> tasks = taskRepository.findByParentIsNull();

		if ("priority".equalsIgnoreCase(sortBy)) {
			return sortByPriority.sort(tasks);
		} else if ("dueDate".equalsIgnoreCase(sortBy)) {
			return sortByDueDate.sort(tasks);
		}
		return tasks;
	}

	public Task getTaskById(Long id) {
		return taskRepository.findById(id)
			    .orElseThrow(() ->
		        new TaskNotFoundException(id));
	}

	public Task createTask(Task task) {

		task.setStatus(TaskStatus.TODO);
		Task saved = taskRepository.save(task);
		eventPublisher.publishEvent(new TaskEvent(this, saved, "CREATED"));
		return saved;
	}

	public Task updateTask(Long id, Task updated) {
		Task task = getTaskById(id);
		task.setTitle(updated.getTitle());
		task.setDescription(updated.getDescription());
		task.setPriority(updated.getPriority());
		task.setDueDate(updated.getDueDate());
		task.setAssignee(updated.getAssignee());
		task.setTags(updated.getTags());
		task.setStatus(updated.getStatus());
		Task saved = taskRepository.save(task);

		eventPublisher.publishEvent(new TaskEvent(this, saved, "UPDATED"));

		return saved;
	}

	public void deleteTask(Long id) {
		Task task = getTaskById(id);
		taskRepository.deleteById(id);
		eventPublisher.publishEvent(new TaskEvent(this, task, "DELETED"));
	}

	public Task startProgress(long id) {
		Task task = getTaskById(id);
		TaskState state = resolveState(task.getStatus());
		state.startProgress(task); 
		Task saved = taskRepository.save(task);

		eventPublisher.publishEvent(new TaskEvent(this, saved, "STATUS → IN_PROGRESS"));
		return saved;

	}
	 public Task completeTask(Long id) {
	        Task task  = getTaskById(id);
	        TaskState state = resolveState(task.getStatus());
	        state.completeTask(task);        
	        Task saved = taskRepository.save(task);

	        eventPublisher.publishEvent(
	            new TaskEvent(this, saved, "STATUS → DONE"));
	        return saved;
	    }

	    public Task reopenTask(Long id) {
	        Task task  = getTaskById(id);
	        TaskState state = resolveState(task.getStatus());
	        state.reopenTask(task);           
	        Task saved = taskRepository.save(task);

	        eventPublisher.publishEvent(
	            new TaskEvent(this, saved, "STATUS → TODO"));
	        return saved;
	    }

	

	    public Task addSubtask(Long parentId, Task subtask) {
	        Task parent = getTaskById(parentId);
	        subtask.setId(null);
	        subtask.setParent(parent);
	        parent.getSubtask().add(subtask); 
	        subtask.setStatus(TaskStatus.TODO);
	        subtask.setAssignee(parent.getAssignee());
	        Task saved = taskRepository.save(subtask);

	        eventPublisher.publishEvent(
	            new TaskEvent(this, saved, "SUBTASK ADDED"));
	        return saved;
	    }

	    public void deleteSubtask(Long subtaskId) {
	        Task subtask = getTaskById(subtaskId);

	       
	        if (subtask.getParent() != null) {
	            subtask.getParent().getSubtask().remove(subtask);
	        }

	        taskRepository.deleteById(subtaskId);

	        eventPublisher.publishEvent(
	            new TaskEvent(this, subtask, "SUBTASK DELETED"));
	    }
	    
	    public List<Task> getSubtasks(Long parentId) {
	        return taskRepository.findByParentId(parentId);
	    }

	    

	    public List<Task> getByStatus(TaskStatus status) {
	        return taskRepository.findByStatus(status);
	    }

	    public List<Task> getByPriority(Task.TaskPriority priority) {
	        return taskRepository.findByPriority(priority);
	    }

	    public List<Task> searchByTitle(String keyword) {
	        return taskRepository.findByTitleContainingIgnoreCase(keyword);
	    }

	    public List<Task> getOverdueTasks() {
	        return taskRepository.findOverdueTasks(LocalDate.now());
	    }


	// picks the right State object based on current task status
	private TaskState resolveState(TaskStatus status) {
		return switch (status) {
		case TODO -> todoState;
		case IN_PROGRESS -> inProgressState;
		case DONE -> doneState;
		};
	}

}

package com.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tasks")
public class TaskController {
	private final TaskService taskService;

	public TaskController(TaskService taskService) {

		this.taskService = taskService;
	}
	
	@GetMapping
	public String listTasks(@RequestParam(required = false) String sortBy,
			@RequestParam(required = false) String status, @RequestParam(required = false) String keyword,
			Model model) {
		// decide which list to show based on query params
		if (keyword != null && !keyword.isEmpty()) {
			model.addAttribute("tasks", taskService.searchByTitle(keyword));
			model.addAttribute("keyword", keyword);
		} else if (status != null && !status.isEmpty()) {
			model.addAttribute("tasks", taskService.getByStatus(Task.TaskStatus.valueOf(status)));
			model.addAttribute("activeStatus", status);
		} else {
			// Strategy pattern
			model.addAttribute("tasks", taskService.getAllTasks(sortBy));
			model.addAttribute("activeSortBy", sortBy);
		}
		// pass enum values to template for filter buttons
		model.addAttribute("statuses", Task.TaskStatus.values());
		model.addAttribute("priorities", Task.TaskPriority.values());

		return "tasks/list"; // → templates/tasks/list.html
	}

	@GetMapping("/new")
	public String showCreateForm(Model model) {
		model.addAttribute("task", new Task());
		model.addAttribute("priorities", Task.TaskPriority.values());
		model.addAttribute("statuses", Task.TaskStatus.values());
		model.addAttribute("formTitle", "Create New Task");
		return "tasks/form"; // → templates/tasks/form.html
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Long id, Model model) {
		model.addAttribute("task", taskService.getTaskById(id));
		model.addAttribute("priorities", Task.TaskPriority.values());
		model.addAttribute("statuses", Task.TaskStatus.values());
		model.addAttribute("formTitle", "Edit Task");
		return "tasks/form"; // reuses same form.html
	}

	@PostMapping("/save")
	public String saveTask(@Valid @ModelAttribute Task task, BindingResult result, Model model,
			RedirectAttributes redirectAttrs) {

		if (result.hasErrors()) {
			model.addAttribute("priorities", Task.TaskPriority.values());
			model.addAttribute("statuses", Task.TaskStatus.values());
			model.addAttribute("formTitle", "Create New Task");
			return "tasks/form"; // go back to form with errors
		}

		taskService.createTask(task);

		// flash message shown after redirect
		redirectAttrs.addFlashAttribute("successMessage", "Task created successfully!");

		return "redirect:/tasks";
	}

	@PostMapping("/update/{id}")
	public String updateTask(@PathVariable Long id, @Valid @ModelAttribute Task task, BindingResult result, Model model,
			RedirectAttributes redirectAttrs) {

		if (result.hasErrors()) {
			model.addAttribute("priorities", Task.TaskPriority.values());
			model.addAttribute("statuses", Task.TaskStatus.values());
			model.addAttribute("formTitle", "Edit Task");
			return "tasks/form";
		}

		taskService.updateTask(id, task);

		redirectAttrs.addFlashAttribute("successMessage", "Task updated successfully!");

		return "redirect:/tasks";
	}

	@GetMapping("/delete/{id}")
	public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttrs) {
		taskService.deleteTask(id);
        System.out.println("end point hit");
		redirectAttrs.addFlashAttribute("successMessage", "Task deleted successfully!");

		return "redirect:/tasks";
	}

	@GetMapping("/start/{id}")
	public String startProgress(@PathVariable Long id, RedirectAttributes redirectAttrs) {
		try {
			taskService.startProgress(id);
			redirectAttrs.addFlashAttribute("successMessage", "Task started!");
		} catch (IllegalStateException e) {
			// State pattern throws this if move is invalid
			redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/tasks";
	}

	@GetMapping("/complete/{id}")
	public String completeTask(@PathVariable Long id, RedirectAttributes redirectAttrs) {
		try {
			taskService.completeTask(id);
			redirectAttrs.addFlashAttribute("successMessage", "Task completed!");
		} catch (IllegalStateException e) {
			redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/tasks";
	}

	@GetMapping("/reopen/{id}")
	public String reopenTask(@PathVariable Long id, RedirectAttributes redirectAttrs) {
		try {
			taskService.reopenTask(id);
			redirectAttrs.addFlashAttribute("successMessage", "Task reopened!");
		} catch (IllegalStateException e) {
			redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/tasks";
	}

	@GetMapping("/{id}/subtasks")
	public String viewSubtasks(@PathVariable Long id, Model model) {
		Task parent = taskService.getTaskById(id);
		model.addAttribute("parent", parent);
		model.addAttribute("subtasks", taskService.getSubtasks(id));
		model.addAttribute("newSubtask", new Task());
		model.addAttribute("priorities", Task.TaskPriority.values());
		return "tasks/subtasks";
	}

	@PostMapping("/{id}/subtasks/add")
	public String addSubtask(@PathVariable Long id, @ModelAttribute Task subtask, RedirectAttributes redirectAttrs) {

		taskService.addSubtask(id, subtask);

		redirectAttrs.addFlashAttribute("successMessage", "Subtask added!");

		return "redirect:/tasks/" + id + "/subtasks";
	}
	
	
	@GetMapping("/{parentId}/subtasks/delete/{subtaskId}")
	public String deleteSubtask(@PathVariable Long parentId,
	                            @PathVariable Long subtaskId,
	                            RedirectAttributes redirectAttrs) {
	    try {
	        taskService.deleteSubtask(subtaskId);
	        redirectAttrs.addFlashAttribute("successMessage",
	            "Subtask deleted successfully!");
	    } catch (Exception e) {
	        redirectAttrs.addFlashAttribute("errorMessage",
	            "Could not delete subtask: " + e.getMessage());
	    }
	    return "redirect:/tasks/" + parentId + "/subtasks";
	}
	

	@GetMapping("/")
	public String home() {
		return "redirect:/tasks";
	}

}

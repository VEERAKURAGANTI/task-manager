package com.taskmanager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class GlobalExceptionHandler {

	  private static final Logger log =
		        LoggerFactory.getLogger(GlobalExceptionHandler.class);
	  
	  @ExceptionHandler(TaskNotFoundException.class)
	    public String handleTaskNotFound(TaskNotFoundException ex,
	                                     Model model) {
	        log.error("Task not found: {}", ex.getMessage());
	        model.addAttribute("errorCode",    "404");
	        model.addAttribute("errorTitle",   "Task Not Found");
	        model.addAttribute("errorMessage", ex.getMessage());
	        return "error";
	    }
	  @ExceptionHandler(IllegalStateException.class)
	    public String handleIllegalState(IllegalStateException ex,
	                                     Model model) {
	        log.error("Invalid state transition: {}", ex.getMessage());
	        model.addAttribute("errorCode",    "400");
	        model.addAttribute("errorTitle",   "Invalid Action");
	        model.addAttribute("errorMessage", ex.getMessage());
	        return "error";
	    }

	 
	    @ExceptionHandler(Exception.class)
	    public String handleGeneral(Exception ex, Model model) {
	        log.error("Unexpected error: {}", ex.getMessage());
	        model.addAttribute("errorCode",    "500");
	        model.addAttribute("errorTitle",   "Something went wrong");
	        model.addAttribute("errorMessage",
	            "An unexpected error occurred. Please try again.");
	        return "error";
	    }
}

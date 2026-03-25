package com.taskmanager.state;


import org.springframework.stereotype.Component;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskStatus;

@Component("todoState")
public class TodoState implements TaskState {

	@Override
	public void startProgress(Task task) {

		task.setStatus(Task.TaskStatus.IN_PROGRESS);
	}

	@Override
	public void completeTask(Task task) {
		throw new IllegalStateException("Cannot complete a task that has not started yet. Click Start first.");

	}

	@Override
	public void reopenTask(Task task) {
		throw new IllegalStateException("Task is already open.");

	}

	@Override
	public TaskStatus getStatus() {

		return Task.TaskStatus.TODO;
	}

}

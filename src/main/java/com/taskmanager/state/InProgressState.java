package com.taskmanager.state;


import org.springframework.stereotype.Component;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskStatus;

@Component("inProgressState")
public class InProgressState implements TaskState {

	@Override
	public void startProgress(Task task) {

		throw new IllegalStateException("Task is already in progress.");
	}

	@Override
	public void completeTask(Task task) {

		task.setStatus(Task.TaskStatus.DONE);
	}

	@Override
	public void reopenTask(Task task) {
		task.setStatus(Task.TaskStatus.TODO);

	}

	@Override
	public TaskStatus getStatus() {

		return Task.TaskStatus.IN_PROGRESS;
	}

}

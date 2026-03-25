package com.taskmanager.state;


import org.springframework.stereotype.Component;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskStatus;

@Component("doneState")
public class DoneState implements TaskState {

	@Override
	public void startProgress(Task task) {
		throw new IllegalStateException("Task is already completed.");
	}

	@Override
	public void completeTask(Task task) {
		throw new IllegalStateException("Task is already completed.");
	}

	@Override
	public void reopenTask(Task task) {
		task.setStatus(Task.TaskStatus.TODO);
	}

	@Override
	public TaskStatus getStatus() {

		return Task.TaskStatus.DONE;
	}

}

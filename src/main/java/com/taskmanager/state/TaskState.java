package com.taskmanager.state;

import com.taskmanager.model.Task;

public interface TaskState {

	void startProgress(Task task);

	void completeTask(Task task);

	void reopenTask(Task task);

	Task.TaskStatus getStatus();
}

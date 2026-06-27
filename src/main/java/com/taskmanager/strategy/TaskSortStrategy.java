package com.taskmanager.strategy;

import java.util.List;
import com.taskmanager.model.Task;

public interface TaskSortStrategy {

	List<Task> sort(List<Task> tasks);
}

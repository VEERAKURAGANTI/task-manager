package com.taskmanager.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Component;

import com.taskmanager.model.Task;

@Component("sortByPriority")
public class SortByPriority implements TaskSortStrategy {

	public static final List<Task.TaskPriority> ORDER = List.of(Task.TaskPriority.CRITICAL, Task.TaskPriority.HIGH,
			Task.TaskPriority.MEDIUM, Task.TaskPriority.LOW);

	@Override
	public List<Task> sort(List<Task> tasks) {

		return tasks.stream().sorted(Comparator.comparingInt(t -> ORDER.indexOf(t.getPriority())))
				.collect(Collectors.toList());
	}

}

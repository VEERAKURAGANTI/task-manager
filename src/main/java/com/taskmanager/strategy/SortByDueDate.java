package com.taskmanager.strategy;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Component;

import com.taskmanager.model.Task;

@Component("sortByDueDate")
public class SortByDueDate implements TaskSortStrategy {

	@Override
	public List<Task> sort(List<Task> tasks) {

		return tasks.stream()
				.sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList());
	}

}

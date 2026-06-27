package com.taskmanager.strategy;
//import the List class
import java.util.List;
//import the Task class from the model package
import com.taskmanager.model.Task;
//sort the task
public interface TaskSortStrategy {
	//define the sort  abstarct method
	List<Task> sort(List<Task> tasks);
}

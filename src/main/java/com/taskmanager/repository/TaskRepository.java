package com.taskmanager.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskPriority;
import com.taskmanager.model.Task.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ── Basic finders 

    // find all tasks by status  e.g. TODO / IN_PROGRESS / DONE
    List<Task> findByStatus(TaskStatus status);

    // find all tasks by priority  e.g. HIGH / CRITICAL
    List<Task> findByPriority(TaskPriority priority);

    // find only top-level tasks (no parent) — excludes subtasks
    List<Task> findByParentIsNull();

    // find all subtasks of a specific parent task
    List<Task> findByParentId(Long parentId);

    // ── Search ─────────────────────────────────────────

    // search tasks by keyword in title (case-insensitive)
    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // ── Due date queries ───────────────────────────────

    // find tasks due before a certain date  e.g. overdue tasks
    List<Task> findByDueDateBefore(LocalDate date);

    // find tasks due between two dates
    List<Task> findByDueDateBetween(LocalDate start, LocalDate end);

    // ── Custom JPQL queries ────────────────────────────

    // find tasks by assignee name
    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee")
    List<Task> findByAssignee(@Param("assignee") String assignee);

    // count tasks grouped by status  — useful for dashboard
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countByStatus();

    // find all overdue tasks that are not yet DONE
    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);
}

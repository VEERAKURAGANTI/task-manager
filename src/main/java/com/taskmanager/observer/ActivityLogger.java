package com.taskmanager.observer;




import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taskmanager.model.Task;

@Component
public class ActivityLogger {
   private static final Logger log= LoggerFactory.getLogger(ActivityLogger.class);
   
   @EventListener
   public void onTaskEvent(TaskEvent event) {
       Task task = event.getTask();

       log.info("─────────────────────────────────────────");
       log.info("  ACTION   : {}", event.getAction());
       log.info("  Task ID  : {}", task.getId());
       log.info("  Title    : {}", task.getTitle());
       log.info("  Status   : {}", task.getStatus());
       log.info("  Priority : {}", task.getPriority());
       log.info("  Time     : {}", LocalDateTime.now());
       log.info("─────────────────────────────────────────");
   }
}

package com.bbzbl.task.services;

import com.bbzbl.task.data.entity.Task;
import com.bbzbl.task.data.repository.TaskRepository;
import com.bbzbl.task.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling task-related operations such as create, read, update, and delete.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AuthenticatedUser authenticatedUser;

    /**
     * Constructor for injecting dependencies.
     *
     * @param taskRepository     repository for task data access
     * @param authenticatedUser  current authenticated user provider
     */
    public TaskService(TaskRepository taskRepository, AuthenticatedUser authenticatedUser) {
        this.taskRepository = taskRepository;
        this.authenticatedUser = authenticatedUser;
    }

    /**
     * Creates a new task for the currently authenticated user.
     *
     * @param title       the title of the task
     * @param description the task description
     * @param duration    estimated time to complete
     * @param status      current status of the task
     * @param priority    priority level of the task
     * @param dueDate     due date of the task
     * @return the saved {@link Task}
     * @throws RuntimeException if no user is authenticated
     */
    public Task createTask(String title, String description, String duration, String status, String priority, LocalDate dueDate) {
        return authenticatedUser.get().map(user -> {
            Task task = new Task(title, description, priority, user);
            task.setDuration(duration);
            task.setStatus(status != null ? status : "Offen");
            task.setDueDate(dueDate);
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("User not authenticated"));
    }

    /**
     * Retrieves all tasks for the currently authenticated user.
     *
     * @return list of {@link Task}s
     * @throws RuntimeException if no user is authenticated
     */
    public List<Task> getUserTasks() {
        return authenticatedUser.get()
                .map(user -> taskRepository.findByOwnerUsername(user.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     */
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @return an Optional containing the task if found
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Updates a task with new values if provided.
     *
     * @param taskId      the ID of the task to update
     * @param title       new title (nullable)
     * @param description new description (nullable)
     * @param duration    new duration (nullable)
     * @param status      new status (nullable)
     * @param priority    new priority (nullable)
     * @param dueDate     new due date (nullable)
     * @throws RuntimeException if the task is not found
     */
    public void updateTask(Long taskId, String title, String description, String duration, String status, String priority, LocalDate dueDate) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (title != null) task.setTitle(title);
        if (description != null) task.setDescription(description);
        if (duration != null) task.setDuration(duration);
        if (status != null) task.setStatus(status);
        if (priority != null) task.setPriority(priority);
        if (dueDate != null) task.setDueDate(dueDate);

        taskRepository.save(task);
    }
}

package com.bbzbl.task.data.repository;

import com.bbzbl.task.data.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for accessing and managing {@link Task} entities.
 * Provides basic CRUD operations and custom queries.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Retrieves all tasks associated with the specified owner's username.
     *
     * @param username the username of the task owner
     * @return list of tasks belonging to the specified user
     */
    List<Task> findByOwnerUsername(String username);
}

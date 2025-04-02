package com.bbzbl.task.data.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task entity with attributes such as title, description,
 * priority, due date, duration, and owner.
 */
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String duration;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    @ManyToOne
    private User owner;

    /**
     * Default constructor initializing the creation timestamp and default values.
     */
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.status = "Offen";
        this.dueDate = LocalDate.now();
        this.duration = "0h";
    }

    /**
     * Constructs a new Task with the provided values.
     *
     * @param title       the title of the task
     * @param description the description of the task
     * @param priority    the priority level
     * @param owner       the user who owns the task
     */
    public Task(String title, String description, String priority, User owner) {
        this();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.owner = owner;
    }

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}

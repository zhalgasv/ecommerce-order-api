package com.zhalgas.taskmanager.task;

import jakarta.persistence.*;
import com.zhalgas.taskmanager.user.User;
import com.zhalgas.taskmanager.category.Category;

@Entity
@Table(name = "tasks")
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(length = 2000)
    private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private TaskStatus status;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    protected Task() {
    }

    public Task(String title, String description, TaskStatus status, User user, Category category) {
        update(title, description, status, user, category);
    }

    public void update(String title, String description, TaskStatus status, User user, Category category) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.user = user;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public Category getCategory() {
        return category;
    }
}

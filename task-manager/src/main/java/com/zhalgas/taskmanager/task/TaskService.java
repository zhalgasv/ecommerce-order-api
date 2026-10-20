package com.zhalgas.taskmanager.task;

import com.zhalgas.taskmanager.user.User;
import com.zhalgas.taskmanager.user.UserRepository;
import com.zhalgas.taskmanager.category.Category;
import com.zhalgas.taskmanager.category.CategoryRepository;
import com.zhalgas.taskmanager.exception.NotFoundException;
import com.zhalgas.taskmanager.config.QueryValidation;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository repository;
    private final UserRepository users;
    private final CategoryRepository categories;
    public TaskService(TaskRepository repository, UserRepository users, CategoryRepository categories) {
        this.repository = repository;
        this.users = users;
        this.categories = categories;
    }

    public Page<TaskResponse> list(Long categoryId, Long userId, Pageable pageable) {
        QueryValidation.validate(pageable, Set.of("id", "title", "status"));
        if (categoryId != null) category(categoryId);
        if (userId != null) user(userId);
        return repository.search(categoryId, userId, pageable).map(TaskResponse::from);
    }

    public TaskResponse get(Long id) {
        return TaskResponse.from(find(id));
    }
    private Task find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    }
    private User user(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }
    private Category category(Long id) {
        return categories.findById(id).orElseThrow(() -> new NotFoundException("Category", id));
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        return TaskResponse.from(repository.save(new Task(request.title().strip(), request.description(), request.status(), user(request.userId()), category(request.categoryId()))));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = find(id);
        task.update(request.title().strip(), request.description(), request.status(), user(request.userId()), category(request.categoryId()));
        return TaskResponse.from(task);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }
}

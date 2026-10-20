package com.zhalgas.taskmanager.user;

import com.zhalgas.taskmanager.exception.NotFoundException;
import com.zhalgas.taskmanager.exception.ConflictException;
import com.zhalgas.taskmanager.task.TaskRepository;
import com.zhalgas.taskmanager.config.QueryValidation;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository repository;
    private final TaskRepository tasks;
    public UserService(UserRepository repository, TaskRepository tasks) {
        this.repository = repository;
        this.tasks = tasks;
    }

    public Page<UserResponse> list(Pageable pageable) {
        QueryValidation.validate(pageable, Set.of("id", "name", "email"));
        return repository.findAll(pageable).map(UserResponse::from);
    }

    public UserResponse get(Long id) {
        return UserResponse.from(find(id));
    }
    private User find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        return UserResponse.from(repository.save(new User(request.name().strip(), request.email().strip().toLowerCase(java.util.Locale.ROOT))));
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User entity = find(id);
        entity.setName(request.name().strip());
        entity.setEmail(request.email().strip().toLowerCase(java.util.Locale.ROOT));
        return UserResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        User entity = find(id);
        if (tasks.existsByUserId(id)) throw new ConflictException("Delete this user's tasks first");
        repository.delete(entity);
    }
}

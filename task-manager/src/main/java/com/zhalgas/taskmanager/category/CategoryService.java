package com.zhalgas.taskmanager.category;

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
public class CategoryService {
    private final CategoryRepository repository;
    private final TaskRepository tasks;
    public CategoryService(CategoryRepository repository, TaskRepository tasks) {
        this.repository = repository;
        this.tasks = tasks;
    }

    public Page<CategoryResponse> list(Pageable pageable) {
        QueryValidation.validate(pageable, Set.of("id", "name"));
        return repository.findAll(pageable).map(CategoryResponse::from);
    }

    public CategoryResponse get(Long id) {
        return CategoryResponse.from(find(id));
    }
    private Category find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Category", id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        return CategoryResponse.from(repository.save(new Category(request.name().strip())));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category entity = find(id);
        entity.setName(request.name().strip());
        return CategoryResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Category entity = find(id);
        if (tasks.existsByCategoryId(id)) throw new ConflictException("Delete this category's tasks first");
        repository.delete(entity);
    }
}

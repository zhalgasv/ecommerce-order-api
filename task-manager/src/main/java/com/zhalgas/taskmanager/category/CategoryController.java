package com.zhalgas.taskmanager.category;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping public Page<CategoryResponse> list(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/{id}") public CategoryResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        var result = service.create(request);
        return ResponseEntity.created(URI.create("/api/categories/" + result.id())).body(result);
    }

    @PutMapping("/{id}") public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

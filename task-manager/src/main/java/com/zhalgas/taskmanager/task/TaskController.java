package com.zhalgas.taskmanager.task;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping public Page<TaskResponse> list(@RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long userId,
    @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.list(categoryId, userId, pageable);
    }

    @GetMapping("/{id}") public TaskResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        var result = service.create(request);
        return ResponseEntity.created(URI.create("/api/tasks/" + result.id())).body(result);
    }

    @PutMapping("/{id}") public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

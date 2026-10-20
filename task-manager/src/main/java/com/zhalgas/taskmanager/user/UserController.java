package com.zhalgas.taskmanager.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping public Page<UserResponse> list(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/{id}") public UserResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        var result = service.create(request);
        return ResponseEntity.created(URI.create("/api/users/" + result.id())).body(result);
    }

    @PutMapping("/{id}") public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

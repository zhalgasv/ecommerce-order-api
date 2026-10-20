package com.zhalgas.taskmanager.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t where (:categoryId is null or t.category.id = :categoryId) and (:userId is null or t.user.id = :userId)")
    Page<Task> search(Long categoryId, Long userId, Pageable pageable);
    boolean existsByUserId(Long userId);
    boolean existsByCategoryId(Long categoryId);
}

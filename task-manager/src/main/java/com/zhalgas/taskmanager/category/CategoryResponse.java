package com.zhalgas.taskmanager.category;
public record CategoryResponse(Long id, String name) {
    public static CategoryResponse from(Category entity) {
        return new CategoryResponse(entity.getId(), entity.getName());
    }
}

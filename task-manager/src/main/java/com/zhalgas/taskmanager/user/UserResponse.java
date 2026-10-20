package com.zhalgas.taskmanager.user;
public record UserResponse(Long id, String name, String email) {
    public static UserResponse from(User entity) {
        return new UserResponse(entity.getId(), entity.getName(), entity.getEmail());
    }
}

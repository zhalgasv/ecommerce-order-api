package com.zhalgas.taskmanager.user;
public record UserRequest(@jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 100) String name, @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Email @jakarta.validation.constraints.Size(max = 254) String email) {
}

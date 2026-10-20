package com.zhalgas.taskmanager.category;
public record CategoryRequest(@jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 100) String name) {
}

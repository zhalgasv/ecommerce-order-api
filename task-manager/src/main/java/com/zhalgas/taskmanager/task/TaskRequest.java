package com.zhalgas.taskmanager.task;

import jakarta.validation.constraints.*;

public record TaskRequest(
@NotBlank @Size(max = 200) String title,
@Size(max = 2000) String description,
@NotNull TaskStatus status,
@NotNull @Positive Long userId,
@NotNull @Positive Long categoryId
) {
}

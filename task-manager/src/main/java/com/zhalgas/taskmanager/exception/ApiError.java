package com.zhalgas.taskmanager.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(int status, String message, String path, Instant timestamp, Map<String, String> fields) {
}

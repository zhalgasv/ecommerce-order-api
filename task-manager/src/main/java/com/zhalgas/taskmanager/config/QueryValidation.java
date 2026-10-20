package com.zhalgas.taskmanager.config;

import com.zhalgas.taskmanager.exception.BadRequestException;
import org.springframework.data.domain.Pageable;
import java.util.Set;

public final class QueryValidation {
    private QueryValidation() {
    }

    public static void validate(Pageable pageable, Set<String> allowedFields) {
        for (var order : pageable.getSort()) {
            if (!allowedFields.contains(order.getProperty())) {
                throw new BadRequestException("Unsupported sort field: " + order.getProperty());
            }
        }
    }
}

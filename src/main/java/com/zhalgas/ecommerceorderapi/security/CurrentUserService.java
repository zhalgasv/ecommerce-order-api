package com.zhalgas.ecommerceorderapi.security;

import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Long getCurrentUserId() {
        // TODO: Replace with user id from SecurityContext after JWT authentication is enabled.
        return 1L;
    }
}

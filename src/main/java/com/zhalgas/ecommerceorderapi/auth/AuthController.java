package com.zhalgas.ecommerceorderapi.auth;

import com.zhalgas.ecommerceorderapi.auth.dto.AuthResponse;
import com.zhalgas.ecommerceorderapi.auth.dto.LoginRequest;
import com.zhalgas.ecommerceorderapi.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest);
    }
}

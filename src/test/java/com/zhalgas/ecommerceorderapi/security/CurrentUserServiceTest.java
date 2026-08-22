package com.zhalgas.ecommerceorderapi.security;

import com.zhalgas.ecommerceorderapi.exception.UnauthorizedException;
import com.zhalgas.ecommerceorderapi.user.User;
import com.zhalgas.ecommerceorderapi.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_whenUserIsAuthenticated_returnsUserId() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(token);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Long userId = currentUserService.getCurrentUserId();

        assertEquals(1L, userId);
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void getCurrentUserId_whenAuthenticationIsMissing_throwsUnauthorizedException() {
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> currentUserService.getCurrentUserId()
        );

        assertEquals("User is not authenticated", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    void getCurrentUserId_whenUserDoesNotExist_throwsUnauthorizedException() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(token);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> currentUserService.getCurrentUserId()
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByEmail("user@example.com");
    }
}

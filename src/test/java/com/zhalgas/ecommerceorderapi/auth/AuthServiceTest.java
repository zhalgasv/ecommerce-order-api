package com.zhalgas.ecommerceorderapi.auth;

import com.zhalgas.ecommerceorderapi.auth.dto.AuthResponse;
import com.zhalgas.ecommerceorderapi.auth.dto.LoginRequest;
import com.zhalgas.ecommerceorderapi.auth.dto.RegisterRequest;
import com.zhalgas.ecommerceorderapi.cart.Cart;
import com.zhalgas.ecommerceorderapi.cart.CartRepository;
import com.zhalgas.ecommerceorderapi.exception.BadRequestException;
import com.zhalgas.ecommerceorderapi.security.JwtService;
import com.zhalgas.ecommerceorderapi.user.Role;
import com.zhalgas.ecommerceorderapi.user.User;
import com.zhalgas.ecommerceorderapi.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Mock
    private CartRepository cartRepository;

    @Test
    void register_whenRequestIsValid_returnsAuthResponseWithToken() {
        RegisterRequest request = createRegisterRequest();
        User savedUser = createSavedUser();

        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponse result = authService.register(request);

        assertEquals("jwt-token", result.getToken());
        verify(userRepository).findByEmail("email");
        verify(passwordEncoder).encode("password");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("username", capturedUser.getUsername());
        assertEquals("email", capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals(Role.USER, capturedUser.getRole());

        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(jwtService).generateToken(userDetailsCaptor.capture());

        UserDetails capturedUserDetails = userDetailsCaptor.getValue();
        assertEquals("email", capturedUserDetails.getUsername());
        assertEquals("encodedPassword", capturedUserDetails.getPassword());
        assertTrue(
                capturedUserDetails.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"))
        );

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        verify(cartRepository).save(cartCaptor.capture());
        Cart capturedCart = cartCaptor.getValue();

        assertSame(savedUser, capturedCart.getUser());
    }

    @Test
    void register_whenEmailAlreadyExists_throwsBadRequestException() {
        RegisterRequest request = createRegisterRequest();
        User existingUser = createSavedUser();

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(existingUser));

        assertThrows(BadRequestException.class, () -> authService.register(request));

        verify(userRepository).findByEmail("email");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(UserDetails.class));
        verifyNoInteractions(cartRepository);
    }

    @Test
    void login_whenCredentialsAreValid_returnsAuthResponseWithToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("email");
        request.setPassword("password");

        User user = createSavedUser();

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponse result = authService.login(request);

        assertEquals("jwt-token", result.getToken());

        verify(userRepository).findByEmail("email");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void login_whenEmailDoesNotExist_throwsBadRequestException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("email");
        request.setPassword("password");

        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.login(request));

        verify(userRepository).findByEmail("email");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void login_whenPasswordIsInvalid_throwsBadRequestException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("email");
        request.setPassword("password");

        User user = createSavedUser();

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(request));

        verify(userRepository).findByEmail("email");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("username");
        request.setEmail("email");
        request.setPassword("password");
        return request;
    }

    private User createSavedUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        return user;
    }
}

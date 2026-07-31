package com.zhalgas.ecommerceorderapi.security;

import com.zhalgas.ecommerceorderapi.user.Role;
import com.zhalgas.ecommerceorderapi.user.User;
import com.zhalgas.ecommerceorderapi.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_returnUserDetails() {
        User user = createUser();

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("email");

        assertEquals(user.getEmail(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());

        assertTrue(
                result.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"))
        );
        verify(userRepository).findByEmail("email");
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("email"));

        verify(userRepository).findByEmail("email");
    }

    private User createUser() {
        User user = new User();
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("password");
        user.setRole(Role.USER);
        return user;
    }
}

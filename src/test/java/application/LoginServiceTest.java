package application;

import domain.port.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {
    private AuthService authService;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        authService = Mockito.mock(AuthService.class);
        loginService = new LoginService(authService);
    }

    @Test
    void loginDelegatesToAuthService() {
        when(authService.login("user@example.com", "pw")).thenReturn(true);
        assertTrue(loginService.login("user@example.com", "pw"));
        verify(authService).login("user@example.com", "pw");
    }

    @Test
    void registerDelegatesToAuthService() {
        when(authService.register("a", "b")).thenReturn(false);
        assertFalse(loginService.register("a", "b"));
        verify(authService).register("a", "b");
    }

    @Test
    void logoutDelegates() {
        loginService.logout();
        verify(authService).logout();
    }

    @Test
    void getCurrentUserDelegates() {
        when(authService.getCurrentUser()).thenReturn("me");
        assertEquals("me", loginService.getCurrentUser());
        verify(authService).getCurrentUser();
    }
}

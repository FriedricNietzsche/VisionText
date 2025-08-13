package application;

import domain.port.AuthService;

/**
 * Service for user login, registration, and session management.
 */
public final class LoginService {
    private final AuthService authService;

    public LoginService(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Attempt to log in.
     * @param username the username
     * @param password the password
     * @return login success
     */
    public boolean login(String username, String password) {
        return authService.login(username, password);
    }

    /**
     * Attempt to register.
     * @param username the username
     * @param password the password
     * @return registration success
     */
    public boolean register(String username, String password) {
        return authService.register(username, password);
    }

    /** End user session. */
    public void logout() {
        authService.logout();
    }

    public String getCurrentUser() {
        return authService.getCurrentUser();
    }
}

package application;

import domain.port.AuthService;

public class LoginService {
    private final AuthService authService;

    public LoginService(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(String username, String password) {
        return authService.login(username, password);
    }

    public boolean register(String username, String password) {
        return authService.register(username, password);
    }

    public void logout() {
        authService.logout();
    }

    public String getCurrentUser() {
        return authService.getCurrentUser();
    }
}

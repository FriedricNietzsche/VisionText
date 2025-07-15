package infrastructure;

import domain.port.AuthService;

public class FirebaseAuthService implements AuthService {
    @Override
    public boolean register(String username, String password) {
        // TODO: Implement Firebase Auth REST API registration
        return false;
    }

    @Override
    public boolean login(String username, String password) {
        // TODO: Implement Firebase Auth REST API login
        return false;
    }

    @Override
    public void logout() {
        // TODO: Implement logout logic (clear session/token)
    }

    @Override
    public String getCurrentUser() {
        // TODO: Return current logged-in username or null
        return null;
    }
} 
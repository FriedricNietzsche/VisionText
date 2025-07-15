package domain.port;

public interface AuthService {
    boolean register(String username, String password);
    boolean login(String username, String password);
    void logout();
    String getCurrentUser();
}

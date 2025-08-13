package domain.port;

/**
 * Authentication service port.
 * @null
 */
public interface AuthService {

    /**
     * Attempt to register.
     * @param username the username
     * @param password the password
     * @return registration success
     */
    boolean register(String username, String password);

    /**
     * Attempt to log in.
     * @param username the username
     * @param password the password
     * @return login success
     */
    boolean login(String username, String password);

    /** End user session. */
    void logout();

    /**
     * Getter for current user.
     * @return the user
     */
    String getCurrentUser();
}

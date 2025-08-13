package infrastructure;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import domain.port.AuthService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseAuthService implements AuthService {

    // IMPORTANT: Replace with your actual Firebase Web API Key
    private static final String FIREBASE_API_KEY = "AIzaSyCSXaDGCtEx3bdqRR2Zd30SBTDwLUqqBq0";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final Gson gson;

    private String currentUserEmail;

    public FirebaseAuthService() {
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    @Override
    public void logout() {
        // For client-side Firebase Auth REST API, logout simply means clearing local tokens.
        // Firebase sessions are primarily managed by the ID token's expiration.
        this.currentUserEmail = null;
        System.out.println("User logged out.");
    }

    @Override
    public String getCurrentUser() {
        return currentUserEmail;
    }

    @Override
    public boolean register(String email, String password) {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;

        AuthRequest requestBody = new AuthRequest(email, password);
        RequestBody body = RequestBody.create(gson.toJson(requestBody), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBodyString = null;
            if (response.body() != null) {
                responseBodyString = response.body().string();
            }

            if (response.isSuccessful()) {
                AuthSuccessResponse authResponse = gson.fromJson(responseBodyString, AuthSuccessResponse.class);
                this.currentUserEmail = authResponse.email;
                System.out.println("Registration successful for: " + currentUserEmail);
                return true;
            }
            else {
                AuthErrorResponse errorResponse = gson.fromJson(responseBodyString, AuthErrorResponse.class);
                String errorMessage = errorResponse.error.message;
                System.err.println("Registration failed. Status: " + response.code() + ", Error: " + errorMessage);
                return false;
            }
        }
        catch (IOException ex) {
            System.err.println("Error during registration: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean login(String email, String password) {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;

        AuthRequest requestBody = new AuthRequest(email, password);
        RequestBody body = RequestBody.create(gson.toJson(requestBody), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBodyString = null;
            if (response.body() != null) {
                responseBodyString = response.body().string();
            }

            if (response.isSuccessful()) {
                AuthSuccessResponse authResponse = gson.fromJson(responseBodyString, AuthSuccessResponse.class);
                this.currentUserEmail = authResponse.email;
                System.out.println("Login successful for: " + currentUserEmail);
                return true;
            }
            else {
                AuthErrorResponse errorResponse = gson.fromJson(responseBodyString, AuthErrorResponse.class);
                String errorMessage = errorResponse.error.message;
                System.err.println("Login failed. Status: " + response.code() + ", Error: " + errorMessage);
                return false;
            }
        }
        catch (IOException ex) {
            System.err.println("Error during login: " + ex.getMessage());
            return false;
        }
    }

    // --- Helper classes for JSON serialization/deserialization ---

    // Request body for registration and login
    private static class AuthRequest {
        @SerializedName("email")
        private final String email;

        @SerializedName("password")
        private final String password;

        // Firebase recommends returnSecureToken=true
        @SerializedName("returnSecureToken")
        private final boolean returnSecureToken = true;

        AuthRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

    }

    // Response body for successful registration/login
    private static final class AuthSuccessResponse {
        @SerializedName("email")
        private final String email;

        private AuthSuccessResponse(String email) {
            this.email = email;
        }
    }

    // Error response body from Firebase Auth API
    /* Right now error will just show up in the console instead of being handled in the app. Example console outputs:
    Registration failed. Status: 400, Error: WEAK_PASSWORD : Password should be at least 6 characters
    Login failed. Status: 400, Error: INVALID_LOGIN_CREDENTIALS
    */
    private static final class AuthErrorResponse {
        private Error error;

        static class Error {
            private int code;
            private final String message;

            Error(String message) {
                this.message = message;
            }
        }
    }
}

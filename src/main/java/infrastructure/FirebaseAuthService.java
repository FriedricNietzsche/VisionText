package infrastructure;

import domain.port.AuthService;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okhttp3.*;

import java.io.IOException;

public class FirebaseAuthService implements AuthService {

    // IMPORTANT: Replace with your actual Firebase Web API Key
    private static final String FIREBASE_API_KEY = "AIzaSyCSXaDGCtEx3bdqRR2Zd30SBTDwLUqqBq0";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final Gson gson;

    private String currentUserEmail = null;

    public FirebaseAuthService() {
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    // --- Helper classes for JSON serialization/deserialization ---

    // Request body for registration and login
    private static class AuthRequest {
        String email;
        String password;

        public AuthRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    // Response body for successful registration/login
    private static class AuthSuccessResponse {
        @SerializedName("email")
        String email;
    }

    // Error response body from Firebase Auth API
    /* Right now error will just show up in the console instead of being handled in the app. Example console outputs:
    Registration failed. Status: 400, Error: WEAK_PASSWORD : Password should be at least 6 characters
    Login failed. Status: 400, Error: INVALID_LOGIN_CREDENTIALS
    */
    private static class AuthErrorResponse {
        Error error;

        static class Error {
            int code;
            String message;
            // Add other fields if needed, like errors list
        }
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
            String responseBodyString = response.body() != null ? response.body().string() : "";

            if (response.isSuccessful()) {
                AuthSuccessResponse authResponse = gson.fromJson(responseBodyString, AuthSuccessResponse.class);
                this.currentUserEmail = authResponse.email;
                System.out.println("Registration successful for: " + currentUserEmail);
                return true;
            } else {
                AuthErrorResponse errorResponse = gson.fromJson(responseBodyString, AuthErrorResponse.class);
                String errorMessage = (errorResponse != null && errorResponse.error != null) ?
                        errorResponse.error.message : "Unknown error";
                System.err.println("Registration failed. Status: " + response.code() + ", Error: " + errorMessage);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error during registration: " + e.getMessage());
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
            String responseBodyString = response.body() != null ? response.body().string() : "";

            if (response.isSuccessful()) {
                AuthSuccessResponse authResponse = gson.fromJson(responseBodyString, AuthSuccessResponse.class);
                this.currentUserEmail = authResponse.email;
                System.out.println("Login successful for: " + currentUserEmail);
                return true;
            } else {
                AuthErrorResponse errorResponse = gson.fromJson(responseBodyString, AuthErrorResponse.class);
                String errorMessage = (errorResponse != null && errorResponse.error != null) ?
                        errorResponse.error.message : "Unknown error";
                System.err.println("Login failed. Status: " + response.code() + ", Error: " + errorMessage);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
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
}
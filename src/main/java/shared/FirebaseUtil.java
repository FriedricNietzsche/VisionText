package shared;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Utility class for Firebase HTTP operations.
 */
public class FirebaseUtil {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final String UNEXPECTED_CODE = "Unexpected code ";

    /**
     * Sends a POST request to the specified URL with the given JSON body.
     * @param url the URL to send the request to
     * @param json the JSON body to include in the request
     * @return the response body as a string
     * @throws IOException if an I/O error occurs
     */
    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(UNEXPECTED_CODE + response);
            }
            return response.body() == null ? "" : response.body().string();
        }
    }

    /**
     * Sends a GET request to the specified URL.
     * @param url the URL to send the request to
     * @return the response body as a string
     * @throws IOException if an I/O error occurs
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(UNEXPECTED_CODE + response);
            }
            return response.body() == null ? "" : response.body().string();
        }
    }

    /**
     * Sends a DELETE request to the specified URL.
     * @param url the URL to send the request to
     * @return the response body as a string
     * @throws IOException if an I/O error occurs
     */
    public static String delete(String url) throws IOException {
        Request request = new Request.Builder().url(url).delete().build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(UNEXPECTED_CODE + response);
            }
            return response.body() == null ? "" : response.body().string();
        }
    }
}

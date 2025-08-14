package infrastructure;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import domain.port.OCRProcessor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import shared.Config;

/**
 * OCR.space API service implementation.
 */
public class OCRSpaceService implements OCRProcessor {
    private static final String OCR_URL = "https://api.ocr.space/parse/image";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();

    @Override
    public String extractTextFromImage(File imageFile) throws Exception {
        String apiKey = Config.getOcrApiKey();
        if (apiKey.isEmpty()) {
            throw new Exception("OCR.space API key not set in config.properties");
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("apikey", apiKey)
                .addFormDataPart("language", "eng")
                .addFormDataPart("file", imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/*")))
                .build();
        Request request = new Request.Builder()
                .url(OCR_URL)
                .post(requestBody)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OCR API HTTP error: " + response.code());
            }
            String json = null;
            if (response.body() != null) {
                json = response.body().string();
            }
            JsonObject obj = GSON.fromJson(json, JsonObject.class);
            if (obj.has("IsErroredOnProcessing") && obj.get("IsErroredOnProcessing").getAsBoolean()) {
                String msg = obj.has("ErrorMessage") ? obj.get("ErrorMessage").toString() : "Unknown error";
                throw new Exception("OCR API error: " + msg);
            }
            JsonArray results = obj.getAsJsonArray("ParsedResults");
            if (results != null && !results.isEmpty()) {
                JsonObject first = results.get(0).getAsJsonObject();
                if (first.has("ParsedText")) {
                    return first.get("ParsedText").getAsString();
                }
            }
            throw new Exception("No text found in OCR response.");
        }
    }
}

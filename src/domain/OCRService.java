package domain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import okhttp3.*;
import util.Config;
import java.io.File;
import java.io.IOException;

public class OCRService {
    private static final String OCR_URL = "https://api.ocr.space/parse/image";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public String extractTextFromImage(File imageFile) throws Exception {
        String apiKey = Config.getOcrApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
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
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OCR API HTTP error: " + response.code());
            }
            String json = response.body().string();
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            if (obj.has("IsErroredOnProcessing") && obj.get("IsErroredOnProcessing").getAsBoolean()) {
                String msg = obj.has("ErrorMessage") ? obj.get("ErrorMessage").toString() : "Unknown error";
                throw new Exception("OCR API error: " + msg);
            }
            JsonArray results = obj.getAsJsonArray("ParsedResults");
            if (results != null && results.size() > 0) {
                JsonObject first = results.get(0).getAsJsonObject();
                if (first.has("ParsedText")) {
                    return first.get("ParsedText").getAsString();
                }
            }
            throw new Exception("No text found in OCR response.");
        }
    }
} 
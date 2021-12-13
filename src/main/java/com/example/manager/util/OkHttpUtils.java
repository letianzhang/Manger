package com.example.manager.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OkHttpUtils {
    private static final OkHttpClient client = new OkHttpClient();
    public static String get(String url, String cookie) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}

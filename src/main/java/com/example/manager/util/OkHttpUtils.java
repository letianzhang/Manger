package com.example.manager.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

public class OkHttpUtils {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    public static String get(String url, String cookie, String token) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .addHeader("X-XSRF-TOKEN", token)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            return failedString(e);
        }
    }

    public static String post(String url, String cookie, String token, String json) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .addHeader("X-XSRF-TOKEN", token)
                .post(RequestBody.create(json, JSON))
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            return failedString(e);
        }
    }

    private static String failedString(Exception e) {
        JSONObject object = new JSONObject();
        object.put("code", -1);
        object.put("message", e.getMessage());
        return object.toJSONString();
    }
}

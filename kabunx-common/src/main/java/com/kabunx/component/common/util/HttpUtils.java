package com.kabunx.component.common.util;

import com.kabunx.component.common.exception.SysException;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 基于OkHttp封装的静态工具类
 */
public class HttpUtils {
    private static final OkHttpClient httpClient = new OkHttpClient();

    public static void doGet(String url, Consumer<String> consumer) throws SysException {
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        consumer.accept(doRequest(request));
    }

    private static final MediaType jsonType = MediaType.get("application/json");

    public static void doPost(String url, String jsonBody, Consumer<String> consumer) throws SysException {
        final RequestBody body = RequestBody.create(jsonType, jsonBody);
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        consumer.accept(doRequest(request));
    }

    public static String doRequest(Request request) throws SysException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new SysException("");
            }
            ResponseBody body = response.body();
            if (Objects.isNull(body)) {
                throw new SysException("");
            }
            return body.string();
        } catch (IOException e) {
            throw new SysException("");
        }
    }

}

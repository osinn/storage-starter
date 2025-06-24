package com.github.osinn.storage.utils;

import okhttp3.*;

/**
 * OkHttp3 工具类封装
 */
public class OkHttpUtil {

    private static OkHttpClient client;

    // 私有构造方法
    private OkHttpUtil() {
        client = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // 单例模式
    public static OkHttpClient getOkHttpClient() {
        return client;
    }


}

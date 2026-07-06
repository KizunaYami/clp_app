package com.example.labremotoclp;

import okhttp3.Credentials;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    //private static final String BASE_URL = "http://192.168.1.41:8080/";

    private static final String BASE_URL = "http://170.233.228.157:8011/";
    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            String credentials = Credentials.basic("GRUPO1", "12A4");
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(chain -> {
                            Request original = chain.request();
                            Request request = original.newBuilder()
                                    .header("Authorization", credentials)
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        }).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}

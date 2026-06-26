package com.sharktech.clp_app.networkinterface;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://172.20.32.1:8080/";
    public static NetworkRetrofit networkRetrofit;

    public static NetworkRetrofit getNetworkRetrofit() {

        // Verifica se o Retrofit já foi inicializado
        if (networkRetrofit == null) {
            String credentials = Credentials.basic("admin", "admin");

            // Cria o OkHttpClient com o interceptor de autenticação
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        // Adiciona o cabeçalho Authorization na requisição
                        Request request = original.newBuilder()
                                .header("Authorization", credentials)
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }).build();

            // Cria o Retrofit com o OkHttpClient e o GsonConverterFactory
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            networkRetrofit = retrofit.create(NetworkRetrofit.class);
        }
        return networkRetrofit;
    }
}

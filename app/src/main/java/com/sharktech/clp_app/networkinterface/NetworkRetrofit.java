package com.sharktech.clp_app.networkinterface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NetworkRetrofit {
    @GET("api")
    Call<String> getPins();

    @POST("api")
    Call<String> setPins();
}

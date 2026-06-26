package com.sharktech.clp_app.networkinterface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NetworkRetrofit {
    @GET("/")
    Call<String> getPins();

    @POST("/")
    Call<String> setPins();
}

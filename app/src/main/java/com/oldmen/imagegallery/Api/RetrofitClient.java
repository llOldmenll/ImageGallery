package com.oldmen.imagegallery.Api;

import android.content.Context;

import com.oldmen.imagegallery.Utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MVP on 04.12.2017.
 */

public class RetrofitClient {

    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiService getApiService(Context context) {
        return getRetrofitInstance().create(ApiService.class);
    }


}

package com.sibi.util.retrofit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sibi.model.YouTubeDto;
import com.sibi.util.Constants;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adway on 08/12/17.
 */

public class YoutubeAPIProvider {
    private static final String API_KEY = "AIzaSyDPZAzlsqAUE_NAzXU6lV8xBETM2Fp49wc";
    private final YoutubeAPI service;

    public YoutubeAPIProvider() {

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.YT_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(createHTTPClient())
            .build();
        service = retrofit.create(YoutubeAPI.class);
    }

    @NonNull private static OkHttpClient createHTTPClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .build();
    }

    public Observable<YouTubeDto> getBudgetVideos() {
        return service.getSearchResults(API_KEY, "25", "snippet",
            "how to budget", "");
    }
}

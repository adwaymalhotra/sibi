package com.sibi.util.retrofit;

import com.sibi.model.YouTubeDto;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by adway on 08/12/17.
 */

public interface YoutubeAPI {
    @GET("search/") Observable<YouTubeDto> getSearchResults(
        @Query("key") String key,
        @Query("maxResults") String maxResults,
        @Query("part") String part,
        @Query("q") String q,
        @Query("type") String type
    );
}

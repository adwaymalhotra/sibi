package com.sibi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sibi.model.YouTubeDto;
import com.sibi.model.YouTubeItem;
import com.sibi.util.YoutubeAdapter;
import com.sibi.util.dagger.AppComponentProvider;
import com.sibi.util.retrofit.YoutubeAPIProvider;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by adway on 08/12/17.
 */

public class YoutubeActivity extends AppCompatActivity {
    public static final String TAG = "YoutubeActivity";

    @BindView(R.id.recycler) RecyclerView recyclerView;
    @Inject YoutubeAPIProvider api;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_frag);
        AppComponentProvider.getAppComponent().inject(this);
        ButterKnife.bind(this);

        updateData();
    }

    public void updateData() {
        api.getBudgetVideos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(youTubeDto -> {
                List<YouTubeItem> youTubeItems = youTubeDto.items;
                Log.d(TAG, "updateData: " + youTubeItems.size());
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(new YoutubeAdapter(youTubeItems));
            }, Throwable::printStackTrace);
    }
}

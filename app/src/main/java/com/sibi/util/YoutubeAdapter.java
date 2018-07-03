package com.sibi.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sibi.R;
import com.sibi.model.YouTubeItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adway on 08/12/17.
 */

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.YoutubeViewHolder> {

    private List<YouTubeItem> items = new ArrayList<>();
    private Context context;

    public YoutubeAdapter(List<YouTubeItem> youTubeItems) {
        this.items.addAll(youTubeItems);
    }

    @Override
    public YoutubeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_yt, parent, false);

        return new YoutubeViewHolder(view);
    }

    @Override public void onBindViewHolder(YoutubeViewHolder holder, int position) {
        YouTubeItem item = items.get(position);
        holder.title.setText(item.snippet.title);
        Picasso.with(context).load(item.snippet.thumbnails.high.url).into(holder.imageView);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    class YoutubeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.image) ImageView imageView;

        public YoutubeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> {
                Intent i = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://www.youtube.com/watch?v="
                    + items.get(getAdapterPosition()).id.videoId));
                itemView.getContext().startActivity(i);
            });
        }
    }
}

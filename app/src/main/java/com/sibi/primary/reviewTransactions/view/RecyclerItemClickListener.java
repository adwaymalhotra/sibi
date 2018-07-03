package com.sibi.primary.reviewTransactions.view;

import android.util.Log;
import android.view.View;

/**
 * Created by adway on 03/12/17.
 */

public interface RecyclerItemClickListener {
    public static final String TAG = "RecyclerItemClick";
    default void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: not handled!");
    }
    default void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: not handled!");
    }
}

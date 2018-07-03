package com.sibi.primary.reviewTransactions.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sibi.util.Constants;

import java.util.List;

/**
 * Created by adway on 04/12/17.
 */

public class CategoryPagerAdapter extends FragmentStatePagerAdapter {

    List<String> categories;

    public CategoryPagerAdapter(FragmentManager fm, List<String> categories) {
        super(fm);
        this.categories = categories;
    }

    @Override public Fragment getItem(int position) {
        Fragment f = new CategoryFragment();
        switch (position) {
            case 0:
                break;
            default:
                Bundle b = new Bundle();
                b.putString(Constants.Keys.KEY_CATEGORY, categories.get(position));
                b.putBoolean(Constants.Keys.KEY_CATEGORY_EMBEDDED, true);
                f.setArguments(b);
                break;
        }
        return f;
    }

    @Override public int getCount() {
        return categories.size();
    }

    @Nullable @Override public CharSequence getPageTitle(int position) {
        if (position == 0) return "All";
        return categories.get(position);
    }
}

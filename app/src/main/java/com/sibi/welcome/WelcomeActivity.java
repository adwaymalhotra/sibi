package com.sibi.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.sibi.R;
import com.sibi.primary.ManageBudgetFragment;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.dagger.AppComponentProvider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adway on 24/11/17.
 */

public class WelcomeActivity extends AppCompatActivity {
    @BindView(R.id.viewpager) ViewPager viewPager;
    @Inject PersistenceInteractor persistenceInteractor;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        AppComponentProvider.getAppComponent().inject(this);
        ButterKnife.bind(this);
        persistenceInteractor.setFirstRun();
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
    }

    @Override public void onBackPressed() {
        finish();
    }

    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(int position) {
            Bundle b = new Bundle();
            b.putInt("index", position);
            Fragment f = new WelcomeFragment();
            switch (position) {
                case 2:
                    return new ManageBudgetFragment();
                default:
                    f.setArguments(b);
                    return f;
            }
        }

        @Override public int getCount() {
            return 3;
        }
    }
}

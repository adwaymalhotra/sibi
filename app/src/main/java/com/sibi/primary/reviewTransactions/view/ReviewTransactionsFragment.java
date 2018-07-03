package com.sibi.primary.reviewTransactions.view;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sibi.R;
import com.sibi.model.Category;
import com.sibi.primary.IMainActivityView;
import com.eftimoff.viewpagertransformers.FlipHorizontalTransformer;

import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewTransactionsFragment extends Fragment {
    public static final String TAG = "ReviewTransFragment";

    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    private IMainActivityView mainActivityView;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityView = ((IMainActivityView) context);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review_transactions, container,
            false);
        ButterKnife.bind(this, v);

        mainActivityView.setTitle("Review Transactions", false);
        return v;
    }

    @Override public void onStart() {
        super.onStart();
        CategoryPagerAdapter adapter = new CategoryPagerAdapter(getChildFragmentManager(),
            mainActivityView.getCategories().stream().map(Category::getName)
                .collect(Collectors.toList()));

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new FlipHorizontalTransformer());
        tabLayout.setupWithViewPager(viewPager);
    }
}
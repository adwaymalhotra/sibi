package com.sibi.welcome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sibi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adway on 08/12/17.
 */

public class WelcomeFragment extends Fragment {
    public static final String TAG = "WelcomeFragment";

    @BindView(R.id.welcome_tv1) TextView tv1;
    @BindView(R.id.welcome_tv2) TextView tv2;
    @BindView(R.id.image) ImageView imageView;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_page1, container, false);
        ButterKnife.bind(this, view);

        int index = getArguments().getInt("index");

        switch (index) {
            case 0:
                tv1.setText("Track your budget with Sibi.");
                try {
                    imageView.setImageResource(R.drawable.dashboard1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                tv1.setText("Record all your spending!");
                try {
                    imageView.setImageResource(R.drawable.add_expense31);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }


        return view;
    }
}

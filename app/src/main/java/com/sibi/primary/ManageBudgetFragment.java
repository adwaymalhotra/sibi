package com.sibi.primary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.sibi.R;
import com.sibi.util.dagger.AppComponentProvider;
import com.sibi.util.firebase.CloudInteractor;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adway on 08/12/17.
 */

public class ManageBudgetFragment extends Fragment {
    public static final String TAG = "ManageBudgetFragment";

    @BindView(R.id.budget_et) EditText budgetET;
    @Inject CloudInteractor cloudInteractor;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_budget, container, false);
        ButterKnife.bind(this, view);
        AppComponentProvider.getAppComponent().inject(this);
        return view;
    }

    @OnClick(R.id.btn) public void onSaveBudget() {
        if (budgetET.getText().toString().isEmpty()) {
            Toast.makeText(getContext(),
                "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        cloudInteractor.updateBudget(Float.parseFloat(budgetET.getText().toString()), objects -> {
            if (objects[0] instanceof Boolean) {
                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
    }
}

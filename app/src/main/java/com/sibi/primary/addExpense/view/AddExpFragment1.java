package com.sibi.primary.addExpense.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sibi.R;
import com.sibi.model.Transaction;
import com.sibi.primary.IMainActivityView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddExpFragment1 extends Fragment {
    public static final String TAG = "AddExpFragment1";

    @BindView(R.id.amount_et) EditText amountET;

    private IMainActivityView mainActivityView;

    @OnClick(R.id.button) public void onButtonClick() {
        if (amountET.getText().toString().isEmpty()) {
            mainActivityView.showToast("Please enter an amount.");
            return;
        }
        mainActivityView.updateAddExpense(amountET, Double.parseDouble(amountET.getText().toString()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityView = ((IMainActivityView) context);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_exp_fragment1, container, false);
        ButterKnife.bind(this, v);

        Transaction t = mainActivityView.getTransaction();
        if (t.getAmount() != 0.0D)
            amountET.setText(String.format(getString(R.string.amount_text),
                new DecimalFormat("#.##").format(t.getAmount())));

        mainActivityView.setTitle("Add Expense", false);
        return v;
    }
}

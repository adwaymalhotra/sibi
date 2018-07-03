package com.sibi.primary.addExpense.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sibi.R;
import com.sibi.model.Transaction;
import com.sibi.primary.IMainActivityView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddExpFragment2 extends Fragment {
    public static final String TAG = "AddExpFragment2";

    @BindView(R.id.name_et) EditText nameET;
    @BindView(R.id.amountPaid_textView) TextView amountPaidTV;

    private IMainActivityView mainActivityView;

    @OnClick(R.id.button) public void onButtonClick() {
        if (nameET.getText().toString().isEmpty()) {
            mainActivityView.showToast("Please enter a name.");
            return;
        }
        mainActivityView.updateAddExpenseName(nameET.getText().toString(), nameET);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_exp_fragment2, container, false);
        ButterKnife.bind(this, v);
        amountPaidTV.setText(mainActivityView.getAmountPaidText());
        Transaction t = mainActivityView.getTransaction();
        nameET.setText(t.getName());
        return v;
    }
}
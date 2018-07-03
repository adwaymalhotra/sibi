package com.sibi.primary.addExpense.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.sibi.R;
import com.sibi.model.Category;
import com.sibi.primary.IMainActivityView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddExpFragment3 extends Fragment {
    public static final String TAG = "AddExpFragment3";

    @BindView(R.id.category_et) EditText categoryET;
    @BindView(R.id.date_et) EditText dateET;
    @BindView(R.id.amountPaid_textView) TextView amountPaidTV;
    @BindView(R.id.receiver_textView) TextView receiverTV;

    private IMainActivityView mainActivityView;
    private PopupMenu menu;
    private String today = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY", Locale.US);
    private long selectedTimestamp;
    private Bitmap imageBitmap;

    private void setupCategoryPopupMenu() {
        menu = new PopupMenu(getContext(), categoryET);
        List<Category> categories = mainActivityView.getCategories();
        for (Category c : categories)
            menu.getMenu().add(c.getName());
        menu.setOnMenuItemClickListener(menuItem -> {
            categoryET.setText(menuItem.getTitle());
            return true;
        });
        categoryET.setText(categories.get(0).getName());
    }

    @OnClick(R.id.button) public void onButtonClick() {
        Log.d(TAG, "onButtonClick: " + dateET.getText().toString());
        mainActivityView.finalizeExpense(categoryET.getText().toString(), selectedTimestamp,
            result -> {
                if (((boolean) result[0])) {
                    Log.d(TAG, "onButtonClick: added to key " + result[1]);
                    mainActivityView.gotoDashboard();
                } else {
                    mainActivityView.showToast("Error adding transaction, please try again.");
                }
            });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityView = (IMainActivityView) context;
        mainActivityView.setCurrentFragment(TAG);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // TODO: location integration
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_exp_fragment3, container, false);
        ButterKnife.bind(this, v);
        setupCategoryPopupMenu();
        categoryET.setOnClickListener(view -> menu.show());
        today = sdf.format(new Date(System.currentTimeMillis()));
        selectedTimestamp = System.currentTimeMillis();
        dateET.setText(today);
        amountPaidTV.setText(mainActivityView.getAmountPaidText());
        receiverTV.setText(mainActivityView.getReceiverText());

        dateET.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(view.getContext(), (datePicker, y, m, d) -> {
                Calendar c = Calendar.getInstance();
                c.set(y, m, d);
                Log.d(TAG, "onCreateView: " + c.getTimeInMillis());
                selectedTimestamp = c.getTimeInMillis();
                dateET.setText(sdf.format(c.getTimeInMillis()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
        });
        return v;
    }

}

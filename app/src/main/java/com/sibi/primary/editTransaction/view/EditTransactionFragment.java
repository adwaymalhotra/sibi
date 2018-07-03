package com.sibi.primary.editTransaction.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.sibi.R;
import com.sibi.model.Category;
import com.sibi.primary.IMainActivityView;
import com.sibi.primary.editTransaction._interface.IEditTransactionView;
import com.sibi.primary.editTransaction.presenter.EditTransactionPresenter;
import com.sibi.util.Constants;
import com.sibi.util.dagger.AppComponentProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adway on 05/12/17.
 */

public class EditTransactionFragment extends Fragment implements IEditTransactionView {
    public static final String TAG = "EditTransactionFragment";

    @BindView(R.id.name_et) EditText nameET;
    @BindView(R.id.category_et) EditText categoryET;
    @BindView(R.id.amount_et) EditText amountET;
    @BindView(R.id.date_et) EditText dateET;
    @Inject EditTransactionPresenter presenter;
    private long selectedTimestamp;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private PopupMenu menu;
    private IMainActivityView parentView;

    @OnClick(R.id.category_et) public void onCategoryClick() {menu.show();}

    @OnClick(R.id.save_transaction) public void onSaveClick() {presenter.saveTransaction(); }

    @OnClick(R.id.delete_transaction) public void onDeleteClick() {presenter.deleteTransaction();}

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        parentView = ((IMainActivityView) context);
        setRetainInstance(true);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_transaction, container, false);
        ButterKnife.bind(this, view);

        AppComponentProvider.getAppComponent().inject(this);
        presenter.setView(this);

        setupCategoryPopupMenu();
        dateET.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(v.getContext(), (datePicker, y, m, d) -> {
                Calendar c = Calendar.getInstance();
                c.set(y, m, d);
                Log.d(TAG, "onCreateView: " + c.getTimeInMillis());
                selectedTimestamp = c.getTimeInMillis();
                dateET.setText(sdf.format(c.getTimeInMillis()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
        });
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            String transactionKey = args.getString(Constants.Keys.KEY_EDIT_TRANSACTION);
            presenter.initTransactionObject(transactionKey);
        }
        parentView.setTitle("Edit Transaction", false);
    }

    private void setupCategoryPopupMenu() {
        menu = new PopupMenu(getContext(), categoryET);
        List<Category> categories = presenter.getCategories();

        for (Category c : categories) menu.getMenu().add(c.getName());
        menu.setOnMenuItemClickListener(menuItem -> {
            categoryET.setText(menuItem.getTitle());
            return true;
        });
    }

    @Override public void updateData() {
        nameET.setText(presenter.getName());
        categoryET.setText(presenter.getCategory());
        amountET.setText(presenter.getAmountText());
        selectedTimestamp = presenter.getTime();
        dateET.setText(presenter.getDate());
    }

    @Override public String getName() {
        return nameET.getText().toString();
    }

    @Override public double getAmount() {
        return Double.parseDouble(amountET.getText().toString());
    }

    @Override public String getCategory() {
        return categoryET.getText().toString();
    }

    @Override public long getTimestamp() {
        return selectedTimestamp;
    }

    @Override public void onUpdateSuccess() {
        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
        parentView.finishFragment();
    }

    @Override public void onUpdateFailure() {
        Toast.makeText(getContext(), "Update failed! Please try again!", Toast.LENGTH_SHORT).show();
    }

    @Override public void onDelete() {
        parentView.finishFragment();
    }
}

package com.sibi.primary.category.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;
import com.sibi.R;
import com.sibi.model.Category;
import com.sibi.primary.IMainActivityView;
import com.sibi.primary.category._interface.IEditCategoryView;
import com.sibi.primary.category.presenter.EditCategoryPresenter;
import com.sibi.util.Constants;
import com.sibi.util.dagger.AppComponentProvider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adway on 08/12/17.
 */

public class EditCategoryFragment extends Fragment implements IEditCategoryView {
    public static final String TAG = "EditCategoryFragment";

    @BindView(R.id.edit_cat_name_et) EditText categoryNameET;
    @BindView(R.id.view_color) View colorV;
    @Inject EditCategoryPresenter presenter;

    private IMainActivityView mainActivityView;
    private int selectedColorCode;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivityView = ((IMainActivityView) context);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_category, container, false);
        ButterKnife.bind(this, view);
        AppComponentProvider.getAppComponent().inject(this);
        presenter.setView(this);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            String title = bundle.getString(Constants.Keys.KEY_CAT_EDIT, "Create Category");
            mainActivityView.setTitle(title, false);

            if (bundle.containsKey(Constants.Keys.KEY_CATEGORY)) {
                Category category = new Gson()
                    .fromJson(bundle.getString(Constants.Keys.KEY_CATEGORY, ""),
                        Category.class);
                if (category != null) {
                    categoryNameET.setText(category.getName());
                    selectedColorCode = category.getColorCode();
                    colorV.setBackgroundColor(category.getColorCode());
                }
            }
        }
    }

    @OnClick(R.id.save_category_btn) public void onSaveClicked() {
        presenter.saveTransaction();
    }


    @OnClick(R.id.view_color) public void onColorPicker() {
        HSLColorPicker colorPicker = new HSLColorPicker(getContext());
        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColorCode = color;
            }
        });
        new AlertDialog.Builder(getContext())
            .setView(colorPicker)
            .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
            .setOnDismissListener(dialogInterface -> colorV.setBackgroundColor(selectedColorCode))
            .show();
    }

    @Override public int getColorCode() {
        return selectedColorCode;
    }

    @Override public String getName() {
        return categoryNameET.getText().toString();
    }

    @Override public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override public void finish() {
        mainActivityView.finishFragment();
        mainActivityView.updateNavDrawer();
    }
}

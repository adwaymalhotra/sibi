package com.sibi.primary.dashboard.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.sibi.R;
import com.sibi.YoutubeActivity;
import com.sibi.primary.IMainActivityView;
import com.sibi.primary.dashboard._interface.IDashboardView;
import com.sibi.primary.dashboard.presenter.DashboardPresenter;
import com.sibi.util.Constants;
import com.sibi.util.Utils;
import com.sibi.util.dagger.AppComponentProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment implements IDashboardView {
    public static final String TAG = "DashboardFragment";

    @Inject DashboardPresenter presenter;
    @BindView(R.id.available_amount_tv) TextView availableAmountTV;
    @BindView(R.id.total_spending_tv) TextView totalSpendingTV;
    @BindView(R.id.monthly_budget_tv) TextView monthlyBudgetTV;
    @BindView(R.id.pieChart) PieChart pieChart;

    @BindView(R.id.expense_button) View expenseButton;
    @BindView(R.id.review_transactions_tv) View reviewTransactions;

    private IMainActivityView mainActivityView;

    @OnClick(R.id.review_transactions_tv) public void onReviewTransactionsClick() {
        mainActivityView.gotoReviewTransactions(reviewTransactions, getString(R.string.or_review_transactions));
    }

    @OnClick(R.id.expense_button) public void onAddExpenseClick() {
        mainActivityView.gotoAddExpense1(expenseButton, getString(R.string.add_an_expense));
    }

//    @OnClick(R.id.purchase_button) public void onPlanPurchaseClick() {
//        mainActivityView.gotoWishlist();
//    }

//    @OnClick(R.id.wishlist_status_tv) public void onWishlistStatusClick() {
//        mainActivityView.gotoWishlist();
//    }

    @Override public void showFilePicker() {
        Intent intent = new Intent(getContext(), FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.ARG_FILTER, Pattern.compile(".*\\.json$"));
        intent.putExtra(FilePickerActivity.ARG_TITLE, "Select File");
        intent.putExtra(FilePickerActivity.ARG_CLOSEABLE, true);
        this.startActivityForResult(intent, Utils.FILE_PICKER_CODE);
//        new MaterialFilePicker()
//            .withTitle("Select File")
//            .withFilter(Pattern.compile(".*\\.json$"))
//            .withRequestCode(Utils.FILE_PICKER_CODE)
//            .start();
    }

    @Override public void setBudgetStatus(double monthlyBudget, double spending, double remaining) {
        Log.d(TAG, "setBudgetStatus: " + monthlyBudget);
        String text = "$%s";
        monthlyBudgetTV.setText(String.format(text, new DecimalFormat("#.##")
            .format(monthlyBudget)));
        totalSpendingTV.setText(String.format(text, new DecimalFormat("#.##")
            .format(spending)));
        availableAmountTV.setText(String.format(text, new DecimalFormat("#.##")
            .format(remaining)));
    }

    @Override public void updateChart(Map<String, Double> catTransMap) {
        pieChart.setCenterText("Expenditure in\nCurrent Month");
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(60.0f);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.setDrawEntryLabels(false);
        pieChart.setEntryLabelTextSize(12f);
        Description d = new Description();
        d.setText("");
        pieChart.setDescription(d);

        List<PieEntry> entries = new ArrayList<>();
        PieDataSet set = new PieDataSet(entries, "");
        int[] colors = new int[catTransMap.keySet().size()];
        for (int i = 0; i < catTransMap.keySet().size(); i++) {
            String cat = (String) catTransMap.keySet().toArray()[i];
            entries.add(new PieEntry(catTransMap.get(cat).floatValue(), cat));
            colors[i] = presenter.getColor(cat);
//            set.addColor(presenter.getColor(cat));
        }
        set.setColors(colors);

        PieData data = new PieData(set);
        data.setValueTextSize(15f);
        data.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> "$" + value);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    @Override public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.FILE_PICKER_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Log.d(TAG, "onActivityResult: " + filePath);
            try {
                presenter.parseJson(new URI(filePath));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong while parsing file!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utils.REQUEST_CAMERA:
                if (grantResults[0] == 1)
                    Utils.openCameraForImage(this);
                break;
//            case Utils.REQUEST_GALLERY_IMAGE:
//                if (grantResults[0] == 1)
//                    Utils.openGalleryForImage(this);
//                break;
            case Constants.PermissionsConstants.STORAGE_PERMISSION_CODE:
                Toast.makeText(getContext(), "Please select your action again.",
                    Toast.LENGTH_SHORT).show();
                break;
            case Utils.FILE_PICKER_CODE:
                if (grantResults[0] == 1)
                    showFilePicker();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityView = (IMainActivityView) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, v);
        AppComponentProvider.getAppComponent().inject(this);
        presenter.setView(this);
        mainActivityView.setTitle("Dashboard", true);
        setHasOptionsMenu(true);
        return v;
    }

    @Override public void onStart() {
        super.onStart();
        presenter.updateData();
        mainActivityView.clearBackStack();
    }

    @Override public void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return true;
            case R.id.menu_backup_to_json:
                if (Utils.hasStoragePermission(this) == 1) presenter.backupToJson();
                return true;
            case R.id.menu_restore_transactions:
                if (Utils.hasStoragePermission(this) == 1) showFilePicker();
                return true;
            case R.id.menu_youtube:
                Intent intent = new Intent(getContext(), YoutubeActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

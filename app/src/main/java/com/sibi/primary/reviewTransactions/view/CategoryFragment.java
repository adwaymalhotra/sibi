package com.sibi.primary.reviewTransactions.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.sibi.R;
import com.sibi.model.Transaction;
import com.sibi.primary.IMainActivityView;
import com.sibi.primary.reviewTransactions._interface.ICategoryView;
import com.sibi.primary.reviewTransactions.presenter.CategoryPresenter;
import com.sibi.util.Constants;
import com.sibi.util.dagger.AppComponentProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adway on 03/12/17.
 */

public class CategoryFragment extends Fragment implements ICategoryView, RecyclerItemClickListener {
    public static final String TAG = "CategoryFragment";

    @BindView(R.id.transactions_rv) RecyclerView recycler;
    @BindView(R.id.chart) BarChart barChart;
    @Inject CategoryPresenter presenter;

    private TransactionsAdapter adapter;
    private IMainActivityView parentView;
    private String category = "All Transactions";
    private Menu menu;


    @Override public void onItemClick(View view, int position) {
        parentView.gotoEditTransaction(view, adapter.getTransactionKey(position));
    }

    @Override public void updateList() {
        adapter.notifyDataSetChanged();
        updateGraph();
        Log.d(TAG, "updateList: ");
    }

    @Override public void updateGraph() {
        if (getContext() == null) return;
        List<BarEntry> entries = new ArrayList<>();
        List<Transaction> transactions = presenter.getTransactionList();
        if (transactions.size() > 20) transactions = transactions.subList(0, 19);
        transactions.sort((t0, t1) -> t0.getTimestamp() > t1.getTimestamp() ? 1 : -1);
        for (int i = 0; i < presenter.getTransactionList().size(); i++) {
            entries.add(new BarEntry(i, (float) presenter.getTransactionList().get(i).getAmount()));
        }
        BarDataSet set = new BarDataSet(entries, "Transactions");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        if (presenter.getCategory() != null)
            set.setColor(presenter.getCategory().getColorCode());
        else
            set.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        BarData data = new BarData(set);
        data.setBarWidth(0.5f);
        data.setValueTextSize(14f);
        Description d = new Description();
        d.setText("");
        barChart.setData(data);
        barChart.setDescription(d);
        barChart.invalidate();
    }

    private void initRecyclerView() {
        // TODO: add animations
        adapter = new TransactionsAdapter(presenter.getTransactionList(), this,
            parentView.getCategories());
        presenter.udpateData();
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        parentView = ((IMainActivityView) context);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, v);
        AppComponentProvider.getAppComponent().inject(this);
        return v;
    }

    @Override public void onStart() {
        super.onStart();
        boolean embedded = false;
        if (getArguments() != null) {
            category = getArguments().getString(Constants.Keys.KEY_CATEGORY,
                "");
            Log.d(TAG, "onCreateView: " + category);
            presenter.setCategory(category);
            embedded = getArguments().getBoolean(Constants.Keys.KEY_CATEGORY_EMBEDDED,
                false);
        }
        if (!embedded) parentView.setTitle(category, true);

        presenter.setView(this);
        initRecyclerView();
        presenter.udpateData();
    }

    @Override public void onDetach() {
        super.onDetach();
        presenter.unsubscribe();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_category, menu);
        this.menu = menu;
        //setting up search
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        searchView.setQuery("", true);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_decreasing_amt:
                adapter.sortByAmount(false);
                break;
            case R.id.menu_sort_increasing_amt:
                adapter.sortByAmount(true);
                break;
            case R.id.menu_sort_most_recent:
                adapter.sortByDate(false);
                break;
            case R.id.menu_sort_oldest:
                adapter.sortByDate(true);
                break;
            case R.id.menu_sort_name:
                adapter.sortByName();
                break;
            case R.id.menu_edit_category:
                parentView.gotoEditCategory(presenter.getCategory());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

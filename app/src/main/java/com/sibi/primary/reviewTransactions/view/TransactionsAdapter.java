package com.sibi.primary.reviewTransactions.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sibi.R;
import com.sibi.model.Category;
import com.sibi.model.Transaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adway on 03/12/17.
 */

public class TransactionsAdapter
    extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>
    implements Filterable {

    public static final String TAG = "TransactionsAdapter";
    private final List<Category> categories;
    private final List<Transaction> dataset;
    private RecyclerItemClickListener listener;
    private List<Transaction> transactions = new ArrayList<>();

    TransactionsAdapter(List<Transaction> transactions, RecyclerItemClickListener listener,
                        List<Category> categories) {
        this.dataset = transactions;
        this.listener = listener;
        this.categories = categories;

        transactions.addAll(dataset);
        transactions.sort((t0, t1) -> t0.getTimestamp() > t1.getTimestamp() ? -1 : 1);
    }

    void sortByDate(boolean isAscending) {
        transactions.sort((c0, c1) -> {
            if (c0.getTimestamp() > c1.getTimestamp())
                if (isAscending) return 1;
                else return -1;
            else {
                if (isAscending) return -1;
                else return 1;
            }
        });
        notifyDataSetChanged();
    }

    void sortByAmount(boolean isAscending) {
        transactions.sort((c0, c1) -> {
            if (c0.getAmount() > c1.getAmount())
                if (isAscending) return 1;
                else return -1;
            else {
                if (isAscending) return -1;
                else return 1;
            }
        });
        notifyDataSetChanged();
    }

    void sortByName() {
        transactions.sort(Comparator.comparing(Transaction::getName));
        notifyDataSetChanged();
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        Transaction transaction = transactions.get(position);

        holder.nameTV.setText(transaction.getName());
        String amt = "$" + new DecimalFormat("#.##").format(transaction.getAmount());
        holder.amountTV.setText(amt);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY", Locale.US);
        holder.dateTV.setText(sdf.format(new Date(transaction.getTimestamp())));
        categories.stream().filter(c -> c.getName().equals(transaction.getCategory())).findFirst()
            .ifPresent(category -> holder.category.setBackgroundColor(category.getColorCode()));
    }

    @Override public int getItemCount() {
        return transactions.size();
    }

    String getTransactionKey(int position) {
        return transactions.get(position).getKey();
    }

    @Override public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence charSequence) {
                if (charSequence.length() == 0) {
                    FilterResults results = new FilterResults();
                    results.count = dataset.size();
                    results.values = dataset;
                    return results;
                }
                List<Transaction> list = dataset.stream()
                    .filter(transaction -> transaction.getName()
                        .toLowerCase().contains(charSequence.toString().toLowerCase()))
                    .collect(Collectors.toList());
                FilterResults results = new FilterResults();
                results.count = list.size();
                results.values = list;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                transactions = (List<Transaction>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class TransactionViewHolder extends ViewHolder {
        @BindView(R.id.transaction_name_tv) TextView nameTV;
        @BindView(R.id.amount_tv) TextView amountTV;
        @BindView(R.id.date_tv) TextView dateTV;
        @BindView(R.id.category_color) View category;

        TransactionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                listener.onItemClick(view, getAdapterPosition());
            });
        }
    }
}

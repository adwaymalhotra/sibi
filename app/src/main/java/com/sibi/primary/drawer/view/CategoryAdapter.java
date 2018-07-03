package com.sibi.primary.drawer.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sibi.R;
import com.sibi.model.Category;
import com.sibi.primary.reviewTransactions.view.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adway on 04/12/17.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final RecyclerItemClickListener listener;
    List<Category> categories;
    private List<Double> amounts;
    private Context context;
    private List<Integer> selectedPositions = new ArrayList<>();

    CategoryAdapter(List<Category> categories, List<Double> amounts,
                    RecyclerItemClickListener listener) {
        this.categories = categories;
//
//        amounts.stream().reduce((aDouble, aDouble2) -> aDouble + aDouble2)
//            .ifPresent(d -> amounts.add(0, d));
//
//        this.amounts = amounts;
        addAll();
        this.listener = listener;
    }

    private void addAll() {
        Category category = new Category();
        category.setName("All");
        categories.add(0, category);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        categories.get(0).setColorCode(ContextCompat.getColor(context, R.color.colorAccent));
        View view = LayoutInflater.from(context)
            .inflate(R.layout.row_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category c = categories.get(position);
        holder.name.setText(c.getName());
        holder.color.setBackgroundColor(c.getColorCode());
        if (selectedPositions.contains(position)) holder.root.setSelected(true);
        else holder.root.setSelected(false);
//        holder.color.setText(String.format(context.getString(R.string.spent_text),
//            new DecimalFormat("#.##").format(amounts.get(position))));
    }

    @Override public int getItemCount() {
        return categories.size();
    }

    public Category getCategory(int position) {
        if (position == 0) return null;
        else return categories.get(position);
    }

    void setSelected(int position) {
        if (-1 == position) {
            selectedPositions.clear();
            notifyDataSetChanged();
            return;
        }

        if (selectedPositions.contains(position))
            selectedPositions.removeIf(integer -> integer == position);
        else
            selectedPositions.add(position);

        notifyItemChanged(position);
    }

    List<Category> getSelectedCategories() {
        List<Category> list = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            if (selectedPositions.contains(i))
                list.add(categories.get(i));
        }
        return list;
    }

    void updateCategories(List<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        addAll();
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_tv) TextView name;
        @BindView(R.id.spent_tv) View color;
        View root;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            root = itemView;

            itemView.setOnLongClickListener(view -> {
                listener.onItemLongClick(view, getAdapterPosition());
                return true;
            });
            itemView.setOnClickListener(view -> {
                listener.onItemClick(view, getAdapterPosition());
            });
        }
    }
}
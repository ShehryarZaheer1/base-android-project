package com.xevensolutions.baseapp.bottomOptions.UI.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xevensolutions.baseapp.R;

import com.xevensolutions.baseapp.bottomOptions.interfaces.EmployeeSelectorListener;
import com.xevensolutions.baseapp.bottomOptions.models.ItemSelection;
import com.xevensolutions.baseapp.utils.GenericUtils;
import com.xevensolutions.baseapp.utils.ImageUtils;

import java.util.ArrayList;

public class AdapterListItemEmployeeSelector extends RecyclerView.Adapter<EmployeeSelectorViewHolder> {

    EmployeeSelectorListener employeeSelectorListener;
    Context context;
    ArrayList<? extends ItemSelection> items;
    ArrayList<? extends ItemSelection> filterdItems;
    boolean isMultiSelection = false;

    public AdapterListItemEmployeeSelector(Context context, ArrayList<? extends ItemSelection> items,
                                           EmployeeSelectorListener employeeSelectorListener,
                                           boolean isMultiSelection) {
        this.context = context;
        this.employeeSelectorListener = employeeSelectorListener;
        this.items = items;
        this.filterdItems = new ArrayList<>(items);
        this.isMultiSelection = isMultiSelection;

    }

    public void updateItems(ArrayList<? extends ItemSelection> items) {
        this.filterdItems = items;
        notifyDataSetChanged();
    }

    public void filterItems(String filterQuery) {
        if (items == null || items.size() == 0)
            return;
        if (GenericUtils.isStringEmpty(filterQuery))
            updateItems(this.items);
        else {

            ArrayList<ItemSelection> filterdItems = new ArrayList<>();
            for (ItemSelection ItemSelection : items) {
                if (ItemSelection.getTitle() != null && ItemSelection.getTitle().toLowerCase().contains(filterQuery.toLowerCase()))
                    filterdItems.add(ItemSelection);
            }
            updateItems(filterdItems);
        }
    }

    @NonNull
    @Override
    public EmployeeSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_employee_selector, parent, false);
        return new EmployeeSelectorViewHolder(view);
    }

    public ArrayList<ItemSelection> getSelectedItems() {
        if (filterdItems == null)
            return new ArrayList<>();

        ArrayList<ItemSelection> ItemSelections = new ArrayList<>();
        for (ItemSelection ItemSelection : filterdItems) {
            if (ItemSelection.isSelected())
                ItemSelections.add(ItemSelection);
        }

        return ItemSelections;
    }

    public ArrayList<Integer> getSelectedItemsIds() {
        ArrayList<Integer> selectedIds = new ArrayList<>();
        ArrayList<ItemSelection> selecteditems = getSelectedItems();
        for (ItemSelection ItemSelection : selecteditems) {
            selectedIds.add((int) ItemSelection.getId());
        }
        return selectedIds;
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeSelectorViewHolder holder, int position) {
        ItemSelection val = filterdItems.get(position);
        if (val.getIconRes() > 0) {
            Glide.with(context).load(val.getIconRes()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivProfile);
        } else
            ImageUtils.setImage(context, val.getTitle(), val.getIcon(), holder.ivProfile,
                    true, true, false, R.drawable.image_placeholder);
        holder.tvName.setText(val.getTitle());
        ViewGroup.LayoutParams layoutParams = holder.ivProfile.getLayoutParams();
        if (GenericUtils.isStringEmpty(val.getIcon()) && val.getIconRes() == 0) {
            layoutParams.width = 0;
        } else {
            layoutParams.width = layoutParams.height;
        }
        holder.ivProfile.setLayoutParams(layoutParams);

        if (isMultiSelection) {
            holder.contCheck.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(val.isSelected());
        } else
            holder.contCheck.setVisibility(View.GONE);


        holder.mainCardView.setOnClickListener(v -> {
            onListItemClicked(holder, val);
        });

        holder.contCheck.setOnClickListener(view -> {
            onListItemClicked(holder, val);
        });

        try {
            holder.mainCardView.setCardBackgroundColor(ContextCompat.getColor(context, val.getItemBackgroundColor()));

            if (val.getItemBackgroundColor() == R.color.white) {
                // show only lines in case of vaccine names case
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.ef_grey));
            }

        } catch (Exception e) {
//            Toast.makeText(context, "Sorry", Toast.LENGTH_SHORT).show();
        }

        if (val.getItemBackgroundColor() == R.color.white) {
            // show only lines in case of vaccine names case
            holder.view.setBackgroundColor(context.getResources().getColor(R.color.ef_grey));
        }

    }

    public void onListItemClicked(EmployeeSelectorViewHolder holder, ItemSelection val) {
        if (isMultiSelection) {
            if (!holder.checkBox.isChecked()) {
                holder.checkBox.setChecked(true);
                val.setSelected(true);
            } else {
                holder.checkBox.setChecked(false);
                val.setSelected(false);
            }

        } else
            employeeSelectorListener.onEmployeeSelected(holder.getAdapterPosition(), val);
    }

    @Override
    public int getItemCount() {
        return filterdItems.size();
    }

}

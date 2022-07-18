package com.xevensolutions.baseapp.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xevensolutions.baseapp.interfaces.OnListItemClickListener;
import com.xevensolutions.baseapp.presenters.BaseFragmentView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public abstract class BaseRecyclerViewFilterAdapter<T> extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<T> items;
    public ArrayList<T> itemsCopy;
    private final Context context;
    WeakReference<BaseFragmentView> fragmentViewWeakReference;
    OnListItemClickListener onListItemClickListener;

    public abstract void onBindData(RecyclerView.ViewHolder holder, T record);

    public void updateItem(int index, T item) {
        items.set(index, item);
        try {
            itemsCopy.set(index, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyItemChanged(index);
    }

    public BaseRecyclerViewFilterAdapter(Context context, ArrayList<T> items, BaseFragmentView baseFragmentView,
                                         OnListItemClickListener onListItemClickListener) {
        fragmentViewWeakReference = new WeakReference<>(baseFragmentView);
        this.context = context;
        this.items = items;
        this.itemsCopy = new ArrayList<>(items);
        this.onListItemClickListener = onListItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getViewHolder(viewType, parent);
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<T> getItems() {
        return items;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (items.size() > 0)
            onBindData(holder, items.get(position));
        else
            onBindData(holder, null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(ArrayList<T> savedCardItemz) {

        updateNoDataVisibility(savedCardItemz);
        items = savedCardItemz;

        itemsCopy.clear();
        itemsCopy.addAll(savedCardItemz);
        this.notifyDataSetChanged();
    }

    //override toString() method of T class and return the proper attribute on which filter is required in order to work this properly
    public void filter(String text) {
        items.clear();

        if (text.isEmpty()) {
            items.addAll(itemsCopy);
        } else {
            text = text.toLowerCase();
            for (T item : itemsCopy) {
                if (item.toString().toLowerCase().contains(text)) {
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateNoDataVisibility(ArrayList<T> items) {
        if (fragmentViewWeakReference.get() != null) {
            if (items == null || items.size() == 0)
                fragmentViewWeakReference.get().showNoData();
            else
                fragmentViewWeakReference.get().hideNoData();
        }
    }

    public void addItem(T item) {
        this.items.add(item);
        updateNoDataVisibility(items);

        this.itemsCopy.add(item);
        this.notifyDataSetChanged();
    }

    public void addItem(int index, T item) {
        this.items.add(index, item);
        this.itemsCopy.add(index, item);
        updateNoDataVisibility(items);
        this.notifyItemInserted(index);
        this.notifyDataSetChanged();
    }

    public T getItem(int position) {
        return items.get(position);
    }

    public void deleteItem(int position) {
        this.items.remove(position);
        this.itemsCopy.remove(position);
        updateNoDataVisibility(items);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, this.items.size());
    }

    public void restoreItem(T item, int position) {
        getItems().add(position, item);
        this.itemsCopy.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public abstract RecyclerView.ViewHolder getViewHolder(int viewType, ViewGroup parent);

    public void reverseItems() {
        Collections.reverse(items);
        notifyDataSetChanged();
    }
}
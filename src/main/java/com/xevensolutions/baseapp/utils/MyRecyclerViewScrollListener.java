package com.xevensolutions.baseapp.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class MyRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    public static int PAGE_SIZE = 5;
    LinearLayoutManager layoutManager;
    GridLayoutManager gridLayoutManager;
    private boolean isLoading;
    private boolean isLastPage;
    boolean isReverse;

    public MyRecyclerViewScrollListener(RecyclerView.LayoutManager layoutManager, boolean isReverse,
                                        int pageSize) {
        if (layoutManager == null)
            return;
        PAGE_SIZE = pageSize;
        this.isReverse = isReverse;
        if (layoutManager instanceof GridLayoutManager)
            gridLayoutManager = (GridLayoutManager) layoutManager;
        else if (layoutManager instanceof LinearLayoutManager)
            this.layoutManager = (LinearLayoutManager) layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = gridLayoutManager != null ? gridLayoutManager : this.layoutManager;
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = gridLayoutManager != null ?
                this.gridLayoutManager.findFirstVisibleItemPosition()
                : this.layoutManager.findFirstVisibleItemPosition();


        Log.i("recyclerview scroll", "first item position: " + firstVisibleItemPosition + " visible Item count: " + visibleItemCount
                + " total Item count: " + totalItemCount);

        if (!isLoading && !isLastPage) {
            if (isReverse ? firstVisibleItemPosition < PAGE_SIZE :
                    ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount)
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                loadMoreItems();
            }
        }
    }

    public abstract void loadMoreItems();

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}

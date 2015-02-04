package com.sunnydaycorp.simpletwitterapp.listeners;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class EndlessScrollListener implements OnScrollListener {

	private int visibleThreshold = 10;

	private int previousTotalItemCount = 0;
	private boolean isLoading = true;
	private boolean isEndReached = false;

	public EndlessScrollListener() {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		// If the total item count is zero then the initial page is still
		// loading
		if (totalItemCount == 0) {
			this.isLoading = true;
			this.previousTotalItemCount = totalItemCount;

		}

		// If it’s still loading, we check to see if the dataset count has
		// changed, if so we conclude it has finished loading
		if (isLoading && (totalItemCount > previousTotalItemCount)) {
			isLoading = false;
			previousTotalItemCount = totalItemCount;
		}

		// If it isn’t currently loading, we check to see if we have breached
		// the visibleThreshold and need to reload more data.
		if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && !isEndReached) {
			Log.d("LOAD_MORE", "on load more is called with isLoading:" + isLoading + " isEndReached:" + isEndReached);
			onLoadMore();
			isLoading = true;
		}
	}

	public abstract void onLoadMore();

	public void setLoadAsFailed() {
		isLoading = false;
	}

	public void setIsEndReached(boolean isEndReached) {
		Log.d("LOAD_MORE", "end is reached is set to " + isEndReached);
		this.isEndReached = isEndReached;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
package com.sunnydaycorp.simpletwitterapp.activities;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class EndlessScrollListener implements OnScrollListener {

	private int visibleThreshold = 5;

	private int previousTotalItemCount = 0;
	private boolean isLoading = true;

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
		if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			onLoadMore();
			isLoading = true;
		}
	}

	public abstract void onLoadMore();

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
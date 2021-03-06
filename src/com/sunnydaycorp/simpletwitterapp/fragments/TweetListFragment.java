package com.sunnydaycorp.simpletwitterapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.activities.TweetDetailsActivity;
import com.sunnydaycorp.simpletwitterapp.adapters.TimelineTweetListItemAdapter;
import com.sunnydaycorp.simpletwitterapp.listeners.DatabaseAwareTimelineResponseListener;
import com.sunnydaycorp.simpletwitterapp.listeners.EndlessScrollListener;
import com.sunnydaycorp.simpletwitterapp.listeners.TimelineResponseListener;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.TwitterAPIReqCode;

public abstract class TweetListFragment extends Fragment {

	public static final String TWEET_ID_EXTRA_TAG = "TWEET_ID";

	private SwipeRefreshLayout swipeContainer;
	private ListView lvTimelineTweets;
	private TimelineTweetListItemAdapter adapter;

	private LinearLayout llLoadingIndicator;
	private LinearLayout llNoTweetsMessage;
	private TextView tvNoTweetsMessage;

	protected TwitterRestClient twitterRestClient;
	private TimelineResponseListener timelineResponseListener;
	private boolean isLoadingTimeline = false;

	private EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {

		@Override
		public void onLoadMore() {
			if (!isLoadingTimeline) {
				updateLoadAsInProgress();
				fetchTimelineTweets(TwitterAPIReqCode.MORE_OLD_LOAD, timelineResponseListener);
			} else {
				updateLoadAsInProgress();
			}
		}
	};

	private OnRefreshListener onRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			refreshTimeline();
		}
	};

	public TweetListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<Tweet> tweets = new ArrayList<Tweet>();
		adapter = new TimelineTweetListItemAdapter(getActivity(), tweets);

		twitterRestClient = ((SimpleTwitterApp) getActivity().getApplication()).getRestClient();
		timelineResponseListener = new DatabaseAwareTimelineResponseListener(getActivity()) {

			@Override
			public void onTimelineFetched(TwitterAPIReqCode requestCode, List<Tweet> tweets) {
				saveTweets(tweets);
				switch (requestCode) {
				case NEW_LOAD:
					addTweetsOnNewLoad(tweets);
					break;
				case REFRESH_LOAD:
					addTweetsOnRefreshLoad(tweets);
					onRefreshLoad();
					break;
				case MORE_OLD_LOAD:
					addTweetsOnMoreOldLoad(tweets);
					if (tweets.size() == 0) {
						endlessScrollListener.setIsEndReached(true);
					}
					break;
				}
				updateLoadAsCompleted();
				setNoTweetsMessageVisibility();

			}

			@Override
			public void onErrorResult(TwitterRestClient.ResultCode resultCode) {
				updateLoadAsCompleted();
				updateEndlessScrollListnerOnLoadError();
				super.onErrorResult(resultCode);
			}

		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tweet_list, container, false);

		llLoadingIndicator = (LinearLayout) view.findViewById(R.id.llLoadingIndicator);
		llNoTweetsMessage = (LinearLayout) view.findViewById(R.id.llNoTweetsMessage);
		tvNoTweetsMessage = (TextView) view.findViewById(R.id.tvNoTweetsMessage);

		swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
		swipeContainer.setColorSchemeResources(R.color.swipe_container_activated_color, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		swipeContainer.setOnRefreshListener(onRefreshListener);

		lvTimelineTweets = (ListView) view.findViewById(R.id.lvTimelineTweets);
		lvTimelineTweets.setAdapter(adapter);
		lvTimelineTweets.setOnScrollListener(endlessScrollListener);
		lvTimelineTweets.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openTweetDetails(adapter.getItemId(position));
			}
		});
		return view;
	}

	public void refreshTimeline() {
		if (!isLoadingTimeline) {
			updateLoadAsInProgress();
			fetchTimelineTweets(TwitterAPIReqCode.REFRESH_LOAD, timelineResponseListener);
		} else {
			updateLoadAsInProgress();
		}
	}

	protected void reloadRecentTweets() {
		// load last saved tweets from DB
		List<Tweet> tweets = getCachedRecentTweets();
		if (tweets != null && tweets.size() > 0) {
			addTweetsOnNewLoad(tweets);
		}
		updateLoadAsInProgress();
		fetchTimelineTweets(TwitterAPIReqCode.NEW_LOAD, timelineResponseListener);
	}

	protected void openTweetDetails(long tweetId) {
		Intent i = new Intent(getActivity(), TweetDetailsActivity.class);
		i.putExtra(TWEET_ID_EXTRA_TAG, tweetId);
		startActivity(i);
	}

	public void addTweetsOnNewLoad(List<Tweet> tweets) {
		adapter.clear();
		adapter.addAll(tweets);
	}

	public void addTweetsOnRefreshLoad(List<Tweet> tweets) {
		adapter.insertAll(tweets);
	}

	public void addTweetsOnMoreOldLoad(List<Tweet> tweets) {
		adapter.addAll(tweets);
	}

	private void updateLoadAsCompleted() {
		isLoadingTimeline = false;
		setNoTweetsMessageVisibility();
		if (llLoadingIndicator != null) {
			llLoadingIndicator.setVisibility(View.GONE);
		}
		// disable animation for swipe container refreshing
		if (swipeContainer != null) {
			swipeContainer.setRefreshing(false);
		}
	}

	public void updateEndlessScrollListnerOnLoadError() {
		endlessScrollListener.setLoadAsFailed();
	}

	private void updateLoadAsInProgress() {
		isLoadingTimeline = true;
		setNoTweetsMessageVisibility();
		if (llLoadingIndicator != null) {
			llLoadingIndicator.setVisibility(View.VISIBLE);
		}
		// disable animation for swipe container refreshing
		if (swipeContainer != null) {
			swipeContainer.setRefreshing(false);
		}
	}

	public void scrollToTheTop() {
		lvTimelineTweets.smoothScrollToPositionFromTop(0, 0);
	}

	public long getLastItemTweetId() {
		if (adapter.getCount() > 0) {
			return adapter.getItemId(adapter.getCount() - 1);
		}
		return 1;
	}

	public long getFirstItemTweetId() {
		if (adapter.getCount() > 0) {
			return adapter.getItemId(0);
		}
		return 1;
	}

	public void setNoTweetsMessageVisibility() {
		if (llNoTweetsMessage != null) {
			if (!isLoadingTimeline && adapter.getCount() < 1 && tvNoTweetsMessage != null && getEmptyListMessage() != null
					&& !getEmptyListMessage().isEmpty()) {
				tvNoTweetsMessage.setText(getEmptyListMessage());
				llNoTweetsMessage.setVisibility(View.VISIBLE);
			} else {
				llNoTweetsMessage.setVisibility(View.GONE);
			}
		}
	}

	public abstract String getEmptyListMessage();

	public abstract void fetchTimelineTweets(TwitterAPIReqCode twitterAPIReqCode, TimelineResponseListener timelineResponseListener);

	public abstract List<Tweet> getCachedRecentTweets();

	public void onRefreshLoad() {
	};

}

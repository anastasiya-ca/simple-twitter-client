package com.sunnydaycorp.simpletwitterapp.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.adapters.TimelineTweetListItemAdapter;
import com.sunnydaycorp.simpletwitterapp.fragments.NewTweetFragment;
import com.sunnydaycorp.simpletwitterapp.interfaces.OnNewTweetCreatedListener;
import com.sunnydaycorp.simpletwitterapp.interfaces.TwitterClientListener;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public class TimelineActivity extends FragmentActivity {

	public static final String TWEET_ID_EXTRA_TAG = "TWEET_ID";

	private ListView lvTimelineTweets;
	private SwipeRefreshLayout swipeContainer;

	private TwitterRestClient twitterRestClient;
	private TimelineTweetListItemAdapter adapter;
	private boolean isLoadingTimeline = false;

	public enum TwitterAPIReqCode {
		REFRESH_LOAD, MORE_OLD_LOAD, NEW_LOAD
	};

	private TwitterClientListener twitterClientListener = new TwitterClientListener() {

		@Override
		public void onHomeTimelineFetched(TwitterAPIReqCode requestCode, List<Tweet> moreTweets) {
			switch (requestCode) {
			case NEW_LOAD:
				adapter.clear();
				adapter.addAll(moreTweets);
				break;
			case REFRESH_LOAD:
				adapter.insertAll(moreTweets);
				break;
			case MORE_OLD_LOAD:
				adapter.addAll(moreTweets);
				break;
			}
			updateLoadAsCompleted();
		}

		@Override
		public void onError(TwitterRestClient.ResultCode resultCode) {
			updateLoadAsCompleted();
			switch (resultCode) {
			case FAILED_REQUEST:
				Toast.makeText(TimelineActivity.this, "Error connecting Twitter. Please try again", Toast.LENGTH_SHORT).show();
				break;
			case JSON_PARSING_EXCEPTION:
				Toast.makeText(TimelineActivity.this, "Error in processing response from Twitter", Toast.LENGTH_SHORT).show();
				break;
			case NO_INTERNET:
				Toast.makeText(TimelineActivity.this, "You are in offline mode. Please check your internet connection", Toast.LENGTH_SHORT).show();
				break;
			case EXCEEDED_QPS:
				Toast.makeText(TimelineActivity.this, "Too many requests. Please wait and try again later", Toast.LENGTH_SHORT).show();
				break;
			}

		}

		private void updateLoadAsCompleted() {
			if (swipeContainer != null) {
				swipeContainer.setRefreshing(false);
			}
			setProgressBarIndeterminateVisibility(false);
			isLoadingTimeline = false;
		}

		@Override
		public void onNewTweetPosted(Tweet newTweet) {
			lvTimelineTweets.smoothScrollToPositionFromTop(0, 0);
			Toast.makeText(TimelineActivity.this, "Your tweet has been posted", Toast.LENGTH_SHORT).show();
			adapter.insert(newTweet, 0);
		}

		@Override
		public void onUserCredentialsVerified(TwitterUser user) {
			SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) TimelineActivity.this.getApplicationContext())
					.getSharedLoggedUserDetails();
			loggedUserDetails.savePreferences(user.getUserId(), user.getUserName(), user.getUserScreenName(), user.getUserProfilePicUrl());
		}
	};

	private OnNewTweetCreatedListener onNewTweetCreatedListener = new OnNewTweetCreatedListener() {
		@Override
		public void onNewTweetCreated(DialogInterface dialog, String text) {
			twitterRestClient.postNewTweet(twitterClientListener, text);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_timeline);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		lvTimelineTweets = (ListView) findViewById(R.id.lvTimelineTweets);

		List<Tweet> tweets = new ArrayList<Tweet>();
		adapter = new TimelineTweetListItemAdapter(this, tweets);
		lvTimelineTweets.setAdapter(adapter);
		lvTimelineTweets.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore() {
				if (!isLoadingTimeline) {
					isLoadingTimeline = true;
					long maxId = 1;
					if (adapter.getCount() > 0) {
						maxId = adapter.getItem(adapter.getCount() - 1).getTweetId();
					}
					setProgressBarIndeterminateVisibility(true);
					twitterRestClient.fetchHomeTimelineTweets(TwitterAPIReqCode.MORE_OLD_LOAD, twitterClientListener, 0, maxId - 1);
				}

			}
		});
		lvTimelineTweets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(TimelineActivity.this, TweetDetailsActivity.class);
				i.putExtra(TWEET_ID_EXTRA_TAG, adapter.getItem(position).getTweetId());
				startActivity(i);
			}

		});

		twitterRestClient = ((SimpleTwitterApp) getApplication()).getRestClient();
		twitterRestClient.verifyAndGetUserCredentials(twitterClientListener);

		loadRecentTweets();

		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				swipeContainer.setRefreshing(false);
				refreshTimeline();
			}
		});

		swipeContainer.setColorSchemeResources(R.color.swipe_container_activated_color, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
	}

	private void loadRecentTweets() {
		// load last saved tweets from DB
		List<Tweet> tweets = Tweet.recentItems();
		if (tweets != null && tweets.size() > 0) {
			adapter.clear();
			adapter.addAll(Tweet.recentItems());
		}
		setProgressBarIndeterminateVisibility(true);
		twitterRestClient.fetchHomeTimelineTweets(TwitterAPIReqCode.NEW_LOAD, twitterClientListener, 0, 0);
	}

	private void refreshTimeline() {
		if (!isLoadingTimeline) {
			isLoadingTimeline = true;
			long sinceId = 1;
			if (adapter.getCount() > 0) {
				sinceId = adapter.getItem(0).getTweetId();
			}
			setProgressBarIndeterminateVisibility(true);
			twitterRestClient.fetchHomeTimelineTweets(TwitterAPIReqCode.REFRESH_LOAD, twitterClientListener, sinceId, 0);
		} else {
			setProgressBarIndeterminateVisibility(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_new_tweet) {
			showNewTweetDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showNewTweetDialog() {
		DialogFragment newFragment = new NewTweetFragment(onNewTweetCreatedListener);
		newFragment.show(getSupportFragmentManager(), "onNewTweetDialog");
	}
}

package com.sunnydaycorp.simpletwitterapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.fragments.HomeTimelineFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.MentionsTimelineFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.NewTweetFragment;
import com.sunnydaycorp.simpletwitterapp.interfaces.OnNewTweetPostedTCListener;
import com.sunnydaycorp.simpletwitterapp.interfaces.TwitterClientListener;
import com.sunnydaycorp.simpletwitterapp.listeners.SupportFragmentTabListener;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.ResultCode;

public class TimelineActivity extends ActionBarActivity {

	public static final String USER_ID_EXTRA_TAG = "USER_ID";

	private TwitterRestClient twitterRestClient;

	private TwitterClientListener twitterClientListener = new TwitterClientListener() {

		@Override
		public void onError(ResultCode resultCode) {
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

	};

	private OnNewTweetPostedTCListener onNewTweetPostedTCListener = new OnNewTweetPostedTCListener() {
		@Override
		public void onNewTweetPosted(Tweet newTweet) {
			HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) TimelineActivity.this.getSupportFragmentManager().findFragmentByTag(
					"home");
			if (homeTimelineFragment != null) {
				homeTimelineFragment.scrollToTheTop();
				//TODO just refresh?
				homeTimelineFragment.addTweetsOnRefreshLoad(newTweet);
			}
			Toast.makeText(TimelineActivity.this, "Your tweet has been posted", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(ResultCode resultCode) {
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
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		// ActionBar actionBar = getActionBar();

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		twitterRestClient = ((SimpleTwitterApp) this.getApplication()).getRestClient();
		twitterRestClient.verifyAndGetUserCredentials(twitterClientListener);

		setupTabs();

	}

	private void setupTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar.newTab().setText("Home").setTag("HomeTimelineFragment")
				.setTabListener(new SupportFragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, "home", HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
				.newTab()
				.setText("Mentions")
				.setTag("MentionsTimelineFragment")
				.setTabListener(
						new SupportFragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "mentions", MentionsTimelineFragment.class));

		actionBar.addTab(tab2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_new_tweet) {
			showNewTweetDialog();
			return true;
		}
		if (id == R.id.action_view_profile) {
			Intent i = new Intent(TimelineActivity.this, UserProfileActivity.class);
			i.putExtra(USER_ID_EXTRA_TAG, Long.valueOf(((SimpleTwitterApp) getApplicationContext()).getSharedLoggedUserDetails().getUserId()));
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showNewTweetDialog() {
		DialogFragment newFragment = new NewTweetFragment(onNewTweetPostedTCListener);
		newFragment.show(getSupportFragmentManager(), "onNewTweetDialog");
	}
}

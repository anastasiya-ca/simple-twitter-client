package com.sunnydaycorp.simpletwitterapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.adapters.UserProfileDetailsPagerAdapter;
import com.sunnydaycorp.simpletwitterapp.formatters.TwitterCountsFormatter;
import com.sunnydaycorp.simpletwitterapp.fragments.UserTimelineFragment;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.ResultCode;

public class UserProfileActivity extends FragmentActivity {

	public static final String LOG_TAG_CLASS = UserProfileActivity.class.getSimpleName();
	public static final String USER_ID_TAG = "USER_ID";
	public static final String NEW_TWEET_POSTED_INTENT_FILTER = "new-tweet-posted";
	public static final String USER_COUNTS_UPDATED_INTENT_FILTER = "user-counts-updated";

	private UserProfileDetailsPagerAdapter viewPagerAdapter;
	private ViewPager viewPager;

	private TextView tvTweetCount;
	private TextView tvFollowingCount;
	private TextView tvFollowersCount;

	private long userId;
	private boolean isOwnProfile;

	private UserTimelineFragment userTimelineFragment;

	private BroadcastReceiver newTweetPostedBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (isOwnProfile) {
				ResultCode resultCode = (ResultCode) intent.getSerializableExtra("RESULT_CODE");

				switch (resultCode) {
				case FAILED_REQUEST:
					Toast.makeText(context, "Error connecting Twitter. Please try again", Toast.LENGTH_SHORT).show();
					break;
				case JSON_PARSING_EXCEPTION:
					Toast.makeText(context, "Error in processing response from Twitter", Toast.LENGTH_SHORT).show();
					break;
				case NO_INTERNET:
					Toast.makeText(context, "You are in offline mode. Please check your internet connection", Toast.LENGTH_SHORT).show();
					break;
				case EXCEEDED_QPS:
					Toast.makeText(context, "Too many requests. Please wait and try again later", Toast.LENGTH_SHORT).show();
					break;
				case OK:
					Toast.makeText(context, "Your tweet has been posted", Toast.LENGTH_SHORT).show();
					populateUserCounts();
					if (userTimelineFragment != null) {
						userTimelineFragment.refreshTimeline();
					}
					break;
				}

			}
		}

	};

	private BroadcastReceiver countsUpdatedBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			populateUserCounts();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		userId = getIntent().getLongExtra(TimelineActivity.USER_ID_EXTRA_TAG, 0);

		SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) getApplicationContext()).getSharedLoggedUserDetails();
		if (userId == loggedUserDetails.getUserId()) {
			isOwnProfile = true;
		}
		setupViews();

		FragmentTransaction sft = getSupportFragmentManager().beginTransaction();
		userTimelineFragment = UserTimelineFragment.newInstance(userId, isOwnProfile);
		sft.add(R.id.flUserTimelineContainer, userTimelineFragment);
		sft.commit();

		viewPager = (ViewPager) findViewById(R.id.vpUserProfileContainer);
		viewPagerAdapter = new UserProfileDetailsPagerAdapter(getSupportFragmentManager(), userId);
		viewPager.setAdapter(viewPagerAdapter);
	}

	private void setupViews() {
		tvTweetCount = (TextView) findViewById(R.id.tvTweetCount);
		tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
		tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
		populateUserCounts();
	}

	private void populateUserCounts() {
		TwitterUser user = TwitterUser.byRemoteId(userId);
		if (user == null && isOwnProfile) {
			SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) getApplicationContext()).getSharedLoggedUserDetails();
			// if logged user is not in db then he/she does not have tweets anymore - any tweet count received on verify user details is out-dated
			loggedUserDetails.setTweetsCount(0);
			user = new TwitterUser(loggedUserDetails.getUserId(), loggedUserDetails.getUserName(), loggedUserDetails.getUserScreenName(),
					loggedUserDetails.getUserProfilePicUrl(), loggedUserDetails.getUserProfileBackgroundPicUrl(), loggedUserDetails.getTweetsCount(),
					loggedUserDetails.getFollowersCount(), loggedUserDetails.getFollowingCount(), loggedUserDetails.getUserTagline());
		}
		if (user != null) {
			tvTweetCount.setText(TwitterCountsFormatter.getCountString(user.getTweetsCount()));
			tvFollowingCount.setText(TwitterCountsFormatter.getCountString(user.getFollowingCount()));
			tvFollowersCount.setText(TwitterCountsFormatter.getCountString(user.getFollowersCount()));
		} else {
			Log.e(LOG_TAG_CLASS, "User with id " + userId + " is not found in DB");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		LocalBroadcastManager.getInstance(this).registerReceiver(newTweetPostedBroadcastReceiver, new IntentFilter(NEW_TWEET_POSTED_INTENT_FILTER));
		LocalBroadcastManager.getInstance(this).registerReceiver(countsUpdatedBroadcastReceiver, new IntentFilter(USER_COUNTS_UPDATED_INTENT_FILTER));
		populateUserCounts();
		super.onResume();
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(newTweetPostedBroadcastReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(countsUpdatedBroadcastReceiver);
		super.onPause();
	}

}

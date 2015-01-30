package com.sunnydaycorp.simpletwitterapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.formatters.TwitterCountsFormatter;
import com.sunnydaycorp.simpletwitterapp.fragments.UserProfileFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.UserTaglineFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.UserTimelineFragment;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.ResultCode;

public class UserProfileActivity extends FragmentActivity {

	public static final String USER_ID_TAG = "user_id";

	private static final int NUM_PAGES = 2;

	private int count = 0;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

	private TextView tvTweetCount;
	private TextView tvFollowingCount;
	private TextView tvFollowersCount;

	private long userId;
	private boolean isOwnProfile;

	private UserProfileFragment userProfileFragment;
	private UserTaglineFragment userTaglineFragment;
	private UserTimelineFragment userTimelineFragment;

	public static final String LOG_TAG_CLASS = UserProfileActivity.class.getSimpleName();

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

		tvTweetCount = (TextView) findViewById(R.id.tvTweetCount);
		tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
		tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);

		userTaglineFragment = UserTaglineFragment.newInstance(userId);
		userProfileFragment = UserProfileFragment.newInstance(userId);

		FragmentTransaction sft = getSupportFragmentManager().beginTransaction();
		userTimelineFragment = UserTimelineFragment.newInstance(userId, isOwnProfile);
		sft.add(R.id.flUserTimelineContainer, userTimelineFragment);
		sft.commit();

		mPager = (ViewPager) findViewById(R.id.vpUserProfileContainer);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		populateUserCounts();

	}

	private void populateUserCounts() {
		count++;
		SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) getApplicationContext()).getSharedLoggedUserDetails();
		TwitterUser user = TwitterUser.byRemoteId(userId);
		if (user == null && isOwnProfile) {
			// if logged user is not in db then he/she does not have tweets anymore - any tweet count received on verify user details is out-dated
			user = new TwitterUser(loggedUserDetails.getUserId(), loggedUserDetails.getUserName(), loggedUserDetails.getUserScreenName(),
					loggedUserDetails.getUserProfilePicUrl(), loggedUserDetails.getUserProfileBackgroundPicUrl(), 0,
					loggedUserDetails.getFollowersCount(), loggedUserDetails.getFollowingCount(), loggedUserDetails.getUserTagline());
		}

		if (user != null) {
			tvTweetCount.setText(TwitterCountsFormatter.getCountString(user.getTweetsCount()));
			Log.e("COUNTS", "populate counts number " + count + ": " + user.getTweetsCount());
			tvFollowingCount.setText(TwitterCountsFormatter.getCountString(user.getFollowingCount()));
			tvFollowersCount.setText(TwitterCountsFormatter.getCountString(user.getFollowersCount()));

		} else {
			Log.d(LOG_TAG_CLASS, "User details are not found in DB");
		}

	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			super.onBackPressed();
		} else {
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return userProfileFragment;
			case 1:
				return userTaglineFragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	@Override
	public void onResume() {
		LocalBroadcastManager.getInstance(this).registerReceiver(newTweetPostedBroadcastReceiver, new IntentFilter("new-tweet-posted"));
		LocalBroadcastManager.getInstance(this).registerReceiver(countsUpdatedBroadcastReceiver, new IntentFilter("user-counts-updated"));
		populateUserCounts();
		super.onResume();
	}

	@Override
	public void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(newTweetPostedBroadcastReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(countsUpdatedBroadcastReceiver);
		super.onStop();
	}

}

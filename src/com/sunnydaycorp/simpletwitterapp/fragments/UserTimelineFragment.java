package com.sunnydaycorp.simpletwitterapp.fragments;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.activities.UserProfileActivity;
import com.sunnydaycorp.simpletwitterapp.listeners.TimelineResponseListener;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.TwitterAPIReqCode;

public class UserTimelineFragment extends TweetListFragment {

	private String screenUserName;
	private boolean isOwnProfile;
	private long userId = 0;

	public UserTimelineFragment() {
	}

	public static UserTimelineFragment newInstance(long userId, boolean isOwnProfile) {
		UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
		Bundle args = new Bundle();
		args.putLong(UserProfileActivity.USER_ID_TAG, userId);
		args.putBoolean("IS_OWN_PROFILE", isOwnProfile);
		userTimelineFragment.setArguments(args);
		return userTimelineFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userId = getArguments().getLong(UserProfileActivity.USER_ID_TAG, 0);
		isOwnProfile = getArguments().getBoolean("IS_OWN_PROFILE", false);
		Log.d("DEBUG2", "user time line is created for " + userId + "isOwnProfile:" + isOwnProfile);
		screenUserName = "@user";
		TwitterUser user = TwitterUser.byRemoteId(userId);
		if (user != null) {
			screenUserName = "@" + user.getUserScreenName();
		} else if (isOwnProfile) {
			SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) getActivity().getApplicationContext()).getSharedLoggedUserDetails();
			screenUserName = "@" + loggedUserDetails.getUserScreenName();
		}
		reloadRecentTweets();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public String getEmptyListMessage() {
		return screenUserName + " " + getActivity().getResources().getString(R.string.user_timeline_no_tweets_message);
	}

	@Override
	public void fetchTimelineTweets(TwitterAPIReqCode twitterAPIReqCode, TimelineResponseListener timelineResponseListener) {
		switch (twitterAPIReqCode) {
		case NEW_LOAD:
			Log.d("DEBUG2", "user timeline new load for " + userId + "isOwnProfile:" + isOwnProfile);
			twitterRestClient.fetchUserTimelineTweets(twitterAPIReqCode, timelineResponseListener, 0, 0, userId);
			break;
		case REFRESH_LOAD:
			Log.d("DEBUG2", "user timeline refresh load for " + userId + "isOwnProfile:" + isOwnProfile);
			twitterRestClient.fetchUserTimelineTweets(twitterAPIReqCode, timelineResponseListener, getFirstItemTweetId(), 0, userId);
			break;
		case MORE_OLD_LOAD:
			Log.d("DEBUG2", "user timeline more old load for " + userId + "isOwnProfile:" + isOwnProfile);
			twitterRestClient.fetchUserTimelineTweets(twitterAPIReqCode, timelineResponseListener, 0, getLastItemTweetId() - 1, userId);
			break;
		}

	}

	@Override
	public List<Tweet> getCachedRecentTweets() {
		return Tweet.recentUserTweets(userId);
	}

	@Override
	public void updateUserCountsTimelineRefreshed() {
		Intent intent = new Intent("user-counts-updated");
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
	}

}

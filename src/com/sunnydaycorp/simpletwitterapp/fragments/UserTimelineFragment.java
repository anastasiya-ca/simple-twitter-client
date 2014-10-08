package com.sunnydaycorp.simpletwitterapp.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.activities.UserProfileActivity;
import com.sunnydaycorp.simpletwitterapp.interfaces.TimelineResponseListener;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.TwitterAPIReqCode;

public class UserTimelineFragment extends TweetListFragment {

	private TwitterRestClient twitterRestClient;
	private String screenUserName;
	private long userId = 0;

	public UserTimelineFragment() {
	}

	public static UserTimelineFragment newInstance(long userId) {
		UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
		Bundle args = new Bundle();
		args.putLong(UserProfileActivity.USER_ID_TAG, userId);
		userTimelineFragment.setArguments(args);
		return userTimelineFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		twitterRestClient = ((SimpleTwitterApp) getActivity().getApplication()).getRestClient();
		userId = getArguments().getLong(UserProfileActivity.USER_ID_TAG, 0);
		screenUserName = "@user";
		TwitterUser user = TwitterUser.byRemoteId(userId);
		if (user != null) {
			screenUserName = "@" + user.getUserScreenName();
		}
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
			twitterRestClient.fetchUserTimelineTweets(twitterAPIReqCode, timelineResponseListener, 0, 0, userId);
			break;
		case REFRESH_LOAD:
			twitterRestClient.fetchUserTimelineTweets(twitterAPIReqCode, timelineResponseListener, getFirstItemTweetId(), 0, userId);
			break;
		case MORE_OLD_LOAD:
			twitterRestClient.fetchUserTimelineTweets(twitterAPIReqCode, timelineResponseListener, 0, getLastItemTweetId() - 1, userId);
			break;
		}

	}

	@Override
	public List<Tweet> getCachedRecentTweets() {
		return Tweet.recentUserTweets(userId);
	}

	@Override
	public boolean isClearDataRequiredOnNewLoad() {
		return false;
	}

}

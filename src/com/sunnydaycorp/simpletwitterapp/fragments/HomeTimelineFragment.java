package com.sunnydaycorp.simpletwitterapp.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.interfaces.TimelineResponseListener;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.TwitterAPIReqCode;

public class HomeTimelineFragment extends TweetListFragment {

	private TwitterRestClient twitterRestClient;

	public HomeTimelineFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		twitterRestClient = ((SimpleTwitterApp) getActivity().getApplication()).getRestClient();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public String getEmptyListMessage() {
		return getActivity().getResources().getString(R.string.home_timeline_no_tweets_message);
	}

	@Override
	public void fetchTimelineTweets(TwitterAPIReqCode twitterAPIReqCode, TimelineResponseListener timelineResponseListener) {
		switch (twitterAPIReqCode) {
		case NEW_LOAD:
			twitterRestClient.fetchHomeTimelineTweets(twitterAPIReqCode, timelineResponseListener, 0, 0);
			break;
		case REFRESH_LOAD:
			twitterRestClient.fetchHomeTimelineTweets(twitterAPIReqCode, timelineResponseListener, getFirstItemTweetId(), 0);
			break;
		case MORE_OLD_LOAD:
			twitterRestClient.fetchHomeTimelineTweets(twitterAPIReqCode, timelineResponseListener, 0, getLastItemTweetId() - 1);
			break;
		}

	}

	@Override
	public List<Tweet> getCachedRecentTweets() {
		return Tweet.recentHomeTweets();
	}

	@Override
	public boolean isClearDataRequiredOnNewLoad() {
		return true;
	}

}

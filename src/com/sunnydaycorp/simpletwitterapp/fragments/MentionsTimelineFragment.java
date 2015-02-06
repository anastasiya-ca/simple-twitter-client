package com.sunnydaycorp.simpletwitterapp.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.listeners.TimelineResponseListener;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.TwitterAPIReqCode;

public class MentionsTimelineFragment extends TweetListFragment {

	public MentionsTimelineFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reloadRecentTweets();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public String getEmptyListMessage() {
		return getActivity().getResources().getString(R.string.mentions_timeline_no_tweets_message);
	}

	@Override
	public void fetchTimelineTweets(TwitterAPIReqCode twitterAPIReqCode, TimelineResponseListener timelineResponseListener) {
		switch (twitterAPIReqCode) {
		case NEW_LOAD:
			twitterRestClient.fetchMentionsTimelineTweets(twitterAPIReqCode, timelineResponseListener, 0, 0);
			break;
		case REFRESH_LOAD:
			twitterRestClient.fetchMentionsTimelineTweets(twitterAPIReqCode, timelineResponseListener, getFirstItemTweetId(), 0);
			break;
		case MORE_OLD_LOAD:
			twitterRestClient.fetchMentionsTimelineTweets(twitterAPIReqCode, timelineResponseListener, 0, getLastItemTweetId() - 1);
			break;
		}
	}

	@Override
	public List<Tweet> getCachedRecentTweets() {
		return Tweet.recentMentionsTweets();
	}

}

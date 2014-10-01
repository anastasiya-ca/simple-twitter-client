package com.sunnydaycorp.simpletwitterapp.interfaces;

import java.util.List;

import com.sunnydaycorp.simpletwitterapp.activities.TimelineActivity.TwitterAPIReqCode;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public interface TwitterClientListener {
	public void onHomeTimelineFetched(TwitterAPIReqCode requestCode, List<Tweet> tweets);

	public void onNewTweetPosted(Tweet tweet);

	public void onUserCredentialsVerified(TwitterUser user);

	public void onError(TwitterRestClient.ResultCode resultCode);
}

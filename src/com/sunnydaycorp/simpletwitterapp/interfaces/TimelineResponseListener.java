package com.sunnydaycorp.simpletwitterapp.interfaces;

import java.util.List;

import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public interface TimelineResponseListener extends TwitterClientListener {

	public void onTimelineFetched(TwitterRestClient.TwitterAPIReqCode requestCode, List<Tweet> tweets);

}

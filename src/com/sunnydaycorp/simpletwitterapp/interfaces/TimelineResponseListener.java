package com.sunnydaycorp.simpletwitterapp.interfaces;

import java.util.List;

import android.content.Context;

import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public abstract class TimelineResponseListener extends TwitterClientListener {

	public TimelineResponseListener(Context context) {
		super(context);
	}

	public abstract void onTimelineFetched(TwitterRestClient.TwitterAPIReqCode requestCode, List<Tweet> tweets);

}

package com.sunnydaycorp.simpletwitterapp.interfaces;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public abstract class DatabaseAwareTimelineResponseListener implements TimelineResponseListener {

	private Context context;

	public DatabaseAwareTimelineResponseListener(Context context) {
		this.context = context;
	}

	protected void clearData() {
		TwitterUser.deleteAll();
		Tweet.deleteAll();
	}

	protected void saveTweets(List<Tweet> tweets) {
		for (Tweet tweet : tweets) {
			tweet.getUser().save();
			TwitterUser user = TwitterUser.byRemoteId(tweet.getUser().getUserId());
			Log.d("DEBUG", "User has been saved with " + user.getUserId() + " and DB id " + user.getId());
			tweet.setUser(user);
			tweet.save();
			Log.d("DEBUG", "Tweet has been saved with " + tweet.getTweetId() + " and DB id " + tweet.getId() + " and user id "
					+ tweet.getUser().getId());
		}
		Log.d("DEBUG", "On save there are " + TwitterUser.recordCount() + " users in DB");
		Log.d("DEBUG", "On save there are " + Tweet.recordCount() + " tweets in DB");
	}

	@Override
	public void onError(TwitterRestClient.ResultCode resultCode) {
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
		}

	}

}

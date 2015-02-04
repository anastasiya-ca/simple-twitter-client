package com.sunnydaycorp.simpletwitterapp.listeners;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;

public abstract class DatabaseAwareTimelineResponseListener extends TimelineResponseListener {

	public DatabaseAwareTimelineResponseListener(Context context) {
		super(context);
	}

	protected void saveTweets(List<Tweet> tweets) {
		HashMap<Long, TwitterUser> twitterUserMap = new HashMap<Long, TwitterUser>();
		for (Tweet tweet : tweets) {
			TwitterUser user = twitterUserMap.get(tweet.getUser().getUserId());
			if (user == null) {
				user = TwitterUser.byRemoteId(tweet.getUser().getUserId());
			}
			if (user == null) {
				// save new user
				tweet.getUser().save();
				user = tweet.getUser();
			} else {
				// update existing user
				user.setFollowersCount(tweet.getUser().getFollowersCount());
				user.setFollowingCount(tweet.getUser().getFollowingCount());
				user.setTweetsCount(tweet.getUser().getTweetsCount());
				user.setUserName(tweet.getUser().getUserName());
				user.setUserProfileBackgroundPicUrl(tweet.getUser().getUserProfileBackgroundPicUrl());
				user.setUserProfilePicUrl(tweet.getUser().getUserProfilePicUrl());
				user.setUserScreenName(tweet.getUser().getUserScreenName());
				user.setUserTagline(tweet.getUser().getUserTagline());
				user.save();
			}
			twitterUserMap.put(tweet.getUser().getUserId(), user);
			tweet.setUser(user);
			tweet.save();
			Log.d("DEBUG",
					"Misha saving tweet User:" + tweet.getUser().getUserName() + " tweet:" + tweet.getTweetId() + " Mention:"
							+ tweet.isMentionsTweet());

			Log.d("DEBUG", "Misha " + TwitterUser.recordCount() + " users in DB");
			Log.d("DEBUG", "Misha " + Tweet.recordCount() + " tweets in DB");
		}
		Log.d("DEBUG", "On save there are " + TwitterUser.recordCount() + " users in DB");
		Log.d("DEBUG", "On save there are " + Tweet.recordCount() + " tweets in DB");
	}
}

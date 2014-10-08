package com.sunnydaycorp.simpletwitterapp.interfaces;

import com.sunnydaycorp.simpletwitterapp.models.Tweet;

public interface OnNewTweetPostedTCListener extends TwitterClientListener {

	public void onNewTweetPosted(Tweet tweet);

}

package com.sunnydaycorp.simpletwitterapp.interfaces;

import android.content.DialogInterface;

public interface OnNewTweetCreatedListener {

	public void onNewTweetCreated(DialogInterface dialog, String tweetText);

}
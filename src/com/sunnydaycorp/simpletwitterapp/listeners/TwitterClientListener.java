package com.sunnydaycorp.simpletwitterapp.listeners;

import android.content.Context;
import android.widget.Toast;

import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.ResultCode;

public abstract class TwitterClientListener {

	protected Context context;

	public TwitterClientListener(Context context) {
		this.context = context;
	}

	public void onErrorResult(ResultCode resultCode) {
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

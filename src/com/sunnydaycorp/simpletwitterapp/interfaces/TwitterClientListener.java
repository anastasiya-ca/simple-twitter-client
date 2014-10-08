package com.sunnydaycorp.simpletwitterapp.interfaces;

import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public interface TwitterClientListener {

	public void onError(TwitterRestClient.ResultCode resultCode);
}

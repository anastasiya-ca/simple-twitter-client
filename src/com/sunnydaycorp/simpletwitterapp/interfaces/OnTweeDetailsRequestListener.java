package com.sunnydaycorp.simpletwitterapp.interfaces;

import java.io.Serializable;

import android.support.v4.app.Fragment;

public interface OnTweeDetailsRequestListener extends Serializable {

	public void onNewTweetDetailsRequest(Fragment fragment, long tweetId);

}

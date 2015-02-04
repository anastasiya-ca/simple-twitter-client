package com.sunnydaycorp.simpletwitterapp.listeners;

import android.content.Context;

public abstract class LoggedUserInfoResponseListener extends TwitterClientListener {

	public LoggedUserInfoResponseListener(Context context) {
		super(context);
	}

	public abstract void onSuccess();

}

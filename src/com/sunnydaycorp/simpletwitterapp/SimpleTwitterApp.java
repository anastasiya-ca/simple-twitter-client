package com.sunnydaycorp.simpletwitterapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public class SimpleTwitterApp extends com.activeandroid.app.Application {

	private SharedLoggedUserDetails sharedLoggedUserDetails;

	@Override
	public void onCreate() {
		super.onCreate();
		sharedLoggedUserDetails = new SharedLoggedUserDetails(this);

		// set config for ImageLoader
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
				.build();
		ImageLoader.getInstance().init(config);

	}

	public boolean hasInternetConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public TwitterRestClient getRestClient() {
		return (TwitterRestClient) TwitterRestClient.getInstance(TwitterRestClient.class, this);
	}

	public SharedLoggedUserDetails getSharedLoggedUserDetails() {
		return sharedLoggedUserDetails;
	}

}
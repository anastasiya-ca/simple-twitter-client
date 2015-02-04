package com.sunnydaycorp.simpletwitterapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.listeners.LoggedUserInfoResponseListener;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.ResultCode;

public class LoginActivity extends OAuthLoginActivity<TwitterRestClient> {

	public static final String LOG_TAG_CLASS = LoginActivity.class.getSimpleName();

	private Button btnLogin;
	private ProgressBar pbLoadingUserDetails;
	private LoggedUserInfoResponseListener loggedUserInfoResponseListener = new LoggedUserInfoResponseListener(this) {

		@Override
		public void onSuccess() {
			// internet connection, twitter service responsiveness and user credentials are checked
			// local db is to be cleaned - a placholder because proper sync with removing deleted tweets is not implemented
			TwitterUser.deleteAll();
			Tweet.deleteAll();
			openTimelineActivity();
		}

		@Override
		public void onErrorResult(ResultCode resultCode) {
			super.onErrorResult(resultCode);
			Toast.makeText(context, "Your local data has not been syncronized with Twitter", Toast.LENGTH_SHORT).show();
			openTimelineActivity();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		pbLoadingUserDetails = (ProgressBar) findViewById(R.id.pbLoadingUserDetails);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginToRest(v);
			}
		});
	}

	@Override
	public void onLoginSuccess() {
		btnLogin.setVisibility(View.GONE);
		pbLoadingUserDetails.setVisibility(View.VISIBLE);
		TwitterRestClient twitterRestClient = ((SimpleTwitterApp) getApplication()).getRestClient();
		twitterRestClient.verifyAndGetUserCredentials(loggedUserInfoResponseListener);
	}

	private void openTimelineActivity() {
		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onLoginFailure(Exception e) {
		Toast.makeText(this, "Login failed. Please try to login again", Toast.LENGTH_SHORT).show();
		Log.e(LOG_TAG_CLASS, e.toString());
	}

	public void loginToRest(View view) {
		getClient().connect();
	}

}

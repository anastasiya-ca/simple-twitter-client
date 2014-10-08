package com.sunnydaycorp.simpletwitterapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public class LoginActivity extends OAuthLoginActivity<TwitterRestClient> {

	private Button btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginToRest(v);

			}
		});
		return true;
	}

	@Override
	public void onLoginSuccess() {
		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onLoginFailure(Exception e) {
		Toast.makeText(this, "Login failed. Please try to login again", Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	}

	public void loginToRest(View view) {
		getClient().connect();
	}

}

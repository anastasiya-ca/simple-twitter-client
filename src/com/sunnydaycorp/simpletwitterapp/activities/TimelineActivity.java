package com.sunnydaycorp.simpletwitterapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.adapters.TimelineActivityTabsPagerAdapter;
import com.sunnydaycorp.simpletwitterapp.fragments.HomeTimelineFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.MentionsTimelineFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.NewTweetFragment;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient.ResultCode;

public class TimelineActivity extends ActionBarActivity implements ActionBar.TabListener {

	public static final String USER_ID_EXTRA_TAG = "USER_ID";

	private TimelineActivityTabsPagerAdapter viewPagerAdapter;
	private ViewPager viewPager;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ResultCode resultCode = (ResultCode) intent.getSerializableExtra("RESULT_CODE");

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
			case OK:
				Toast.makeText(context, "Your tweet has been posted", Toast.LENGTH_SHORT).show();
				HomeTimelineFragment homeTimelineFragment = viewPagerAdapter.getHomeTimelineFragment();
				if (homeTimelineFragment != null) {
					homeTimelineFragment.refreshTimeline();
				}
				// only required if user mentions himself
				MentionsTimelineFragment mentionsTimelineFragment = viewPagerAdapter.getMentionsTimelineFragment();
				if (mentionsTimelineFragment != null) {
					mentionsTimelineFragment.refreshTimeline();
				}
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		setupTabs();
	}

	private void setupTabs() {
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		viewPagerAdapter = new TimelineActivityTabsPagerAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.vpTimelineActivityContainer);
		viewPager.setAdapter(viewPagerAdapter);

		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(viewPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_timeline_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_new_tweet) {
			showNewTweetDialog();
			return true;
		}
		if (id == R.id.action_view_profile) {
			Intent i = new Intent(this, UserProfileActivity.class);
			i.putExtra(USER_ID_EXTRA_TAG, Long.valueOf(((SimpleTwitterApp) getApplicationContext()).getSharedLoggedUserDetails().getUserId()));
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showNewTweetDialog() {
		DialogFragment newFragment = NewTweetFragment.newInstance(null);
		newFragment.show(getSupportFragmentManager(), "onNewTweetDialog");
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (viewPager.getCurrentItem() != tab.getPosition())
			viewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onResume() {
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("new-tweet-posted"));
		super.onResume();
	}

	@Override
	public void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		super.onStop();
	}
}

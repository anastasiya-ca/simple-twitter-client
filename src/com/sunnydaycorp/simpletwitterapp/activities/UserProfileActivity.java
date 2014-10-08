package com.sunnydaycorp.simpletwitterapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.fragments.UserProfileFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.UserTaglineFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.UserTimelineFragment;

public class UserProfileActivity extends FragmentActivity {

	public static final String USER_ID_TAG = "user_id";

	private static final int NUM_PAGES = 2;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	long userId = 0;

	private UserProfileFragment userProfileFragment;
	private UserTaglineFragment userTaglineFragment;
	private UserTimelineFragment userTimelineFragment;

	public static final String LOG_TAG_CLASS = UserProfileActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		userId = getIntent().getLongExtra(TimelineActivity.USER_ID_EXTRA_TAG, 0);

		userTaglineFragment = UserTaglineFragment.newInstance(userId);
		userProfileFragment = UserProfileFragment.newInstance(userId);

		FragmentTransaction sft = getSupportFragmentManager().beginTransaction();
		userTimelineFragment = UserTimelineFragment.newInstance(userId);
		sft.add(R.id.flUserTimelineContainer, userTimelineFragment);
		sft.commit();

		mPager = (ViewPager) findViewById(R.id.vpUserProfileContainer);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			super.onBackPressed();
		} else {
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return userProfileFragment;
			case 1:
				return userTaglineFragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

}

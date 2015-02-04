package com.sunnydaycorp.simpletwitterapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sunnydaycorp.simpletwitterapp.fragments.UserProfileFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.UserTaglineFragment;

public class UserProfileDetailsPagerAdapter extends FragmentPagerAdapter {

	private UserProfileFragment userProfileFragment;
	private UserTaglineFragment userTaglineFragment;

	public UserProfileDetailsPagerAdapter(FragmentManager fm, long userId) {
		super(fm);
		userTaglineFragment = UserTaglineFragment.newInstance(userId);
		userProfileFragment = UserProfileFragment.newInstance(userId);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return userProfileFragment;
		case 1:
			return userTaglineFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return "Profile";
		case 1:
			return "Tagline";
		}
		return null;
	}

	public UserProfileFragment getUserProfileFragment() {
		return userProfileFragment;
	}

	public UserTaglineFragment getUserTaglineFragment() {
		return userTaglineFragment;
	}

}

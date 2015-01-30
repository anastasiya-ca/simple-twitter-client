package com.sunnydaycorp.simpletwitterapp.adapters;

import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sunnydaycorp.simpletwitterapp.fragments.HomeTimelineFragment;
import com.sunnydaycorp.simpletwitterapp.fragments.MentionsTimelineFragment;

public class TimelineActivityTabsPagerAdapter extends FragmentPagerAdapter {

	private HomeTimelineFragment homeTimelineFragment;
	private MentionsTimelineFragment mentionsTimelineFragment;

	public TimelineActivityTabsPagerAdapter(FragmentManager fm) {
		super(fm);
		homeTimelineFragment = new HomeTimelineFragment();
		mentionsTimelineFragment = new MentionsTimelineFragment();
	}

	@Override
	public Fragment getItem(int position) {

		switch (position) {
		case 0:
			return homeTimelineFragment;
		case 1:
			return mentionsTimelineFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return "Home";
		case 1:
			return "Mentions";
		}
		return null;
	}

	public HomeTimelineFragment getHomeTimelineFragment() {
		return homeTimelineFragment;
	}

	public MentionsTimelineFragment getMentionsTimelineFragment() {
		return mentionsTimelineFragment;
	}

}

package com.sunnydaycorp.simpletwitterapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.activities.UserProfileActivity;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;

public class UserTaglineFragment extends Fragment {

	public static final String LOG_TAG_CLASS = UserTaglineFragment.class.getSimpleName();

	private ImageView ivUserProfileBackground;
	private TextView tvTagline;

	private long userId;
	private boolean hasTagline;

	public UserTaglineFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userId = getArguments().getLong(UserProfileActivity.USER_ID_TAG, 0);
	}

	public static UserTaglineFragment newInstance(long userId) {
		UserTaglineFragment userTaglineFragment = new UserTaglineFragment();
		Bundle args = new Bundle();
		args.putLong(UserProfileActivity.USER_ID_TAG, userId);
		userTaglineFragment.setArguments(args);
		return userTaglineFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_tagline, container, false);
		ivUserProfileBackground = (ImageView) view.findViewById(R.id.ivUserProfileBackground);
		tvTagline = (TextView) view.findViewById(R.id.tvTagline);
		populateUserDetails();
		return view;
	}

	private void populateUserDetails() {
		SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) getActivity().getApplicationContext()).getSharedLoggedUserDetails();

		TwitterUser user = TwitterUser.byRemoteId(userId);
		if (user == null && userId == loggedUserDetails.getUserId()) {
			user = new TwitterUser(loggedUserDetails.getUserId(), loggedUserDetails.getUserName(), loggedUserDetails.getUserScreenName(),
					loggedUserDetails.getUserProfilePicUrl(), loggedUserDetails.getUserProfileBackgroundPicUrl(), loggedUserDetails.getTweetsCount(),
					loggedUserDetails.getFollowersCount(), loggedUserDetails.getFollowingCount(), loggedUserDetails.getUserTagline());
		}
		if (user != null) {
			if (user.getUserTagline() != null && !user.getUserTagline().isEmpty()) {
				hasTagline = true;
				tvTagline.setText(user.getUserTagline());
				if (!user.getUserProfileBackgroundPicUrl().isEmpty()) {
					ImageLoader imageLoader = ImageLoader.getInstance();
					imageLoader.displayImage(user.getUserProfileBackgroundPicUrl(), ivUserProfileBackground);
				}
			}
		} else {
			Log.d(LOG_TAG_CLASS, "User details are not found in DB");
		}

	}

	public boolean hasTagline() {
		return hasTagline;
	}

}

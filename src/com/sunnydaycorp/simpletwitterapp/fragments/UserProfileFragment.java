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

public class UserProfileFragment extends Fragment {

	public static final String LOG_TAG_CLASS = UserProfileFragment.class.getSimpleName();

	private ImageView ivUserProfileBackground;
	private ImageView ivUserProfilePic;
	private TextView tvUserName;
	private TextView tvUserScreenName;

	private long userId;

	public UserProfileFragment() {
	}

	public static UserProfileFragment newInstance(long userId) {
		UserProfileFragment userProfileFragment = new UserProfileFragment();
		Bundle args = new Bundle();
		args.putLong(UserProfileActivity.USER_ID_TAG, userId);
		userProfileFragment.setArguments(args);
		return userProfileFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userId = getArguments().getLong(UserProfileActivity.USER_ID_TAG, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_profile, container, false);

		ivUserProfileBackground = (ImageView) view.findViewById(R.id.ivUserProfileBackground);
		ivUserProfilePic = (ImageView) view.findViewById(R.id.ivUserProfilePic);
		tvUserName = (TextView) view.findViewById(R.id.tvUserName);
		tvUserScreenName = (TextView) view.findViewById(R.id.tvUserScreenName);
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
			tvUserName.setText(user.getUserName());
			tvUserScreenName.setText("@" + user.getUserScreenName());
			if (!user.getUserProfilePicUrl().isEmpty()) {
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(user.getUserProfilePicUrl(), ivUserProfilePic);
			}
			if (!user.getUserProfileBackgroundPicUrl().isEmpty()) {
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(user.getUserProfileBackgroundPicUrl(), ivUserProfileBackground);
			}

		} else {
			Log.d(LOG_TAG_CLASS, "User details are not found in DB");
		}

	}

}

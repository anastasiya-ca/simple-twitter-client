package com.sunnydaycorp.simpletwitterapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedLoggedUserDetails {

	public static final String USER_ID_KEY = "user_id";
	public static final String USER_NAME_KEY = "user_name";
	public static final String USER_SCREEN_NAME_KEY = "user_screen_name";
	public static final String USER_PROFILE_PIC_URL_KEY = "profile_pic_url";
	public static final String USER_PROFILE_BACKGROUND_PIC_URL_KEY = "profile_background_pic_url";
	public static final String USER_TWEET_COUNT_KEY = "tweet_count";
	public static final String USER_FOLLOWERS_COUNT_KEY = "followers_count";
	public static final String USER_FOLLOWING_COUNT_KEY = "following_count";
	public static final String USER_TAGLINE_KEY = "user_tagline";

	private long userId;
	private String userName;
	private String userScreenName;
	private String userProfilePicUrl;
	private String userProfileBackgroundPicUrl;
	private String userTagline;
	private long tweetsCount;
	private long followersCount;
	private long followingCount;

	private final SharedPreferences pref;

	public SharedLoggedUserDetails(Context context) {
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		userId = pref.getLong(USER_ID_KEY, 0);
		userName = pref.getString(USER_NAME_KEY, "");
		userScreenName = pref.getString(USER_SCREEN_NAME_KEY, "");
		userProfilePicUrl = pref.getString(USER_PROFILE_PIC_URL_KEY, "");
		userProfileBackgroundPicUrl = pref.getString(USER_PROFILE_BACKGROUND_PIC_URL_KEY, "");
		tweetsCount = pref.getLong(USER_TWEET_COUNT_KEY, 0L);
		followersCount = pref.getLong(USER_FOLLOWERS_COUNT_KEY, 0L);
		followingCount = pref.getLong(USER_FOLLOWING_COUNT_KEY, 0L);
		userTagline = pref.getString(USER_TAGLINE_KEY, "");
	}

	public boolean savePreferences(long userId, String userName, String userScreenName, String userProfilePicUrl, String userProfileBackgroundPicUrl,
			long tweetsCount, long followersCount, long followingCount, String userTagline) {
		this.userId = userId;
		this.userName = userName;
		this.userScreenName = userScreenName;
		this.userProfilePicUrl = userProfilePicUrl;
		this.userProfileBackgroundPicUrl = userProfileBackgroundPicUrl;
		this.tweetsCount = tweetsCount;
		this.followersCount = followersCount;
		this.followingCount = followingCount;
		this.userTagline = userTagline;

		Editor prefEditor = pref.edit();
		prefEditor.putLong(USER_ID_KEY, userId);
		prefEditor.putString(USER_NAME_KEY, userName);
		prefEditor.putString(USER_SCREEN_NAME_KEY, userScreenName);
		prefEditor.putString(USER_PROFILE_PIC_URL_KEY, userProfilePicUrl);
		prefEditor.putString(USER_PROFILE_BACKGROUND_PIC_URL_KEY, userProfileBackgroundPicUrl);
		prefEditor.putString(USER_TAGLINE_KEY, userTagline);
		prefEditor.putLong(USER_TWEET_COUNT_KEY, tweetsCount);
		prefEditor.putLong(USER_FOLLOWERS_COUNT_KEY, followersCount);
		prefEditor.putLong(USER_FOLLOWING_COUNT_KEY, followingCount);
		prefEditor.commit();
		return true;
	}

	public long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserScreenName() {
		return userScreenName;
	}

	public String getUserProfilePicUrl() {
		return userProfilePicUrl;
	}

	public String getUserProfileBackgroundPicUrl() {
		return userProfileBackgroundPicUrl;
	}

	public String getUserTagline() {
		return userTagline;
	}

	public long getTweetsCount() {
		return tweetsCount;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	public long getFollowingCount() {
		return followingCount;
	}

	public void setTweetsCount(long tweetsCount) {
		this.tweetsCount = tweetsCount;
	}

}

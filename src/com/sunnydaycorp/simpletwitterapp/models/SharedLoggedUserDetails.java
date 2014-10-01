package com.sunnydaycorp.simpletwitterapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedLoggedUserDetails {

	public static final String USER_ID_KEY = "user_id";
	public static final String USER_NAME_KEY = "user_name";
	public static final String USER_SCREEN_NAME_KEY = "user_screen_name";
	public static final String USER_PROFILE_PIC_URL = "profile_pic_url";

	private String userId;
	private String userName;
	private String userScreenName;
	private String userProfilePicUrl;
	private final SharedPreferences pref;

	public SharedLoggedUserDetails(Context context) {
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		userId = pref.getString(USER_ID_KEY, "");
		userName = pref.getString(USER_NAME_KEY, "");
		userScreenName = pref.getString(USER_SCREEN_NAME_KEY, "");
		userProfilePicUrl = pref.getString(USER_PROFILE_PIC_URL, "");
	}

	public boolean savePreferences(long userId, String userName, String userScreenName, String userProfilePicUrl) {
		this.userId = String.valueOf(userId);
		this.userName = userName;
		this.userScreenName = userScreenName;
		this.userProfilePicUrl = userProfilePicUrl;

		Editor prefEditor = pref.edit();
		prefEditor.putString(USER_ID_KEY, String.valueOf(userId));
		prefEditor.putString(USER_NAME_KEY, userName);
		prefEditor.putString(USER_SCREEN_NAME_KEY, userScreenName);
		prefEditor.putString(USER_PROFILE_PIC_URL, userProfilePicUrl);
		prefEditor.commit();
		return true;
	}

	public String getUserId() {
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

}

package com.sunnydaycorp.simpletwitterapp.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "twitter_users")
public class TwitterUser extends Model {

	@Column(name = "remote_user_id", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
	private long twitterUserId;
	@Column(name = "user_name")
	private String userName;
	@Column(name = "user_screen_name")
	private String userScreenName;
	@Column(name = "profile_pic_url")
	private String userProfilePicUrl;
	@Column(name = "profile_background_pic_url")
	private String userProfileBackgroundPicUrl;
	@Column(name = "tweets_count")
	private long tweetsCount;
	@Column(name = "followers_count")
	private long followersCount;
	@Column(name = "following_count")
	private long followingCount;
	@Column(name = "user_tagline")
	private String userTagline;

	public TwitterUser(long twitterUserId, String userName, String userScreenName, String userProfilePicUrl, String userProfileBackgroundPicUrl,
			long tweetsCount, long followersCount, long followingCount, String userTagline) {
		super();
		this.twitterUserId = twitterUserId;
		this.userName = userName;
		this.userScreenName = userScreenName;
		this.userProfilePicUrl = userProfilePicUrl;
		this.userProfileBackgroundPicUrl = userProfileBackgroundPicUrl;
		this.tweetsCount = tweetsCount;
		this.followersCount = followersCount;
		this.followingCount = followingCount;
		this.userTagline = userTagline;
	}

	public TwitterUser() {
		super();
	}

	public static TwitterUser fromJSON(JSONObject twitterUserJSON) throws JSONException {
		TwitterUser twitterUser = null;
		if (twitterUserJSON != null) {
			twitterUser = new TwitterUser();
			twitterUser.setUserId(twitterUserJSON.optLong("id"));
			twitterUser.setUserName(twitterUserJSON.optString("name"));
			twitterUser.setUserProfilePicUrl(twitterUserJSON.optString("profile_image_url"));
			twitterUser.setUserScreenName(twitterUserJSON.optString("screen_name"));
			twitterUser.setUserProfileBackgroundPicUrl(twitterUserJSON.optString("profile_banner_url"));
			twitterUser.setTweetsCount(twitterUserJSON.optLong("statuses_count"));
			twitterUser.setFollowersCount(twitterUserJSON.optLong("followers_count"));
			twitterUser.setFollowingCount(twitterUserJSON.optLong("friends_count"));
			twitterUser.setUserTagline(twitterUserJSON.optString("description"));
		}
		return twitterUser;
	}

	public static List<TwitterUser> fromJSONArray(JSONArray jsonTwitterUserArray) throws JSONException {
		ArrayList<TwitterUser> twitterUsers = null;
		if (jsonTwitterUserArray != null) {
			twitterUsers = new ArrayList<TwitterUser>(jsonTwitterUserArray.length());
			for (int i = 0; i < jsonTwitterUserArray.length(); i++) {
				JSONObject twitterUserJSON = jsonTwitterUserArray.getJSONObject(i);
				twitterUsers.add(fromJSON(twitterUserJSON));
			}
		} else {
			twitterUsers = new ArrayList<TwitterUser>();
		}
		return twitterUsers;
	}

	public static TwitterUser byRemoteId(long id) {
		return new Select().from(TwitterUser.class).where("remote_user_id = ?", id).executeSingle();
	}

	public static TwitterUser byId(long id) {
		return new Select().from(TwitterUser.class).where("id = ?", id).executeSingle();
	}

	public static List<TwitterUser> recentItems() {
		return new Select().from(TwitterUser.class).orderBy("remote_user_id DESC").limit("300").execute();
	}

	public static int recordCount() {
		return (new Select().from(TwitterUser.class).execute()).size();
	}

	public List<Tweet> getUserTweets() {
		return getMany(Tweet.class, "user");
	}

	public static List<TwitterUser> deleteAll() {
		return new Delete().from(TwitterUser.class).execute();
	}

	public long getUserId() {
		return twitterUserId;
	}

	public void setUserId(long userId) {
		this.twitterUserId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserTagline() {
		return userTagline;
	}

	public void setUserTagline(String tagline) {
		this.userTagline = tagline;
	}

	public String getUserScreenName() {
		return userScreenName;
	}

	public void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}

	public String getUserProfilePicUrl() {
		return userProfilePicUrl;
	}

	public void setUserProfilePicUrl(String userProfilePicUrl) {
		this.userProfilePicUrl = userProfilePicUrl;
	}

	public String getUserProfileBackgroundPicUrl() {
		return userProfileBackgroundPicUrl;
	}

	public void setUserProfileBackgroundPicUrl(String userProfileBackgroundPicUrl) {
		this.userProfileBackgroundPicUrl = userProfileBackgroundPicUrl;
	}

	public long getTweetsCount() {
		return tweetsCount;
	}

	public void setTweetsCount(long tweetsCount) {
		this.tweetsCount = tweetsCount;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(long followersCount) {
		this.followersCount = followersCount;
	}

	public long getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(long followingCount) {
		this.followingCount = followingCount;
	}

}

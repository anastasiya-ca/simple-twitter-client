package com.sunnydaycorp.simpletwitterapp.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "tweets")
public class Tweet extends Model {

	// "Tue Aug 28 21:16:23 +0000 2012"
	public static final String TWEETER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

	@Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long tweetId;
	@Column(name = "created_at")
	private long createdAt;
	@Column(name = "text")
	private String text;
	@Column(name = "favorites_count")
	private long favoritesCount;
	@Column(name = "retweet_count")
	private long retweetCount;
	@Column(name = "img_url")
	private String displayImgUrl;

	@Column(name = "is_mentions_tweet")
	private boolean isMentionsTweet;

	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private TwitterUser user;

	public Tweet() {
		super();
	}

	public Tweet(long tweetId, TwitterUser user, long createdAt, String text, long favoritesCount, long retweetCount) {
		super();
		this.tweetId = tweetId;
		this.user = user;
		this.createdAt = createdAt;
		this.text = text;
		this.favoritesCount = favoritesCount;
		this.retweetCount = retweetCount;
	}

	public static Tweet fromJSON(JSONObject tweetJSON, boolean isMentionsTweet) throws JSONException {
		Tweet tweet = null;
		if (tweetJSON != null) {
			tweet = new Tweet();
			tweet.setTweetId(tweetJSON.optLong("id"));
			tweet.setUser(TwitterUser.fromJSON(tweetJSON.getJSONObject("user")));
			tweet.setCreatedAt(tweetJSON.optString("created_at"));
			tweet.setText(tweetJSON.optString("text"));
			tweet.setFavoritesCount(tweetJSON.optLong("favorite_count"));
			tweet.setRetweetCount(tweetJSON.optLong("retweet_count"));
			tweet.setMentionsTweet(isMentionsTweet);
		}
		return tweet;
	}

	public static List<Tweet> fromJSONArray(JSONArray jsonTweetArray, boolean isMentionsTweet) throws JSONException {
		ArrayList<Tweet> tweets = null;
		if (jsonTweetArray != null) {
			tweets = new ArrayList<Tweet>(jsonTweetArray.length());
			for (int i = 0; i < jsonTweetArray.length(); i++) {
				JSONObject tweetJSON = jsonTweetArray.getJSONObject(i);
				tweets.add(fromJSON(tweetJSON, isMentionsTweet));
			}
		} else {
			tweets = new ArrayList<Tweet>();
		}
		return tweets;
	}

	public static Tweet byTweetId(long id) {
		return new Select().from(Tweet.class).where("remote_id = ?", id).executeSingle();
	}

	public static List<Tweet> recentHomeTweets() {
		return new Select().from(Tweet.class).orderBy("remote_id DESC").limit("25").execute();
	}

	public static List<Tweet> recentUserTweets(long remoteUserId) {
		TwitterUser user = TwitterUser.byRemoteId(remoteUserId);
		if (user != null) {
			return new Select().from(Tweet.class).where("user = ?", user.getId()).orderBy("remote_id DESC").limit("25").execute();
		} else {
			return new ArrayList<Tweet>();
		}
	}

	public static List<Tweet> recentMentionsTweets() {
		return new Select().from(Tweet.class).where("is_mentions_tweet = ?", true).orderBy("remote_id DESC").limit("25").execute();
	}

	public static int recordCount() {
		return (new Select().from(Tweet.class).execute()).size();
	}

	public static List<Tweet> deleteAll() {
		return new Delete().from(Tweet.class).execute();
	}

	public long getTweetId() {
		return tweetId;
	}

	public void setTweetId(long id) {
		this.tweetId = id;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public Date getCreatedAtDate() {
		return new Date(createdAt);
	}

	public void setCreatedAt(String createdAtString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(TWEETER_DATE_FORMAT, Locale.ENGLISH);
		dateFormat.setLenient(true);
		Date date = null;
		try {
			date = dateFormat.parse(createdAtString);
		} catch (ParseException pe) {
			Log.e("DATE_PARSING", pe.toString());
		}
		this.createdAt = date.getTime();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public TwitterUser getUser() {
		return user;
	}

	public void setUser(TwitterUser user) {
		this.user = user;
	}

	public long getFavoritesCount() {
		return favoritesCount;
	}

	public void setFavoritesCount(long favoritesCount) {
		this.favoritesCount = favoritesCount;
	}

	public long getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(long retweetCount) {
		this.retweetCount = retweetCount;
	}

	public String getDisplayImgUrl() {
		return displayImgUrl;
	}

	public void setDisplayImgUrl(String displayImgUrl) {
		this.displayImgUrl = displayImgUrl;
	}

	public boolean isMentionsTweet() {
		return isMentionsTweet;
	}

	public void setMentionsTweet(boolean isMentionsTweet) {
		this.isMentionsTweet = isMentionsTweet;
	}

}

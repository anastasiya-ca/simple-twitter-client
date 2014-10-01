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

	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private TwitterUser user;

	public Tweet() {
		super();
	}

	public Tweet(long tweetId, TwitterUser user, long createdAt, String text) {
		super();
		this.tweetId = tweetId;
		this.user = user;
		this.createdAt = createdAt;
		this.text = text;
	}

	public static Tweet fromJSON(JSONObject tweetJSON) throws JSONException {
		Tweet tweet = new Tweet();
		tweet.setTweetId(tweetJSON.optLong("id"));
		tweet.setUser(TwitterUser.fromJSON(tweetJSON.getJSONObject("user")));
		tweet.setCreatedAt(tweetJSON.optString("created_at"));
		tweet.setText(tweetJSON.optString("text"));
		return tweet;
	}

	public static List<Tweet> fromJSONArray(JSONArray jsonTweetArray) throws JSONException {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonTweetArray.length());
		for (int i = 0; i < jsonTweetArray.length(); i++) {
			JSONObject tweetJSON = jsonTweetArray.getJSONObject(i);
			tweets.add(fromJSON(tweetJSON));
		}
		return tweets;
	}

	public static Tweet byTweetId(long id) {
		return new Select().from(Tweet.class).where("remote_id = ?", id).executeSingle();
	}

	public static List<Tweet> recentItems() {
		return new Select().from(Tweet.class).orderBy("remote_id DESC").limit("20").execute();
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
			Log.e("JSON_PARSING", pe.toString());
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
}

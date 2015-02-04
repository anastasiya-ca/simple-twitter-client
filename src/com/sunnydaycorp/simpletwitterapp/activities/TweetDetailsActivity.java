package com.sunnydaycorp.simpletwitterapp.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.fragments.TweetListFragment;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;

public class TweetDetailsActivity extends Activity {

	public static final String LOG_TAG_CLASS = TweetDetailsActivity.class.getSimpleName();
	public static final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a \u00B7 dd MMM yy");

	private ImageView ivUserProfilePic;
	private ImageView ivTweetImage;
	private TextView tvUserName;
	private TextView tvUserScreenName;
	private TextView tvTimestamp;
	private TextView tvTweetText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_details);
		setupViews();
	}

	private void setupViews() {
		ivUserProfilePic = (ImageView) findViewById(R.id.ivUserProfilePic);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
		tvTimestamp = (TextView) findViewById(R.id.tvTimestamp);
		tvTweetText = (TextView) findViewById(R.id.tvTweetText);
		ivTweetImage = (ImageView) findViewById(R.id.ivTweetImage);

		long tweetId = getIntent().getLongExtra(TweetListFragment.TWEET_ID_EXTRA_TAG, 0);
		Tweet tweet = Tweet.byTweetId(tweetId);
		if (tweet != null) {
			if (tweet.getUser() != null) {
				tvUserName.setText(tweet.getUser().getUserName());
				tvUserScreenName.setText("@" + tweet.getUser().getUserScreenName());
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(tweet.getUser().getUserProfilePicUrl(), ivUserProfilePic);
			} else {
				Log.d(LOG_TAG_CLASS, "user is not found in local DB");
			}
			tvTweetText.setText(tweet.getText());
			tvTimestamp.setText(sdf.format(new Date(tweet.getCreatedAt())));
		} else {
			Log.d(LOG_TAG_CLASS, "tweet with id " + tweetId + " is not found in local DB");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

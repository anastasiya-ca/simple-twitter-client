package com.sunnydaycorp.simpletwitterapp.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;

public class TweetDetailsActivity extends Activity {

	public static final String LOG_TAG_CLASS = TweetDetailsActivity.class.getSimpleName();
	public static final String IMAGE_LOADING_ERROR_MESSAGE = "Sorry image is not available";

	private ImageView ivUserProfilePic;
	private TextView tvUserName;
	private TextView tvUserScreenName;
	private TextView tvTimeStamp;
	private ImageView ivTweetImage;
	private TextView tvTweetText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_details);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		setupViews();
		long tweetId = getIntent().getLongExtra(TimelineActivity.TWEET_ID_EXTRA_TAG, 0);
		Tweet tweet = Tweet.byTweetId(tweetId);
		if (tweet != null) {
			if (tweet.getUser() != null) {
				tvUserName.setText(tweet.getUser().getUserName());
				tvUserScreenName.setText("@" + tweet.getUser().getUserScreenName());
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(tweet.getUser().getUserProfilePicUrl(), ivUserProfilePic);

			}

			tvTweetText.setText(tweet.getText());
			ivTweetImage.setVisibility(View.GONE);
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a \u00B7 dd MMM yy");
			tvTimeStamp.setText(sdf.format(new Date(tweet.getCreatedAt())));
		}
	}

	private void setupViews() {
		ivUserProfilePic = (ImageView) findViewById(R.id.ivUserProfilePic);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
		tvTimeStamp = (TextView) findViewById(R.id.tvTimeStamp);
		tvTweetText = (TextView) findViewById(R.id.tvTweetText);
		ivTweetImage = (ImageView) findViewById(R.id.ivTweetImage);
	}
}

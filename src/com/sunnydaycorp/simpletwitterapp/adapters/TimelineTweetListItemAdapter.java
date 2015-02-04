package com.sunnydaycorp.simpletwitterapp.adapters;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.activities.TimelineActivity;
import com.sunnydaycorp.simpletwitterapp.activities.UserProfileActivity;
import com.sunnydaycorp.simpletwitterapp.formatters.ElapsedTimeFormatter;
import com.sunnydaycorp.simpletwitterapp.fragments.NewTweetFragment;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;

public class TimelineTweetListItemAdapter extends ArrayAdapter<Tweet> {

	private DisplayMetrics outMetrics;

	private static class ViewHolder {
		ImageView ivRetweetedIcon;
		TextView tvRetweetedText;
		ImageView ivUserProfilePic;
		TextView tvUserName;
		TextView tvTimeStamp;
		TextView tvUserScreenName;
		TextView tvTweetText;
		ImageView ivReplyIcon;
		TextView tvRetweetedCount;
		TextView tvFollowersCount;
	}

	public TimelineTweetListItemAdapter(Context context, List<Tweet> tweets) {
		super(context, R.layout.timeline_tweets_list_item, tweets);
		outMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(outMetrics);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);
		long userId = tweet.getUser().getUserId();

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.timeline_tweets_list_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.ivRetweetedIcon = (ImageView) convertView.findViewById(R.id.ivRetweetedIcon);
			viewHolder.ivUserProfilePic = (ImageView) convertView.findViewById(R.id.ivUserProfilePic);
			viewHolder.tvRetweetedText = (TextView) convertView.findViewById(R.id.tvRetweetedText);
			viewHolder.tvUserScreenName = (TextView) convertView.findViewById(R.id.tvUserScreenName);
			viewHolder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			viewHolder.tvTweetText = (TextView) convertView.findViewById(R.id.tvTweetText);
			viewHolder.ivReplyIcon = (ImageView) convertView.findViewById(R.id.ivReplyIcon);
			viewHolder.tvRetweetedCount = (TextView) convertView.findViewById(R.id.tvRetweetedCount);
			viewHolder.tvFollowersCount = (TextView) convertView.findViewById(R.id.tvFollowersCount);

			viewHolder.ivUserProfilePic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(TimelineTweetListItemAdapter.this.getContext(), UserProfileActivity.class);
					i.putExtra(TimelineActivity.USER_ID_EXTRA_TAG, (Long) v.getTag());
					i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					TimelineTweetListItemAdapter.this.getContext().startActivity(i);
				}
			});

			viewHolder.ivReplyIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogFragment newFragment = NewTweetFragment.newInstance((String) v.getTag());
					newFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "onNewTweetDialog");
				}
			});

			convertView.setTag(viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		if (position == 0 && getCount() == 1) {
			convertView.setBackgroundResource(R.drawable.timeline_tweet_background_single_item);
		} else if (position == getCount() - 1) {
			convertView.setBackgroundResource(R.drawable.timeline_tweet_background_last_item);
		} else if (position == 0) {
			convertView.setBackgroundResource(R.drawable.timeline_tweet_background_first_item);
		} else {
			convertView.setBackgroundResource(R.drawable.timeline_tweet_background);
		}

		if (tweet.getUser() != null) {
			viewHolder.tvUserName.setText(tweet.getUser().getUserName());
			viewHolder.tvUserScreenName.setText("@" + tweet.getUser().getUserScreenName());

			viewHolder.ivUserProfilePic.setImageResource(0);
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(tweet.getUser().getUserProfilePicUrl(), viewHolder.ivUserProfilePic);
		}

		viewHolder.tvTweetText.setText(tweet.getText());
		viewHolder.tvTimeStamp.setText(ElapsedTimeFormatter.getElapsedTimeString(new Date(tweet.getCreatedAt())));
		viewHolder.tvRetweetedCount.setText(String.valueOf(tweet.getRetweetCount()));
		viewHolder.tvFollowersCount.setText(String.valueOf(tweet.getFavoritesCount()));

		viewHolder.ivUserProfilePic.setTag(userId);
		viewHolder.ivReplyIcon.setTag("@" + tweet.getUser().getUserScreenName());

		return convertView;
	}

	public void insertAll(List<Tweet> tweets) {
		if (tweets.size() > 0) {
			if (getCount() > 0) {
				for (int i = tweets.size() - 1; i >= 0; i--) {
					insert(tweets.get(i), 0);
				}
			} else {
				addAll(tweets);
			}
		}
		notifyDataSetChanged();
	}

	public void insertAll(Tweet tweet) {
		if (tweet != null) {
			insert(tweet, 0);
		}
		notifyDataSetChanged();
	}

}

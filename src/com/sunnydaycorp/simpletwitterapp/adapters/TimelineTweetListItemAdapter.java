package com.sunnydaycorp.simpletwitterapp.adapters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;

public class TimelineTweetListItemAdapter extends ArrayAdapter<Tweet> {

	private DisplayMetrics outMetrics;

	private static class ViewHolder {
		ImageView ivRetweetedIcon;
		ImageView ivUserProfilePic;
		TextView tvRetweetedText;
		TextView tvUserName;
		TextView tvTimeStamp;
		TextView tvUserScreenName;
		TextView tvTweetText;
	}

	public TimelineTweetListItemAdapter(Context context, List<Tweet> tweets) {
		super(context, R.layout.timeline_tweets_list_item, tweets);
		outMetrics = new DisplayMetrics();
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(outMetrics);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);

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
			convertView.setTag(viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		if (position == 0) {
			convertView.setBackgroundResource(R.drawable.timeline_tweet_background_first_item);
		} else {
			convertView.setBackgroundResource(R.drawable.timeline_tweet_background);
		}

		viewHolder.ivRetweetedIcon.setVisibility(View.GONE);
		viewHolder.tvRetweetedText.setVisibility(View.GONE);
		viewHolder.ivUserProfilePic.setImageResource(0);
		if (tweet.getUser() != null) {
			viewHolder.tvUserName.setText(tweet.getUser().getUserName());
			viewHolder.tvUserScreenName.setText("@" + tweet.getUser().getUserScreenName());
			// reset image from recycled view
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(tweet.getUser().getUserProfilePicUrl(), viewHolder.ivUserProfilePic);
		}
		viewHolder.tvTweetText.setText(tweet.getText());
		viewHolder.tvTimeStamp.setText(getTimeString(new Date(tweet.getCreatedAt())));

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

	public static String getTimeString(Date fromdate) {

		long then;
		then = fromdate.getTime();
		Date date = new Date(then);

		StringBuffer dateStr = new StringBuffer();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar now = Calendar.getInstance();

		int days = daysBetween(calendar.getTime(), now.getTime());
		int minutes = hoursBetween(calendar.getTime(), now.getTime());
		int hours = minutes / 60;
		if (days == 0) {
			int second = minuteBetween(calendar.getTime(), now.getTime());
			if (minutes > 60) {
				if (hours >= 1 && hours <= 24) {
					dateStr.append(hours).append("h");
				}
			} else {
				if (second <= 10) {
					dateStr.append("now");
				} else if (second > 10 && second <= 30) {
					dateStr.append("few sec");
				} else if (second > 30 && second <= 60) {
					dateStr.append(second).append("s");
				} else if (second >= 60 && minutes <= 60) {
					dateStr.append(minutes).append("m");
				}
			}
		} else if (hours > 24 && days <= 7) {
			dateStr.append(days).append("d");
		}
		return dateStr.toString();
	}

	public static int minuteBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
	}

	public static int hoursBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
	}

	public static int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
	}

}

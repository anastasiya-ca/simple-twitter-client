package com.sunnydaycorp.simpletwitterapp.networking;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.activities.TimelineActivity;
import com.sunnydaycorp.simpletwitterapp.activities.TimelineActivity.TwitterAPIReqCode;
import com.sunnydaycorp.simpletwitterapp.interfaces.TwitterClientListener;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;

public class TwitterRestClient extends OAuthBaseClient {

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "oCT6iMymYQfaYD0DKyZzbsQA1";
	public static final String REST_CONSUMER_SECRET = "JZ9ErEYK4qPLn95HUEQTsNZTq4RPwbNzGN30dvYCl2yf4Gkdpr";
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets";
	public static final String HOME_TIMELINE_PATH = "statuses/home_timeline.json";
	public static final String POST_NEW_TWEET_PATH = "statuses/update.json";
	public static final String VERIFY_CREDENTIALS_PATH = "account/verify_credentials.json";

	public static final String LOG_TAG_CLASS = TwitterRestClient.class.getSimpleName();
	public static final String REQUEST_PARAM_SINCE_ID = "since_id";
	public static final String REQUEST_PARAM_MAX_ID = "max_id";
	public static final String REQUEST_PARAM_STATUS = "status";

	public enum ResultCode {
		JSON_PARSING_EXCEPTION, FAILED_REQUEST, NO_INTERNET, EXCEEDED_QPS
	};

	public TwitterRestClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void fetchHomeTimelineTweets(final TwitterAPIReqCode requestCode, TwitterClientListener twitterClientListener, long sinceId, long maxId) {
		final TwitterClientListener listener = twitterClientListener;
		if (!checkForNetworkAvailability()) {
			Log.w(LOG_TAG_CLASS, "No Internet connection");
			onError(ResultCode.NO_INTERNET, listener);
		} else {
			String apiUrl = getApiUrl(HOME_TIMELINE_PATH);
			RequestParams params = new RequestParams();
			if (sinceId > 0 && requestCode == TimelineActivity.TwitterAPIReqCode.REFRESH_LOAD) {
				params.put(REQUEST_PARAM_SINCE_ID, String.valueOf(sinceId));
			}
			if (maxId > 0 && requestCode == TimelineActivity.TwitterAPIReqCode.MORE_OLD_LOAD) {
				params.put(REQUEST_PARAM_MAX_ID, String.valueOf(maxId));
			}
			Log.i(LOG_TAG_CLASS, "Sending request to Twitter API " + requestCode.toString() + " params " + params.toString());
			if (requestCode == TimelineActivity.TwitterAPIReqCode.NEW_LOAD) {
				params = null;
			}
			client.get(apiUrl, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, JSONArray response) {
					Log.i(LOG_TAG_CLASS, "Received success response from Twitter API: " + response.toString());
					List<Tweet> tweets = new ArrayList<Tweet>();
					try {
						tweets = Tweet.fromJSONArray(response);
						if (tweets != null && tweets.size() > 0) {
							refreshDataCached(requestCode, tweets);
						}
						if (listener != null) {
							listener.onHomeTimelineFetched(requestCode, tweets);
						}
					} catch (JSONException e) {
						Log.e(LOG_TAG_CLASS, "Error processing JSON response from Twitter", e);
						onError(ResultCode.JSON_PARSING_EXCEPTION, listener);
					}
				}

				private void refreshDataCached(TwitterAPIReqCode requestCode, List<Tweet> tweets) {
					if (requestCode == TimelineActivity.TwitterAPIReqCode.NEW_LOAD)
					// clear DB
					{
						TwitterUser.deleteAll();
						Tweet.deleteAll();
					}
					// save fresh data to DB
					for (Tweet tweet : tweets) {
						tweet.getUser().save();
						TwitterUser user = TwitterUser.byRemoteId(tweet.getUser().getUserId());
						Log.d("DEBUG", "User has been saved with " + user.getUserId() + " and DB id " + user.getId());
						tweet.setUser(user);
						tweet.save();
						Log.d("DEBUG", "Tweet has been saved with " + tweet.getTweetId() + " and DB id " + tweet.getId() + " and user id "
								+ tweet.getUser().getId());

					}
					Log.d("DEBUG", "On save there are " + TwitterUser.recordCount() + " users in DB");
					Log.d("DEBUG", "On save there are " + Tweet.recordCount() + " tweets in DB");

				}

				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					Log.i(LOG_TAG_CLASS, "Received JSONObject success response from Twitter API: " + response.toString());
				}

				@Override
				public void onFailure(Throwable throwable, JSONObject errorResponse) {
					Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
					onError(ResultCode.FAILED_REQUEST, listener);
				}

				@Override
				public void onFailure(Throwable throwable, JSONArray errorResponse) {
					Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
					onError(ResultCode.FAILED_REQUEST, listener);
				}

			});
		}

	}

	public void postNewTweet(TwitterClientListener twitterClientListener, String text) {
		final TwitterClientListener listener = twitterClientListener;

		String apiUrl = getApiUrl(POST_NEW_TWEET_PATH);
		RequestParams params = new RequestParams();

		if (text != null) {
			params.put(REQUEST_PARAM_STATUS, text);
			Log.i(LOG_TAG_CLASS, "Sending request to post new tweet to Twitter API " + " params " + params.toString());
			client.post(apiUrl, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, JSONArray response) {
					Log.e(LOG_TAG_CLASS, "Received success response from Twitter API: " + response.toString());
					// unexpected response
					onError(ResultCode.JSON_PARSING_EXCEPTION, listener);
				}

				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					Log.i(LOG_TAG_CLASS, "Received JSONObject success response from Twitter API: " + response.toString());
					try {
						Tweet tweet = Tweet.fromJSON(response);
						if (tweet != null) {
							refreshDataCached(tweet);
						}
						if (listener != null) {
							listener.onNewTweetPosted(tweet);
						}
					} catch (JSONException e) {
						Log.e(LOG_TAG_CLASS, "Error processing JSON response from Twitter", e);
						onError(ResultCode.JSON_PARSING_EXCEPTION, listener);
					}
				}

				@Override
				public void onFailure(Throwable throwable, JSONObject errorResponse) {
					Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
					onError(ResultCode.FAILED_REQUEST, listener);
				}

				@Override
				public void onFailure(Throwable throwable, JSONArray errorResponse) {
					Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
					onError(ResultCode.FAILED_REQUEST, listener);
				}

				private void refreshDataCached(Tweet tweet) {
					// save new tweet data to DB
					tweet.getUser().save();
					TwitterUser user = TwitterUser.byRemoteId(tweet.getUser().getUserId());
					Log.d("DEBUG", "User has been saved with " + user.getUserId() + " and DB id " + user.getId());
					tweet.setUser(user);
					tweet.save();
					Log.d("DEBUG", "Tweet has been saved with " + tweet.getTweetId() + " and DB id " + tweet.getId() + " and user id "
							+ tweet.getUser().getId());
					Log.d("DEBUG", "On save there are " + TwitterUser.recordCount() + " users in DB");
					Log.d("DEBUG", "On save there are " + Tweet.recordCount() + " tweets in DB");

				}

			});
		}

	}

	public void verifyAndGetUserCredentials(TwitterClientListener twitterClientListener) {
		final TwitterClientListener listener = twitterClientListener;

		String apiUrl = getApiUrl(VERIFY_CREDENTIALS_PATH);
		RequestParams params = null;
		client.get(apiUrl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONArray response) {
				Log.i(LOG_TAG_CLASS, "Received success response from Twitter API: " + response.toString());
				if (listener != null) {
					// listener.onUserCredentialsVerified(user);
				}
			}

			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				Log.i(LOG_TAG_CLASS, "Received JSONObject success response from Twitter API: " + response.toString());
				try {
					TwitterUser user = TwitterUser.fromJSON(response);
					if (listener != null) {

						listener.onUserCredentialsVerified(user);
					}
				} catch (JSONException e) {
					Log.e(LOG_TAG_CLASS, "Error processing JSON response from Twitter", e);
					onError(ResultCode.JSON_PARSING_EXCEPTION, listener);
				}
			}

			@Override
			public void onFailure(Throwable throwable, JSONObject errorResponse) {
				Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
				onError(ResultCode.FAILED_REQUEST, listener);
			}

			@Override
			public void onFailure(Throwable throwable, JSONArray errorResponse) {
				Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
				onError(ResultCode.FAILED_REQUEST, listener);
			}

		});

	}

	private boolean checkForNetworkAvailability() {
		// check for Internet connection up to 3 times
		for (int i = 0; i < 3; i++) {
			if (((SimpleTwitterApp) context.getApplicationContext()).hasInternetConnection()) {
				break;
			} else if (i == 2) {
				return false;
			}
		}
		return true;

	}

	private void onError(ResultCode resultCode, TwitterClientListener listener) {
		if (listener != null) {
			listener.onError(resultCode);
		}
	}

}
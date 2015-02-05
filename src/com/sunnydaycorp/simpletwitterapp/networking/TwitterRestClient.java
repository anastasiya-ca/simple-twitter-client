package com.sunnydaycorp.simpletwitterapp.networking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.listeners.LoggedUserInfoResponseListener;
import com.sunnydaycorp.simpletwitterapp.listeners.TimelineResponseListener;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;
import com.sunnydaycorp.simpletwitterapp.models.Tweet;
import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;

public class TwitterRestClient extends OAuthBaseClient {

	public static final String LOG_TAG_CLASS = TwitterRestClient.class.getSimpleName();
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;

	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "oCT6iMymYQfaYD0DKyZzbsQA1";
	public static final String REST_CONSUMER_SECRET = "JZ9ErEYK4qPLn95HUEQTsNZTq4RPwbNzGN30dvYCl2yf4Gkdpr";
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets";

	public static final String HOME_TIMELINE_PATH = "statuses/home_timeline.json";
	public static final String MENTIONS_TIMELINE_PATH = "statuses/mentions_timeline.json";
	public static final String USER_TIMELINE_PATH = "statuses/user_timeline.json";
	public static final String POST_NEW_TWEET_PATH = "statuses/update.json";
	public static final String VERIFY_CREDENTIALS_PATH = "account/verify_credentials.json";
	public static final String SEARCH_TWEETS_PATH = "search/tweets.json";

	public static final String REQUEST_PARAM_USER_ID = "user_id";
	public static final String REQUEST_PARAM_SINCE_ID = "since_id";
	public static final String REQUEST_PARAM_MAX_ID = "max_id";
	public static final String REQUEST_PARAM_STATUS = "status";

	public enum ResultCode implements Serializable {
		OK, JSON_PARSING_EXCEPTION, FAILED_REQUEST, NO_INTERNET, EXCEEDED_QPS
	};

	public enum TwitterAPIReqCode {
		REFRESH_LOAD, MORE_OLD_LOAD, NEW_LOAD
	};

	public TwitterRestClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	private interface TwitterRequestListener {
		public void onSuccess(JSONArray response);

		public void onSuccess(JSONObject response);

		public void onError(ResultCode resultCode);
	}

	private void makeGetRequest(String apiPath, RequestParams params, final TwitterRequestListener requestListener) {
		if (!checkForNetworkAvailability()) {
			Log.e(LOG_TAG_CLASS, "No Internet connection");
			requestListener.onError(ResultCode.NO_INTERNET);
		} else {
			String apiUrl = getApiUrl(apiPath);

			Log.d(LOG_TAG_CLASS, "Sending request to Twitter API URL:" + apiUrl);
			Log.d(LOG_TAG_CLASS, "Sending request to Twitter API with params " + (params != null ? params.toString() : null));

			client.get(apiUrl, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, JSONArray response) {
					Log.d(LOG_TAG_CLASS, "Received JSONArray success response from Twitter API: " + response.toString());
					requestListener.onSuccess(response);
				}

				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					Log.d(LOG_TAG_CLASS, "Received JSONObject success response from Twitter API: " + response.toString());
					requestListener.onSuccess(response);
				}

				@Override
				public void onFailure(Throwable throwable, JSONObject errorResponse) {
					Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
					if (errorResponse != null) {
						try {
							JSONArray errors = errorResponse.getJSONArray("errors");
							if (errors.getJSONObject(0).optInt("code") == 88) {
								requestListener.onError(ResultCode.EXCEEDED_QPS);
								return;
							}

						} catch (JSONException e) {
							Log.e(LOG_TAG_CLASS, errorResponse.toString(), e);
						}

					}
					requestListener.onError(ResultCode.FAILED_REQUEST);
				}

				@Override
				public void onFailure(Throwable throwable, JSONArray errorResponse) {
					Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
					requestListener.onError(ResultCode.FAILED_REQUEST);
				}

			});
		}
	}

	private RequestParams getTimelineRequestParams(TwitterAPIReqCode requestCode, long sinceId, long maxId, Long userId) {
		RequestParams params = null;
		if (requestCode != TwitterAPIReqCode.NEW_LOAD) {
			params = new RequestParams();
			if (sinceId > 0 && requestCode == TwitterAPIReqCode.REFRESH_LOAD) {
				params.put(REQUEST_PARAM_SINCE_ID, String.valueOf(sinceId));
			}
			if (maxId > 0 && requestCode == TwitterAPIReqCode.MORE_OLD_LOAD) {
				params.put(REQUEST_PARAM_MAX_ID, String.valueOf(maxId));
			}
		}
		if (userId != null && userId > 0) {
			if (params == null) {
				params = new RequestParams();
			}
			params.put(REQUEST_PARAM_USER_ID, String.valueOf(userId));
		}
		return params;
	}

	public void verifyAndGetUserCredentials(final LoggedUserInfoResponseListener listener) {
		RequestParams params = null;
		makeGetRequest(VERIFY_CREDENTIALS_PATH, params, new TwitterRequestListener() {
			@Override
			public void onSuccess(JSONObject response) {
				Log.i(LOG_TAG_CLASS, "Received JSONObject success response from Twitter API: " + response.toString());
				try {
					TwitterUser user = TwitterUser.fromJSON(response);
					SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) context.getApplicationContext()).getSharedLoggedUserDetails();
					loggedUserDetails.savePreferences(user.getUserId(), user.getUserName(), user.getUserScreenName(), user.getUserProfilePicUrl(),
							user.getUserProfileBackgroundPicUrl(), user.getTweetsCount(), user.getFollowersCount(), user.getFollowingCount(),
							user.getUserTagline());
					listener.onSuccess();
				} catch (JSONException e) {
					Log.e(LOG_TAG_CLASS, "Error processing User Credentials JSON response from Twitter", e);
					listener.onErrorResult(ResultCode.JSON_PARSING_EXCEPTION);
				}
			}

			@Override
			public void onSuccess(JSONArray response) {
				Log.e(LOG_TAG_CLASS, "Received unexpected JSONArray success response from Twitter API: " + response.toString());
				listener.onErrorResult(ResultCode.FAILED_REQUEST);
			}

			@Override
			public void onError(ResultCode resultCode) {
				listener.onErrorResult(resultCode);
			}
		});

	}

	private void fetchTimelineTweets(String apiPath, final TwitterAPIReqCode requestCode, final TimelineResponseListener listener, long sinceId,
			long maxId, Long userId, final boolean isMentionsTimeline) {
		RequestParams params = getTimelineRequestParams(requestCode, sinceId, maxId, userId);
		makeGetRequest(apiPath, params, new TwitterRequestListener() {
			@Override
			public void onSuccess(JSONObject response) {
				Log.i(LOG_TAG_CLASS, "Received unexpected JSONObject success response from Twitter API: " + response.toString());
				listener.onErrorResult(ResultCode.FAILED_REQUEST);
			}

			@Override
			public void onSuccess(JSONArray response) {
				List<Tweet> tweets = new ArrayList<Tweet>();
				try {
					tweets = Tweet.fromJSONArray(response, isMentionsTimeline);
					listener.onTimelineFetched(requestCode, tweets);
				} catch (JSONException e) {
					Log.e(LOG_TAG_CLASS, "Error processing JSON response from Twitter", e);
					listener.onErrorResult(ResultCode.JSON_PARSING_EXCEPTION);
				}
			}

			@Override
			public void onError(ResultCode resultCode) {
				listener.onErrorResult(resultCode);
			}
		});
	}

	public void fetchHomeTimelineTweets(TwitterAPIReqCode requestCode, TimelineResponseListener listener, long sinceId, long maxId) {
		fetchTimelineTweets(HOME_TIMELINE_PATH, requestCode, listener, sinceId, maxId, null, false);
	}

	public void fetchMentionsTimelineTweets(TwitterAPIReqCode requestCode, TimelineResponseListener listener, long sinceId, long maxId) {
		fetchTimelineTweets(MENTIONS_TIMELINE_PATH, requestCode, listener, sinceId, maxId, null, true);
	}

	public void fetchUserTimelineTweets(TwitterAPIReqCode requestCode, TimelineResponseListener listener, long sinceId, long maxId, long userId) {
		fetchTimelineTweets(USER_TIMELINE_PATH, requestCode, listener, sinceId, maxId, userId, false);
	}

	public void postNewTweet(String text) {
		if (!checkForNetworkAvailability()) {
			Log.e(LOG_TAG_CLASS, "No Internet connection");
			sendNewTweetPostBroadcast(ResultCode.NO_INTERNET);
		} else {
			String apiUrl = getApiUrl(POST_NEW_TWEET_PATH);
			RequestParams params = new RequestParams();
			if (text != null) {
				params.put(REQUEST_PARAM_STATUS, text);
				Log.i(LOG_TAG_CLASS, "Sending request to post new tweet to Twitter API " + " params " + params.toString());

				client.post(apiUrl, params, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, JSONArray response) {
						Log.e(LOG_TAG_CLASS, "Received unexpected success response from Twitter API: " + response.toString());
						sendNewTweetPostBroadcast(ResultCode.FAILED_REQUEST);
					}

					@Override
					public void onSuccess(int statusCode, JSONObject response) {
						Log.i(LOG_TAG_CLASS, "Received JSONObject success response from Twitter API: " + response.toString());
						try {
							Tweet tweet = Tweet.fromJSON(response, false);
							if (tweet != null) {
								sendNewTweetPostBroadcast(ResultCode.OK);
							}
						} catch (JSONException e) {
							Log.e(LOG_TAG_CLASS, "Error processing JSON response from Twitter", e);
							sendNewTweetPostBroadcast(ResultCode.JSON_PARSING_EXCEPTION);
						}
					}

					@Override
					public void onFailure(Throwable throwable, JSONObject errorResponse) {
						Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
						sendNewTweetPostBroadcast(ResultCode.FAILED_REQUEST);
					}

					@Override
					public void onFailure(Throwable throwable, JSONArray errorResponse) {
						Log.e(LOG_TAG_CLASS, errorResponse.toString(), throwable);
						sendNewTweetPostBroadcast(ResultCode.FAILED_REQUEST);
					}

				});
			}
		}

	}

	private void sendNewTweetPostBroadcast(ResultCode resultCode) {
		Intent intent = new Intent("new-tweet-posted");
		intent.putExtra("RESULT_CODE", resultCode);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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

}
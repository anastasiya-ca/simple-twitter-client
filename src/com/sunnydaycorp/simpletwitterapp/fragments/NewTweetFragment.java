package com.sunnydaycorp.simpletwitterapp.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunnydaycorp.simpletwitterapp.R;
import com.sunnydaycorp.simpletwitterapp.SimpleTwitterApp;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;
import com.sunnydaycorp.simpletwitterapp.networking.TwitterRestClient;

public class NewTweetFragment extends DialogFragment {

	public final static String REPLY_TO_EXTRA_TAG = "REPLY_TO_USER_NAME";
	public final static int MAXIMUM_CHAR_NUMBER = 140;

	private Button btnCancelNewTweet;
	private Button btnTweetNewTweet;
	private TextView tvNewTweetCharsNumber;
	private TextView tvNewTweetUserScreenName;
	private ImageView ivNewTweetUserProfilePic;
	private EditText etNewTweetText;

	private TwitterRestClient twitterRestClient;
	private String replyToUserName;

	public NewTweetFragment() {
	}

	public static NewTweetFragment newInstance(String replyToUserName) {
		NewTweetFragment newTweetFragment = new NewTweetFragment();
		Bundle args = new Bundle();
		args.putString(REPLY_TO_EXTRA_TAG, replyToUserName);
		newTweetFragment.setArguments(args);
		return newTweetFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		replyToUserName = getArguments().getString(REPLY_TO_EXTRA_TAG);
		twitterRestClient = ((SimpleTwitterApp) getActivity().getApplicationContext()).getRestClient();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_new_tweet, container);

		btnCancelNewTweet = (Button) view.findViewById(R.id.btnCancelNewTweet);
		btnTweetNewTweet = (Button) view.findViewById(R.id.btnTweetNewTweet);
		tvNewTweetCharsNumber = (TextView) view.findViewById(R.id.tvNewTweetCharNumber);
		ivNewTweetUserProfilePic = (ImageView) view.findViewById(R.id.ivNewTweetUserProfilePic);
		tvNewTweetUserScreenName = (TextView) view.findViewById(R.id.tvNewTweetUserScreenName);
		etNewTweetText = (EditText) view.findViewById(R.id.etNewTweetText);
		setupFields();

		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return view;
	}

	private void setupFields() {
		SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) getActivity().getApplicationContext()).getSharedLoggedUserDetails();
		tvNewTweetUserScreenName.setText("@" + loggedUserDetails.getUserScreenName());
		if (!loggedUserDetails.getUserProfilePicUrl().isEmpty()) {
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(loggedUserDetails.getUserProfilePicUrl(), ivNewTweetUserProfilePic);
		}

		if (replyToUserName != null && !replyToUserName.isEmpty()) {
			etNewTweetText.setText(replyToUserName + " ");
			etNewTweetText.setSelection(getNewTweetCharsCount());
		}
		etNewTweetText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setNewTweetCharsNumber();
				activateTweetNewTweetButton();
			}
		});
		setNewTweetCharsNumber();
		activateTweetNewTweetButton();
		etNewTweetText.requestFocus();

		btnCancelNewTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelNewTweet(v);
			}
		});
		btnTweetNewTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				postNewTweet(v);
			}
		});
	}

	private int getNewTweetCharsLeftCount() {
		return MAXIMUM_CHAR_NUMBER - getNewTweetCharsCount();
	}

	private int getNewTweetCharsCount() {
		return etNewTweetText.getText().toString().length();
	}

	private void setNewTweetCharsNumber() {
		tvNewTweetCharsNumber.setText(String.valueOf(getNewTweetCharsLeftCount()));
	}

	private void activateTweetNewTweetButton() {
		if (getNewTweetCharsCount() > 0) {
			if (btnTweetNewTweet != null) {
				btnTweetNewTweet.setEnabled(true);
			}
		} else {
			if (btnTweetNewTweet != null) {
				btnTweetNewTweet.setEnabled(false);
			}
		}
	}

	public void cancelNewTweet(View view) {
		closeInputFromWindow();
		getDialog().dismiss();
	}

	public void postNewTweet(View view) {
		twitterRestClient.postNewTweet(etNewTweetText.getText().toString());
		getDialog().dismiss();
	}

	private void closeInputFromWindow() {
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

}

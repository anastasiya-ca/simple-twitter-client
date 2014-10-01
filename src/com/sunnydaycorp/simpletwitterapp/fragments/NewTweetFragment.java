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
import com.sunnydaycorp.simpletwitterapp.interfaces.OnNewTweetCreatedListener;
import com.sunnydaycorp.simpletwitterapp.models.SharedLoggedUserDetails;

public class NewTweetFragment extends DialogFragment {

	private OnNewTweetCreatedListener onNewTweetCreatedListener;

	private Button btnCancelNewTweet;
	private Button btnTweetNewTweet;
	private TextView tvNewTweetCharNumber;
	private ImageView ivNewTweetUserProfilePic;
	private TextView tvNewTweetUserScreenName;
	private EditText etNewTweetText;

	public NewTweetFragment() {
	}

	public NewTweetFragment(OnNewTweetCreatedListener onNewTweetCreatedListener) {
		this.onNewTweetCreatedListener = onNewTweetCreatedListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		SharedLoggedUserDetails loggedUserDetails = ((SimpleTwitterApp) getActivity().getApplicationContext()).getSharedLoggedUserDetails();

		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_new_tweet, container);
		btnCancelNewTweet = (Button) view.findViewById(R.id.btnCancelNewTweet);
		btnTweetNewTweet = (Button) view.findViewById(R.id.btnTweetNewTweet);
		tvNewTweetCharNumber = (TextView) view.findViewById(R.id.tvNewTweetCharNumber);
		ivNewTweetUserProfilePic = (ImageView) view.findViewById(R.id.ivNewTweetUserProfilePic);
		tvNewTweetUserScreenName = (TextView) view.findViewById(R.id.tvNewTweetUserScreenName);
		etNewTweetText = (EditText) view.findViewById(R.id.etNewTweetText);

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

		btnTweetNewTweet.setEnabled(false);
		etNewTweetText.requestFocus();
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		etNewTweetText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int charCount = etNewTweetText.getText().toString().trim().length();
				tvNewTweetCharNumber.setText(String.valueOf(140 - charCount));
				if (charCount > 0) {
					if (btnTweetNewTweet != null) {
						btnTweetNewTweet.setEnabled(true);
					}
				} else {
					if (btnTweetNewTweet != null) {
						btnTweetNewTweet.setEnabled(false);
					}
				}
			}
		});

		tvNewTweetUserScreenName.setText("@" + loggedUserDetails.getUserScreenName());
		if (!loggedUserDetails.getUserProfilePicUrl().isEmpty()) {
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(loggedUserDetails.getUserProfilePicUrl(), ivNewTweetUserProfilePic);
		}
		return view;
	}

	public void cancelNewTweet(View view) {
		closeInputFromWindow();
		getDialog().dismiss();

	}

	public void postNewTweet(View view) {
		if (onNewTweetCreatedListener != null) {
			onNewTweetCreatedListener.onNewTweetCreated(this.getDialog(), etNewTweetText.getText().toString().trim());
		}
		getDialog().dismiss();
	}

	private void closeInputFromWindow() {
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

}

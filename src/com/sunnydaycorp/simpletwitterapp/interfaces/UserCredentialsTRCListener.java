package com.sunnydaycorp.simpletwitterapp.interfaces;

import com.sunnydaycorp.simpletwitterapp.models.TwitterUser;

// not used
public interface UserCredentialsTRCListener extends TwitterClientListener {

	public void onUserCredentialsVerified(TwitterUser user);

}

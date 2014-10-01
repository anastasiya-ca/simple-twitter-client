Simple Twitter Client - android app
================
Simple Twitter Client is a basic android app that allows a user to own Twitter timeline, as well as compose and post a new tweet. The app utilizes [Twitter REST API] (https://dev.twitter.com/rest/public). 
The following **required** functionality is completed:
* [x]	User can **sign in to Twitter** using pOAuth login. Existing Twitter account credentials are required.
* [x]	User can **view tweets from their home timeline**. Each tweet has username, screen name, profile picture, body and relative timestamp displayed.
* [x]	User can **scroll down to see more older tweets**. Number of tweets is unlimited. However there are [Twiiter Api Rate Limits] (https://dev.twitter.com/rest/public/rate-limiting) in place.
* [x]	User can **compose and post a new tweet**. Compose icon is in the right corner of action bar. Posting empty tweet is not allowed. Once button “Tweet” is clicked the tweet is posted to twitter and is inserted to the top of the timeline stream. Please note that this implementation can result in losing some tweets (tweets that were created between the last post on your timeline and new tweet posted by you).
The following **optional** features are implemented:
* [x]	User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While “live data” is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
* [x]	User can **pull down to refresh tweets timeline **
* [x]	Compose tweet functionality is build using modal overlay.
* [x]	User can **see a counter with total number of characters left for tweet ** on compose tweet page.
* [x]	User can **open a detailed tweet view**
* [x]	User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [x]	Improved **error handing with relevant messaging** and logging on internet not available and error response from API.
* [x]	Improved **look and fill and user experience** (opening/hiding soft keyboard, loading spinner, focus, empty new tweet validation etc)


The app was tested on HTC One (Android 4.1.2) and on AVDs.

Walkthrough of implemented user stories:


![Video Walkthrough](simple_twitter_client_app_demo.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).


The following open-source libraries were used for the project:
-	[scribe-java ] (https://github.com/fernandezpablo85/scribe-java) - Simple OAuth library for handling the authentication flow.
-	[Android Async HTTP] (https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
-	[codepath-oauth] (https://github.com/thecodepath/android-oauth-handler) - Custom-built library for managing OAuth authentication and signing of requests
-	[UniversalImageLoader] (https://github.com/nostra13/Android-Universal-Image-Loader) - Used for async image loading and caching them in memory and on disk.
-	[ActiveAndroid ] (https://github.com/pardom/ActiveAndroid) - Simple ORM for persisting a local SQLite database on the Android device

Points to consider for future development:
-	Make more tweet data available such as media, retweet and favorites count etc.
-	Make more actions available on native Tweeter e.g. compose tweet to respond to a tweet, re-tweet, delete tweet etc.
-	Improve look an fill and make a consistent theme across the app

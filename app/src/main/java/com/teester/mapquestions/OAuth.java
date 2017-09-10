package com.teester.mapquestions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Browser;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class OAuth extends AsyncTask<Void, Void, Void> {

	private static final String TAG = OAuth.class.getSimpleName();
	private Context context;

	private static final String CONSUMER_KEY = "1LJqwD4kMz96HTbv9I1U1XBM0AL1RpcjuFOPvW0B";
	private static final String CONSUMER_SECRET = "KDCLveu82AZawLELpC6yIP3EI8fJa0JqF0ALukbl";

	private static final String REQUEST_TOKEN_ENDPOINT_URL = "https://www.openstreetmap.org/oauth/request_token";
	private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://www.openstreetmap.org/oauth/access_token";
	private static final String AUTHORIZE_WEBSITE_URL = "https://www.openstreetmap.org/oauth/authorize";
	private static final String CALLBACK_URL = "mapquestions://oauth";

	public OAuth(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... voids) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();

		String verifier = prefs.getString("oauth_verifier", "");
		String token = prefs.getString("oauth_token", "");
		String tokenSecret = prefs.getString("oauth_token_secret", "");

		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		OAuthProvider provider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL, AUTHORIZE_WEBSITE_URL);

		try {
			if (verifier == "") {
				String url = provider.retrieveRequestToken(consumer, CALLBACK_URL);

				editor.putString("oauth_token_secret", consumer.getTokenSecret());
				editor.putString("oauth_token", consumer.getToken());
				editor.apply();

				//Open the url in an external browser
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				browserIntent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
				browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(browserIntent);
			} else {
				consumer.setTokenWithSecret(token, tokenSecret);
				provider.retrieveAccessToken(consumer, verifier);

				editor.putString("oauth_token_secret", consumer.getTokenSecret());
				editor.putString("oauth_token", consumer.getToken());
				editor.putBoolean("logged_in_to_osm", true);
				editor.apply();
			}
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return null;
	}
}

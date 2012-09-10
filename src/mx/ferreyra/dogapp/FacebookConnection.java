package mx.ferreyra.dogapp;

import mx.ferreyra.dogapp.SessionEvents.AuthListener;
import mx.ferreyra.dogapp.SessionEvents.LogoutListener;
import android.app.Activity;
import android.os.Bundle;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

/*
 * Facebook for login
 */
public class FacebookConnection {

	private Activity activity;
	public Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	
	/*
	 * Activity for Facebook to execute
	 */
	public FacebookConnection(Activity activity) {
		activity = (RegisterScreen)activity;
	}

	/*
	 * Validates login
	 */
	public void fbLogin() {
		mx.ferreyra.dogapp.DogUtil ac = (mx.ferreyra.dogapp.DogUtil) activity.getApplication();
		ac.initFacebook();  

		mFacebook = ac.getFacebook();
		mAsyncRunner = ac.getAsyncFacebookRunner();

		SessionStore.restore(mFacebook, activity);
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());

		mFacebook.authorize(activity, ac.getPermissions(),
				new UserInfoListener()); 

	}

	/*
	 * Listens login for Facebook 
	 */
	public class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {


		}

		public void onAuthFail(String error) {

		}
	}

	/*
	 * Listens logout for Facebook
	 */
	public class SampleLogoutListener implements LogoutListener {
		public void onLogoutBegin() {

		}

		public void onLogoutFinish() {

		}
	}


	/*
	 * Request Graph API to render user profile
	 */
	public class UserInfoListener implements DialogListener {
		public void onComplete(Bundle values) {
			try{
				mAsyncRunner.request("me", new FacebookUserRequestListener());
			}
			catch (Exception e) {

			}

		}

		public void onCancel() {
		}

		public void onError(DialogError e) {
			e.printStackTrace();
		}

		public void onFacebookError(FacebookError e) {
			e.printStackTrace();
		}
	}

	/*
	 * Send user profile to middleware
	 */
	public class FacebookUserRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {	
			((RegisterScreen)activity).postServer(response);

		}
	}



}

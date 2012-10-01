package mx.ferreyra.dogapp;

import static mx.ferreyra.dogapp.ui.DialogHelper.ONLY_DISMISS;
import static mx.ferreyra.dogapp.ui.DialogHelper.showOkDialog;

import java.io.IOException;

import mx.ferreyra.dogapp.pojos.FacebookUser;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.gson.Gson;

public class SignupActivity extends Activity {

	private EditText emailValueField;
	private EditText passwordValueField;
	private EditText passwordConfirmationValueField;

	public static final int PASSWORD_MIN_SIZE = 3;

    public static final String IS_FACEBOOK_SIGNUP = "1";
    public static final String NOT_IS_FACEBOOK_SIGNUP = "0";

	private Facebook f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		emailValueField = (EditText)findViewById(R.id.email_value);
		passwordValueField = (EditText)findViewById(R.id.password_value);
		passwordConfirmationValueField = (EditText)findViewById(R.id.password_confirmation_value);
	}

	public void onClickSignup(View view) {
		if(!validateEmail()) {
			// Notify user that email is not valid
		    showOkDialog(this, getString(R.string.type_an_email), ONLY_DISMISS);
		    return;
		} else if(!validatePassword()) {
            // Notify user that password is not valid
            showOkDialog(this, getString(R.string.type_a_password), ONLY_DISMISS);
            return;
        } else if(!validatePasswordConfirmarion()) {
            // Notify user that password confirmation is not valid
            showOkDialog(this, getString(R.string.password_and_confirmation_not_equal), ONLY_DISMISS);
            return;
        }

		Signup task = new Signup(this);
		String[] args = {
				emailValueField.getText().toString(),
				passwordValueField.getText().toString(),
				NOT_IS_FACEBOOK_SIGNUP};
		task.execute(args);
	}

	public void onClickSignupWithFacebook(View view) {
	    //Facebook f = DogUtil.getInstance().getFacebook();
	    f = new Facebook(DogUtil.FACEBOOK_APPID);

	    if(f.isSessionValid()) {
		    // Valid session
	        new GetDataFB(this).run();
	    } else {
	        // Not valid session
	        String[] fbPermissions = new String[] {
	                "email",
	                "read_stream",
	                "publish_stream"};
	        f.authorize(this, fbPermissions,
	                Facebook.FORCE_DIALOG_AUTH,
	                new DialogListenerForFacebookSinup(this));
	    }
	}

	private class GetDataFB implements Runnable {
	    private final Context context;

	    public GetDataFB(Context context) {
	        this.context = context;
	    }

        @Override
        public void run() {
            try {
                //Facebook f = DogUtil.getInstance().getFacebook();

                SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_name), 0);
                String fbToken   = f.getAccessToken();
                long   fbExpires = f.getAccessExpires();

                if(fbToken == null || fbExpires<0) {
                    // Something wrong
                    // TODO Improve this stage
                } else {
                    // Storing Facebook credentials in preferences
                    Editor e = pref.edit();
                    e.putString(DogUtil.FB_ACCESS_TOKEN_PREFERENCE_KEY, fbToken);
                    e.putLong(DogUtil.FB_ACCESS_EXPIRES_PREFERENCE_KEY, fbExpires);
                    e.commit();

                    // Pulling user data
                    String me = f.request("me");

                    // Parsing
                    FacebookUser user = new Gson().fromJson(me, FacebookUser.class);

                    // Call web service
                    Signup task = new Signup(context);
                    String[] args = {
                            user.getEmail(),
                            user.getId(),
                            IS_FACEBOOK_SIGNUP};
                    task.execute(args);
                }
            } catch (Exception e) {
                Log.e(DogUtil.DEBUG_TAG, e.toString(), e);
            }
        }
    }

	private class DialogListenerForFacebookSinup implements DialogListener {
	    private final Context context;

	    public DialogListenerForFacebookSinup(Context context) {
	        this.context = context;
	    }

        @Override
        public void onComplete(Bundle values) {
            new GetDataFB(context).run();
        }

        @Override
        public void onFacebookError(FacebookError error) {
            Log.e(DogUtil.DEBUG_TAG, error.toString());
        }

        @Override
        public void onError(DialogError e) {
            Log.e(DogUtil.DEBUG_TAG, e.toString());
        }

        @Override
        public void onCancel() {
            // Nothing to do
        }
    };

	private void dispatchResult(Integer result) {
		if(result == null || result < 0) {
			// -1 => Some data wrong
			// -2 => User registered
			// -3 => Server error
            Log.d(DogUtil.DEBUG_TAG, "Webservice response for signup request => " + result);
            showOkDialog(this, getString(R.string.an_error_ocurred), ONLY_DISMISS);
        } else {
			// Return back
	        Intent intent = new Intent();
	        intent.putExtra("ID_USER", result);
	        setResult(RESULT_OK, intent);
	        finish();
		}
	}

	private boolean validateEmail() {
		String email = emailValueField.getText().toString();
		return email.length() > 5;
	}

    private boolean validatePassword() {
        String password = passwordValueField.getText().toString();

        if(password == null || password.trim().length() < PASSWORD_MIN_SIZE)
            return false;

        return true;
    }

    private boolean validatePasswordConfirmarion() {
        String password = passwordValueField.getText().toString();
        String confirmation = passwordConfirmationValueField.getText().toString();

        if(confirmation == null || confirmation.trim().length() < PASSWORD_MIN_SIZE)
            return false;

        return password.equals(confirmation);
    }

	protected class Signup extends AsyncTask<String, Integer, Integer> {
		private final Context context;
		private ProgressDialog dialog;

		public Signup(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setMessage(context.getString(R.string.please_wait_signing_up));
			dialog.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			WsDogUtils ws = new WsDogUtils();
			Integer result = null;
			try {
			    result = ws.insertUsers(params[0],params[1],params[2]);
			} catch (IOException e) {
			    Log.e(DogUtil.DEBUG_TAG, e.toString(), e);
			} catch (XmlPullParserException e) {
			    Log.e(DogUtil.DEBUG_TAG, e.toString(), e);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			dialog.dismiss();
			dispatchResult(result);
		}
	}
}

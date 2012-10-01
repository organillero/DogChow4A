package mx.ferreyra.dogapp;


import static mx.ferreyra.dogapp.ui.DialogHelper.ONLY_DISMISS;
import static mx.ferreyra.dogapp.ui.DialogHelper.showOkDialog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mx.ferreyra.dogapp.pojos.FacebookUser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.gson.Gson;

public class LoginScreen extends Activity{

	private String result;
	private SoapParseLogin soapParseLogin;
	private EditText email,password;
	private Intent i;
	private String returnValue;
	private String userEmail,userPassword;
	private ProgressBar progressBar;

	private Button btnLeftTitle, btnRightTitle;
	private TextView txtTitle;

	private LoginHandler loginHandler;

    public static final String IS_FACEBOOK_SIGNUP = "1";
    public static final String NOT_IS_FACEBOOK_SIGNUP = "0";
    private Facebook f;

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);


		btnLeftTitle = (Button)findViewById(R.id.tbutton_left);
		txtTitle = (TextView)findViewById(R.id.title_txt);
		btnRightTitle = (Button)findViewById(R.id.tbutton_right);

		txtTitle.setText(getResources().getString(R.string.login_title));
		btnRightTitle.setVisibility(View.INVISIBLE);


		email = (EditText)findViewById(R.id.emailEdit);
		password = (EditText)findViewById(R.id.passwordEdit);
		progressBar = (ProgressBar)findViewById(R.id.login_progress);
		progressBar.setVisibility(View.INVISIBLE);



		btnLeftTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});


	}

    public void onClickForgotPasswordButton(View view) {
        startActivity(new Intent(this, RetrievePassword.class));
    }

    public void onClickSubmitButton(View view) {
        if(validateForm()) {
            String input[] = {
                this.userEmail = this.email.getText().toString(),
                this.userPassword = this.password.getText().toString()
            };
            soapParseLogin = new SoapParseLogin(this);
            soapParseLogin.execute(input);
        }
    }

    public void onClickLoginWithFacebook(View view) {
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
                    String[] args = {
                            user.getEmail(),
                            user.getId()};
                    soapParseLogin = new SoapParseLogin(context);
                    soapParseLogin.execute(args);
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

    /**
     * Validates form to send for login.
     */
    public boolean validateForm() {
        String userEmail = this.email.getText().toString();
        String userPassword = this.password.getText().toString();

        // Email validation
        if("".equals(userEmail.trim())) {
            // Email is empty
            showOkDialog(this, getString(R.string.email_empty_message), ONLY_DISMISS);
            return false;
        }

        // Password validation
        if("".equals(userPassword.trim())) {
            // Password is empty
            showOkDialog(this, getString(R.string.password_empty_message), ONLY_DISMISS);
            return false;
        }

        return true;
    }

	@Override
	protected void onPause() {
		super.onPause();
		if(progressBar != null)
			progressBar.setVisibility(View.INVISIBLE);

		if(soapParseLogin != null && (soapParseLogin.getStatus() == Status.PENDING || soapParseLogin.getStatus() == Status.RUNNING)){
			soapParseLogin.cancel(true);
		}
	}

	private class SoapParseLogin extends AsyncTask<String, Integer, Integer> {
		private String errorMsg;
		private final Context context;
		private ProgressDialog dialog;

		public SoapParseLogin(Context context) {
		    this.context = context;
		}

		@Override
		protected void onPreExecute() {
		    super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.please_wait_signing_up));
            dialog.show();
//			if(progressBar != null){
//				progressBar.setVisibility(View.VISIBLE);
//
//				int cornerRadius = 10;
//				float[] outerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
//				RectF inset = new RectF(2, 2, 2, 2);
//				float[] innerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
//				ShapeDrawable circle = new ShapeDrawable(new RoundRectShape(outerR, inset, innerR));
//				circle.setPadding(6, 0, 6, 0);
//
//				GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.BLUE, Color.RED});
//				gradient.setCornerRadius(cornerRadius);
//				gradient.setBounds(circle.getBounds());
//
//
//				progressBar.setProgressDrawable(gradient);
//				progressBar.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
//			}
		}


		@Override
        protected Integer doInBackground(String... results) {
		    WsDogUtils ws = new WsDogUtils();
		    Integer result = null;
            try {
                result = ws.userLogin(results[0], results[1]);
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
//			try{
//				if(progressBar != null)
//					progressBar.setVisibility(View.INVISIBLE);
//
//				if (loginHandler != null && !loginHandler.getData())
//					Toast.makeText(getApplicationContext(), R.string.service_error, Toast.LENGTH_SHORT).show();
//				else if(!b){
//					Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if (returnValue.equals("-1")){
//					Toast.makeText(getApplicationContext(), R.string.invalid_params, Toast.LENGTH_SHORT).show();
//				}else if (returnValue.equals("-2")) {
//					Toast.makeText(getApplicationContext(), R.string.invalid_username_pwd, Toast.LENGTH_SHORT).show();
//				}else if (returnValue.equals("-3")) {
//					Toast.makeText(getApplicationContext(), R.string.user_not_exist, Toast.LENGTH_SHORT).show();
//				}else{
//					Integer userId = Integer.parseInt( returnValue );
//					// Save user id
//                    DogUtil.getInstance().saveCurrentUserId(userId);
//
//                    Intent intent = new Intent();
//			        intent.putExtra("ID_USER", userId);
//
//					setResult(RESULT_OK, intent);
//					finish();
//				}
//			}catch (Exception e) {
//
//			}
		}

		@Override
		protected void onCancelled() {

			super.onCancelled();
			try{
				if(progressBar != null)
					progressBar.setVisibility(View.INVISIBLE);
			}catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void dispatchResult(Integer result) {
        if(result == null || result < 0) {
            // -1 => Some data wrong
            // -2 => User registered
            // -3 => Server error
            Log.d(DogUtil.DEBUG_TAG, "Webservice response for signup request => " + result);
            showOkDialog(this, getString(R.string.an_error_ocurred), ONLY_DISMISS);
        } else {
            // Save user id
            DogUtil.getInstance().saveCurrentUserId(result);

            // Return back
            Intent intent = new Intent();
            intent.putExtra("ID_USER", result);
            setResult(RESULT_OK, intent);
            finish();
        }
//          Integer userId = Integer.parseInt( returnValue );
//          // Save user id
//            DogUtil.getInstance().saveCurrentUserId(userId);
//
//            Intent intent = new Intent();
//          intent.putExtra("ID_USER", userId);
//
//          setResult(RESULT_OK, intent);
//          finish();        }
    }

	private  class LoginHandler extends DefaultHandler{

		private boolean isreturn;
		private boolean hasData = false;


		@Override
		public void startDocument() throws SAXException {

		}

		@Override
		public void endDocument() throws SAXException {

		}

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (localName.equals("return")){
				isreturn = true;
				hasData = true;
			}
		}
		@Override
		public void endElement(String namespaceURI, String localName, String qName)
		throws SAXException {

			if (localName.equals("return")){
				isreturn = false;

			}
		}

		@Override
		public void characters(char ch[], int start, int length) {
			String value = new String(ch, start, length);
			if(value == null || value.length()<=0)
				return;

			if (isreturn){
				returnValue = value;
			}
		}

		public boolean getData(){
			return hasData;
		}
	}

	private boolean getParsedMyXML(String xml) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();

		XMLReader xr = sp.getXMLReader();

		loginHandler = new LoginHandler();
		xr.setContentHandler(loginHandler);
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			xr.parse(new InputSource(is));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		if(!loginHandler.getData())
			return false;


		return true;
	}
}

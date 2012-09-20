package mx.ferreyra.dogapp;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mx.ferreyra.dogapp.org.ksoap2.SoapEnvelope;
import mx.ferreyra.dogapp.org.ksoap2.serialization.SoapObject;
import mx.ferreyra.dogapp.org.ksoap2.serialization.SoapSerializationEnvelope;
import mx.ferreyra.dogapp.org.ksoap2.transport.HttpTransportSE;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class RatingsActivity extends Activity {

	private Button title_left, title_right;
	private TextView title_txt;
	private RatingsTask soapParseLogin;

	private String result;
	private Intent i; 
	private String returnValue;
	private String routeID;
	private int ratingValue; 
	private GoogleAnalyticsTracker analyticsTracker;
	private String routeName;
	private Facebook facebook;
	private RatingBar ratingBar;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.ratings);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar); 

		
		title_left = (Button)findViewById(R.id.tbutton_left);
		title_right = (Button)findViewById(R.id.tbutton_right);
		title_txt = (TextView)findViewById(R.id.title_txt);

		//routeName = getIntent().getStringExtra(Utilities.ROUTE_NAME);
		routeName = ((DogUtil)getApplication()).getRouteName();
		title_txt.setText(routeName);	

		routeID = getIntent().getStringExtra("routeID");

		title_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});


		title_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent (RatingsActivity.this, ExerciseMenu.class);
				startActivity(i);
				finish();
			}
		});
		ratingBar = (RatingBar)findViewById(R.id.ratingBar1);
		ratingBar.setRating(Float.valueOf(getIntent().getStringExtra(Utilities.ROUTE_RATINGS)));
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				float ratings = ratingBar.getRating();
				ratingValue = (int) Math.ceil(ratings);
			}
		});

		
		facebook = new Facebook(DogUtil.FACEBOOK_APPID);
		SessionStore.restore(facebook, getApplicationContext());

		if(analyticsTracker == null)
			analyticsTracker = ((DogUtil)getApplication()).getTracker();
	}

    public void onClickButton1Button(View view) {
        String input[]={
            routeID,
            String.valueOf(ratingValue)};
        soapParseLogin = new RatingsTask();
        soapParseLogin.execute(input);
    }

    public void onClickCancelButton(View view) {
        analyticsTracker.trackEvent("Cancel Ratings",       // Category
                                    "Button",               // Action
                                    "clicked",              // Label
                                    DogUtil.TRACKER_VALUE); // Value
        DogUtil.TRACKER_VALUE++;
        finish();
    }

    public void onClickFbButton(View view) {
        share();
    }

    public void onClickTwrButton(View view) {
        String url = "http://twitter.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
        finish();
    }

	public void share(){
		if (! facebook.isSessionValid()) {
			loginAndSend();
		}else{	
			Intent intent = new Intent(RatingsActivity.this, PublishFacebookScreen.class);
			//intent.putExtra(Utilities.SHARE_ROUTE_NAME, routeName);				
			startActivity(intent);
			finish();
		}
	}

	private String[] Permissions = new String[] {Utilities.PERMISSION_PUBLISH_STREAM, Utilities.PERMISSION_READ_STREAM, Utilities.PERMISSION_OFFLINE_ACCESS};
	private boolean isWindowOpen = true;

	public void loginAndSend(){
		isWindowOpen  = false;
		facebook.authorize(this, Permissions, Facebook.FORCE_DIALOG_AUTH, new DialogListener() {

			@Override
			public void onFacebookError(FacebookError e) {
				isWindowOpen = true;
				showToast(getString(R.string.fb_error_msg));

			}

			@Override
			public void onError(DialogError e) {
				isWindowOpen = true;
				showToast(getString(R.string.fb_dialog_error_msg));

			}

			@Override
			public void onComplete(Bundle values) {
				isWindowOpen = true;
				SessionStore.save(facebook, getApplicationContext());
				Intent intent = new Intent(RatingsActivity.this, PublishFacebookScreen.class);
				intent.putExtra(Utilities.SHARE_ROUTE_NAME, routeName);				
				startActivity(intent);
				finish();			
			}

			@Override
			public void onCancel() {
				isWindowOpen = true;
				showToast(getString(R.string.fb_cancel_error_msg));
					
			}
		});
	}

	Handler handler = new Handler();
	private void showToast(final String message){
		handler.post(new Runnable() {

			@Override
			public void run() {
				
				LayoutInflater factory = LayoutInflater.from(getApplicationContext());
				View dialogView = factory.inflate(R.layout.route_naming_dialog,null);

				final Dialog dialog = new Dialog(RatingsActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(dialogView);
				dialog.setCancelable(true);
				
				TextView coverLarge = (TextView)dialogView.findViewById(R.id.naming_description);
				coverLarge.setText(message);
				Button button = (Button) dialogView.findViewById(R.id.naming_success);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});				
				dialog.show();
				
		
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();

		if(analyticsTracker != null)
			analyticsTracker.dispatch();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(analyticsTracker == null)
			analyticsTracker = ((DogUtil)getApplication()).getTracker();

	}

	@Override
	protected void onStop() {
		super.onStop();
		if(analyticsTracker == null)
			analyticsTracker.stopSession();
	}


	private class RatingsTask extends AsyncTask<String, String, Boolean> {

		private String errorMsg;
		private final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_title);
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			title_right.setVisibility(View.INVISIBLE);
		}

		protected Boolean doInBackground(String... results) {
			try {
				SoapObject request = new SoapObject(AppData.NAMESPACE,
						AppData.METHOD_NAME_INSERT_RATINGS);

				request.addProperty("routeId", routeID);
				request.addProperty("rating", ratingValue);


				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet=true;
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						AppData.URL);
				androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				androidHttpTransport.debug=true;
				androidHttpTransport.call(AppData.SOAP_ACTION+AppData.METHOD_NAME_INSERT_RATINGS, envelope); 

				result = androidHttpTransport.responseDump;		
				return getParsedMyXML(result);
			}
			catch (UnknownHostException e) {
				errorMsg = getResources().getString(R.string.no_connection);
				Log.e(DogUtil.DEBUG_TAG, errorMsg);
				return false;
			}catch (UnknownServiceException e) {
				errorMsg = getResources().getString(R.string.service_unavailable);				
				Log.e(DogUtil.DEBUG_TAG,  getResources().getString(R.string.service_unavailable));
				return false;
			}catch (MalformedURLException e) {
				errorMsg = getResources().getString(R.string.url_malformed);	
				e.printStackTrace();
				Log.e(DogUtil.DEBUG_TAG, errorMsg );
				return false;
			}catch (Exception e) {
				errorMsg = getResources().getString(R.string.unable_to_get_data);			
				Log.e(DogUtil.DEBUG_TAG, errorMsg);
				e.printStackTrace();
				return false;
			}


		}

		protected void onProgressUpdate(String... values ) {
		}

		protected void onPostExecute(Boolean b) {

			try{

				if(progressBar != null)
					progressBar.setVisibility(View.INVISIBLE);

				if(!b){
					Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
					return;
				}	


				if (returnValue.equals("-1")){
					Toast.makeText(getApplicationContext(),R.string.invalid_params, Toast.LENGTH_SHORT).show();

				}else if (returnValue.equals("-2")) {
					Toast.makeText(getApplicationContext(), R.string.data_inadequate, Toast.LENGTH_SHORT).show();
				}else if (returnValue.equals("-3")) {
					Toast.makeText(getApplicationContext(),  R.string.data_inadequate, Toast.LENGTH_SHORT).show();
				}else if (isValidNumber(returnValue)){
					String userId = returnValue;

					SharedPreferences pref = getSharedPreferences(Utilities.DOGCHOW, 0);
					SharedPreferences.Editor edit = pref.edit();
					edit.putString(Utilities.USER_ID,userId); 
					edit.commit();

					analyticsTracker.trackEvent(
							"Ratings",  // Category, 
							"Button",  // Action, 
							"processed", // Label    
							DogUtil.TRACKER_VALUE);   // Value
					DogUtil.TRACKER_VALUE++;

					Toast.makeText(getApplicationContext(), R.string.ratings_success, Toast.LENGTH_SHORT).show();
					//i =new Intent(RatingsActivity.this, ExerciseMenu.class); 
					//startActivity(i);
					//finish();
				}else{
					Toast.makeText(getApplicationContext(),  R.string.data_inadequate, Toast.LENGTH_SHORT).show();
				}
			}catch (Exception e) { 

			}

		}
		@Override
		protected void onCancelled() {

			super.onCancelled();
			try{ 
				if(progressBar != null)
					progressBar.setVisibility(View.INVISIBLE);
			}catch (Exception e) {
			}

		}

		private boolean isValidNumber(String number){
			boolean isNumber = false;
			try {				
				if(Integer.parseInt(number)>0)
					isNumber = true;
			}catch (Exception e) {
				isNumber = false;
			}
			return isNumber;
		}
	}


	private  class UserShelfHandler extends DefaultHandler{

		private boolean isreturn;

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
	}
	private boolean getParsedMyXML(String xml) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();

		XMLReader xr = sp.getXMLReader();

		UserShelfHandler myExampleHandler = new UserShelfHandler();
		xr.setContentHandler(myExampleHandler);   
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			xr.parse(new InputSource(is));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(isWindowOpen){
				return super.onKeyDown(keyCode, event);
			}else{
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}

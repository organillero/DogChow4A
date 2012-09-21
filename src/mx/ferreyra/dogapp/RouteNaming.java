package mx.ferreyra.dogapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class RouteNaming extends Activity {
	private Button title_left, title_right;
	private Button easy, med, diff, save, button6;
	private Button fblogout;
	private Button fb, twr;
	private EditText editText;
	private TextView title_txt, txtDesc;
	private double srcLat, srcLong;
	private String routeLat, routeLong;
	private float distance = 0.0f;	
	private String time ="";
	private String returnValue = "0";
	private ProgressBar namingProgress;
	protected static final String TAG = "RouteNaming";
	private FacebookConnector facebookConnector;
	private GoogleAnalyticsTracker analyticsTracker;

	private Facebook facebook;

	private String text = "<html><body bgcolor=\"#020202\"> Que nivel de dificultad " +
			"<font color=\"#015b29\">tiene esta ruta? </font></body></html>";
	private String shareText = null;


	private DIFFICULTY_LEVEL level;
	private boolean isWindowOpen  = true;

	private Context context;


	public static enum DIFFICULTY_LEVEL {
		EASY, MEDIUM, DIFFICULT 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.newroute);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);
		editText = (EditText)findViewById(R.id.editText1);
		txtDesc = (TextView)findViewById(R.id.textView2);
		title_left = (Button)findViewById(R.id.tbutton_left);
		title_right = (Button)findViewById(R.id.tbutton_right);
		namingProgress = (ProgressBar)findViewById(R.id.naming_progress);
		namingProgress.setVisibility(View.INVISIBLE);
		button6 = (Button)findViewById(R.id.button6);

		easy = (Button)findViewById(R.id.button3);
		med = (Button)findViewById(R.id.button2);
		diff = (Button)findViewById(R.id.button4);
		easy.setBackgroundResource(R.drawable.easy);
		med.setBackgroundResource(R.drawable.medium_normal);
		diff.setBackgroundResource(R.drawable.difficult_normal);

		save = (Button)findViewById(R.id.button5);
		save.setEnabled(true);	
		fblogout = (Button)findViewById(R.id.btn_fblogout); 
		txtDesc.setText(Html.fromHtml(text));

		fb = (Button)findViewById(R.id.fb);
		twr = (Button)findViewById(R.id.twr);

		Bundle bundle = getIntent().getExtras();
		srcLat = bundle.getDouble("sourceLatitude");
		srcLong = bundle.getDouble("sourceLongitude");
		routeLat = bundle.getString("routeLatitude");
		routeLong = bundle.getString("routeLongitude");
		distance = bundle.getFloat("distance");
		time = bundle.getString("timeTaken");

		level = DIFFICULTY_LEVEL.EASY;

		title_txt = (TextView)findViewById(R.id.title_txt); 
		title_txt.setText("NUEVA RUTA"); 

		context = this;

		button6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				analyticsTracker.trackEvent(
						"Cancel Route Naming",  // Category
						"Button",  // Action,
						"clicked", // Label    
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;
				finish();			
			}
		});


		title_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(RouteNaming.this, TrackMapRoute.class);  
				startActivity(i); 
			}
		});

		title_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i =new Intent(RouteNaming.this,ExerciseMenu.class);
				startActivity(i);
				finish();
			}
		});

		easy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				level = DIFFICULTY_LEVEL.EASY;
				easy.setBackgroundResource(R.drawable.easy);
				med.setBackgroundResource(R.drawable.medium_normal);
				diff.setBackgroundResource(R.drawable.difficult_normal);

				analyticsTracker.trackEvent(
						"Easy",  // Category
						"Button",  // Action,
						"clicked", // Label    
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;


			}
		});
		med.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				level = DIFFICULTY_LEVEL.MEDIUM;
				easy.setBackgroundResource(R.drawable.easy_normal);
				med.setBackgroundResource(R.drawable.medium);
				diff.setBackgroundResource(R.drawable.difficult_normal);

				analyticsTracker.trackEvent(
						"Medium",  // Category
						"Button",  // Action, 
						"clicked", // Label    
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;

			}
		});
		diff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				level = DIFFICULTY_LEVEL.DIFFICULT;
				easy.setBackgroundResource(R.drawable.easy_normal);
				med.setBackgroundResource(R.drawable.medium_normal);
				diff.setBackgroundResource(R.drawable.difficult);

				analyticsTracker.trackEvent(
						"Difficult",  // Category
						"Button",  // Action, 
						"clicked", // Label   
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;
			}
		});


		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(DogUtil.getInstance().getCurrentUserId()==null) {
					startActivityForResult(new Intent(context, PreSignup.class), DogUtil.NEW_ROUTE);
				} else {


					if(!validateRouteName()){
						Toast.makeText(getApplicationContext(),  getResources().getString(R.string.enter_route_name), Toast.LENGTH_SHORT).show();
						return;
					}

					SharedPreferences preferences = getSharedPreferences(Utilities.DOGCHOW, 0);				

					if(preferences != null){
						String userId = DogUtil.getInstance().getCurrentUserId().toString();//preferences.getString(Utilities.USER_ID, "-1");
						if(userId != null && !userId.equals("")){
							SoapParseInsertRoute insertRoute = new SoapParseInsertRoute();
							insertRoute.execute("");
						}else{
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_not_exist), Toast.LENGTH_SHORT).show();
						}
					}

					analyticsTracker.trackEvent(
							"Save Route",  // Category
							"Button",  // Action, 
							"clicked", // Label    
							DogUtil.TRACKER_VALUE);   // Value
					DogUtil.TRACKER_VALUE++;
				}

			}
		});
		fb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!validateRouteName()){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_route_name), Toast.LENGTH_SHORT).show();
					return;
				}
				shareText = editText.getText().toString();
				share();
				analyticsTracker.trackEvent(
						"Facebook",  // Category
						"Button",  // Action, 
						"clicked", // Label    
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;				

			}
		});
		twr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(!validateRouteName()){
					Toast.makeText(getApplicationContext(),  getResources().getString(R.string.enter_route_name), Toast.LENGTH_SHORT).show();
					return;
				}

				String url = "http://twitter.com/";  
				Intent i = new Intent(Intent.ACTION_VIEW);  
				i.setData(Uri.parse(url));  
				startActivity(i); 

				analyticsTracker.trackEvent(
						"Twitter",  // Category
						"Button",  // Action, 
						"clicked", // Label    
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;

			}
		});

		fblogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {	
				if(facebookConnector == null)
					facebookConnector= new FacebookConnector(DogUtil.FACEBOOK_APPID, RouteNaming.this, getApplicationContext(), ((DogUtil)getApplicationContext()).getPermissions());
				if(clearCredentials()){
					SharedPreferences pref = getSharedPreferences(Utilities.DOGCHOW, 0);
					SharedPreferences.Editor edit = pref.edit();
					edit.putString(Utilities.USER_ID,""); 
					edit.commit();
					Toast.makeText(getApplicationContext(), R.string.logout_success, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), R.string.logout_unsuccess, Toast.LENGTH_SHORT).show();
				}


			}
		});


		facebook = new Facebook(DogUtil.FACEBOOK_APPID);
		boolean isSession = SessionStore.restore(facebook, getApplicationContext());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(facebookConnector == null)
					facebookConnector= new FacebookConnector(DogUtil.FACEBOOK_APPID, RouteNaming.this, getApplicationContext(), ((DogUtil)getApplicationContext()).getPermissions());
				if(facebookConnector.isSessionValid()){
					fblogout.setVisibility(View.VISIBLE);
				}else
					fblogout.setVisibility(View.INVISIBLE);
			}
		});

		if(analyticsTracker == null)
			analyticsTracker = ((DogUtil)getApplication()).getTracker();

	}

	public void share(){
		Log.d(DogUtil.DEBUG_TAG,"have token "+facebook.isSessionValid());
		if (!facebook.isSessionValid()) {
			loginAndSend();
		}else{	
			Intent intent = new Intent(RouteNaming.this, PublishFacebookScreen.class);
			intent.putExtra(Utilities.SHARE_ROUTE_NAME, shareText);				
			startActivity(intent);
		}
	}

	public void loginAndSend(){
		isWindowOpen = false;
		facebook.authorize(this, new String[] {}, Facebook.FORCE_DIALOG_AUTH, new DialogListener() {

			@Override
			public void onFacebookError(FacebookError e) {
				isWindowOpen = true;
				showToast(getString(R.string.fb_error_msg));
				finish();

			}

			@Override
			public void onError(DialogError e) {
				isWindowOpen = true;
				showToast(getString(R.string.fb_dialog_error_msg));
				finish();

			}

			@Override
			public void onComplete(Bundle values) {
				isWindowOpen = true;
				SessionStore.save(facebook, getApplicationContext());
				Intent intent = new Intent(RouteNaming.this, PublishFacebookScreen.class);
				intent.putExtra(Utilities.SHARE_ROUTE_NAME, shareText);				
				startActivity(intent);

			}

			@Override
			public void onCancel() {
				isWindowOpen = true;
				showToast(getString(R.string.fb_cancel_error_msg));
				finish();				
			}
		});
	}

	private void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isWindowOpen = true;
		if(analyticsTracker == null)
			analyticsTracker = ((DogUtil)getApplication()).getTracker();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if(namingProgress != null)
			namingProgress.setVisibility(View.INVISIBLE);


		if(analyticsTracker != null)
			analyticsTracker.dispatch();
	}

	@Override
	protected void onStop() {
		super.onStop();	

		if(analyticsTracker != null)
			analyticsTracker.stopSession();
	}

	private class SoapParseInsertRoute extends AsyncTask<String, String, Boolean> {

		private String errorMsg;
		@Override
		protected void onPreExecute() {
			if(namingProgress != null)
				namingProgress.setVisibility(View.VISIBLE);
		}

		protected Boolean doInBackground(String... results) {
			try {
				SoapObject request = new SoapObject(AppData.NAMESPACE,
						AppData.METHOD_NAME_INSERT_ROUTE);

				request.addProperty("routeName", editText.getText().toString());
				request.addProperty("sourceLatitude", String.valueOf(srcLat));
				request.addProperty("sourceLongitude", String.valueOf(srcLong));
				request.addProperty("routeLatitude", routeLat);
				request.addProperty("routeLongitude", routeLong);
				request.addProperty("distance", String.valueOf(distance));
				request.addProperty("timeTaken", time);
				request.addProperty("difficulty", level.name());
				request.addProperty("userId",  DogUtil.getInstance().getCurrentUserId().toString()/*Integer.parseInt(getSharedPreferences("dogapp", 0).getString("userid", "-1"))*/);


				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet=true;
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						AppData.URL);
				androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				androidHttpTransport.debug=true;
				androidHttpTransport.call(AppData.SOAP_ACTION+AppData.METHOD_NAME_INSERT_ROUTE, envelope); 

				String result = androidHttpTransport.responseDump;		


				return getParsedMyXML(result);
			}
			catch (UnknownHostException e) {
				errorMsg = getResources().getString(R.string.error_in_save);
				Log.e(DogUtil.DEBUG_TAG, errorMsg);
				e.printStackTrace();				
				return false;
			}catch (UnknownServiceException e) {
				errorMsg = getResources().getString(R.string.error_in_save);
				Log.e(DogUtil.DEBUG_TAG,  getResources().getString(R.string.service_unavailable));
				return false;
			}catch (MalformedURLException e) {
				errorMsg = getResources().getString(R.string.error_in_save);
				e.printStackTrace();
				Log.e(DogUtil.DEBUG_TAG, errorMsg );
				return false;
			}catch (Exception e) {
				errorMsg = getResources().getString(R.string.error_in_save);
				Log.e(DogUtil.DEBUG_TAG, errorMsg);
				return false;
			}

		}

		protected void onProgressUpdate(String... values ) {
		}

		protected void onPostExecute(Boolean b) {

			try{

				if(namingProgress != null)
					namingProgress.setVisibility(View.INVISIBLE);

				if(b == null || !b){
					Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
				}
				if (Integer.parseInt(returnValue)>0){

					//set up dialog
					final Dialog dialog = new Dialog(RouteNaming.this);									
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.route_naming_dialog);	
					dialog.setCancelable(true);

					//set up button
					Button button = (Button) dialog.findViewById(R.id.naming_success);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();

					if(save != null){
						save.setEnabled(false);
						button6.setEnabled(false);
					}
				}
			}catch (Exception e) { 

			}

		}
		@Override
		protected void onCancelled() {

			super.onCancelled();
			try{ 
				if(namingProgress != null)
					namingProgress.setVisibility(View.INVISIBLE);
			}catch (Exception e) {
			}

		}
	}

	private boolean getParsedMyXML(String xml) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();

		XMLReader xr = sp.getXMLReader();

		RouteNamingHandler myExampleHandler = new RouteNamingHandler();
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

	private  class RouteNamingHandler extends DefaultHandler{

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

	public void postToMessage() {
		if (facebookConnector.getFacebook().isSessionValid()) {
			Intent intent = new Intent(RouteNaming.this, PublishFacebookScreen.class);
			intent.putExtra(Utilities.SHARE_ROUTE_NAME, editText.getText().toString());				
			startActivity(intent);
		} else {
			SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {

				@Override
				public void onAuthSucceed() {
					postMessageInThread();

				}

				@Override
				public void onAuthFail(String error) {
				}
			};
			SessionEvents.addAuthListener(listener);
			facebookConnector.loginOnly(this, editText.getText().toString());
		}
	}


	public void postMessage() {
		if (facebookConnector.getFacebook().isSessionValid()) {
			postMessageInThread();
		} else {
			SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {

				@Override
				public void onAuthSucceed() {
					postMessageInThread();

				}

				@Override
				public void onAuthFail(String error) {
				}
			};
			SessionEvents.addAuthListener(listener);
			facebookConnector.login();
		}
	}	

	final Runnable mUpdateFacebookNotification = new Runnable() {
		public void run() {
			Toast.makeText(getBaseContext(), getResources().getString(R.string.facebook_updated), Toast.LENGTH_LONG).show();
		}
	};



	String getFacebookMsg() {
		String wall = getString(R.string.route_share_text1)+" "+shareText+" "
				+getString(R.string.route_share_text2)+getString(R.string.dogchow)+ " "+getString(R.string.share_text)+" "+getString(R.string.share_link);
		return wall;
	}

	private void postMessageInThread() {
		Thread t = new Thread() {
			public void run() {
				try {
					facebookConnector.postMessageOnWall(getString(R.string.dogchow), facebookConnector.getAccessToken(), getFacebookMsg());
				} catch (Exception ex) {
					Log.e(DogUtil.DEBUG_TAG, "Error sending msg: ",ex);
				}
			}
		};
		t.start();
	}	

	private boolean clearCredentials() {
		try {
			if(facebookConnector != null)
				facebookConnector.getFacebook().logout(getApplicationContext());			
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean validateRouteName(){
		if(editText != null && editText.getText().toString().length()>0)
			return true;
		else 
			return false;
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





	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == Activity.RESULT_OK && intent != null){

			if ( requestCode == DogUtil.NEW_ROUTE){

				if(!validateRouteName()){
					Toast.makeText(getApplicationContext(),  getResources().getString(R.string.enter_route_name), Toast.LENGTH_SHORT).show();
					return;
				}

				SharedPreferences preferences = getSharedPreferences(Utilities.DOGCHOW, 0);				

				if(preferences != null){
					
					
					
					
					String userId = DogUtil.getInstance().getCurrentUserId().toString();//preferences.getString(Utilities.USER_ID, "-1");
					if(userId != null && !userId.equals("")){
						SoapParseInsertRoute insertRoute = new SoapParseInsertRoute();
						insertRoute.execute("");
					}else{
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_not_exist), Toast.LENGTH_SHORT).show();
					}
				}

				analyticsTracker.trackEvent(
						"Save Route",  // Category
						"Button",  // Action, 
						"clicked", // Label    
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;
			}
		}
	}


}
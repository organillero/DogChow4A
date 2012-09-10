package mx.ferreyra.dogapp;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mx.ferreyra.dogapp.AppData.USER_LOGIN_TYPE;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ManualRegister extends Activity{

	private String result;
	private SoapParseLogin soapParseLogin;
	private EditText confirmPassword,email,password;
	private Button submit;
	private Intent i; 
	private String returnValue;
	private String userEmail,userPassword, confirmPwd;
	private ProgressBar loginProgress;

	private TextView txtTitle;
	private Button txtBackBtn, txtHomeBtn;

	private String regExpn =
		"^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
		+"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
		+"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
		+"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
		+"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
		+"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

	private Boolean isValidEmail(String inputEmail) {
		Pattern patternObj = Pattern.compile(regExpn);
		Matcher matcherObj = patternObj.matcher(inputEmail);
		if (matcherObj.matches()){			
			return true;
		}
		else{			
			return false;
		}
	}

	public boolean eMailValidation(String emailstring)
	{
		Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher emailMatcher = emailPattern.matcher(emailstring);
		return emailMatcher.matches();
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	


		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.manual); 
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);


		txtBackBtn = (Button)findViewById(R.id.tbutton_left);
		txtTitle = (TextView)findViewById(R.id.title_txt);
		txtHomeBtn = (Button)findViewById(R.id.tbutton_right);

		txtHomeBtn.setVisibility(View.INVISIBLE);
		txtTitle.setText(getResources().getString(R.string.register));



		email = (EditText)findViewById(R.id.manualemailEdit);
		password = (EditText)findViewById(R.id.manualpasswordEdit);
		confirmPassword = (EditText)findViewById(R.id.manualconfirmPasswordEdit);
		loginProgress = (ProgressBar)findViewById(R.id.register_progress);
		loginProgress.setVisibility(View.INVISIBLE);
		submit = (Button)findViewById(R.id.submit);

		submit.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				confirmPwd=confirmPassword.getText().toString();

				userEmail=email.getText().toString();

				userPassword= password.getText().toString();


				if(userPassword.length()>0 && confirmPwd.length()>0 && !userPassword.equals(confirmPwd)){
					Toast.makeText(getApplicationContext(), R.string.pwd_repwd_notmatch, Toast.LENGTH_SHORT).show(); 
					return;
				}else if(userEmail == null || userEmail.length()<1 || !isValidEmail(userEmail)){
					Toast.makeText(getApplicationContext(), R.string.email_not_valid, Toast.LENGTH_SHORT).show(); 
					return;
				}else if(userPassword== null || confirmPwd==null || userPassword.length()<3 || confirmPwd.length()<3 ){
					Toast.makeText(getApplicationContext(), R.string.min_three_chars, Toast.LENGTH_SHORT).show(); 
					return;
				}

				String input[]={userEmail,userPassword,"0"};

				soapParseLogin = new SoapParseLogin();
				soapParseLogin.execute(input);

			}
		});

		txtBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//i =new Intent(ManualRegister.this,RegisterScreen.class); 
				//startActivity(i);
				finish();
			}
		});


	} 


	@Override
	protected void onPause() {
		super.onPause();

		if(loginProgress != null)
			loginProgress.setVisibility(View.INVISIBLE);

	}
	private class SoapParseLogin extends AsyncTask<String, String, Boolean> {
		private String errorMsg;
		@Override
		protected void onPreExecute() {

			if(loginProgress != null)
				loginProgress.setVisibility(View.VISIBLE);

			float[] outerR = new float[] { 12, 12, 12, 12, 0, 0, 0, 0 };
			RectF   inset = new RectF(6, 6, 6, 6);
			float[] innerR = new float[] { 12, 12, 0, 0, 12, 12, 0, 0 };



			ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(outerR, inset, innerR));
			String MyColor = "#FF00FF";
			pgDrawable.getPaint().setColor(Color.parseColor(MyColor));
			ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

			loginProgress.setProgressDrawable(progress);   
			loginProgress.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
			loginProgress.setProgress(80);

		}

		protected Boolean doInBackground(String... results) {
			try {
				SoapObject request = new SoapObject(AppData.NAMESPACE,
						AppData.METHOD_NAME_USER_REGSTRATION);


				request.addProperty("username", userEmail);
				request.addProperty("password", userPassword);
				request.addProperty("isFacebook", "0");

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet=true;
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(AppData.URL);
				androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				androidHttpTransport.debug=true;
				androidHttpTransport.call(AppData.SOAP_ACTION+AppData.METHOD_NAME_USER_REGSTRATION, envelope);



				result = androidHttpTransport.responseDump;		
				return getParsedMyXML(result);
			}
			catch (UnknownHostException e) {
				errorMsg = getResources().getString(R.string.no_connection);
				Log.e(this.getClass().getSimpleName(), errorMsg);
				return false;
			}catch (UnknownServiceException e) {
				errorMsg = getResources().getString(R.string.service_unavailable);				
				Log.e(this.getClass().getSimpleName(),  getResources().getString(R.string.service_unavailable));
				return false;
			}catch (MalformedURLException e) {
				errorMsg = getResources().getString(R.string.url_malformed) + e.getMessage();	
				e.printStackTrace();
				Log.e(this.getClass().getSimpleName(), errorMsg );
				return false;
			}catch (Exception e) {
				errorMsg = getResources().getString(R.string.unable_to_get_data);	
				Log.e(this.getClass().getSimpleName(), errorMsg);
				return false;
			}


		}

		protected void onProgressUpdate(String... values ) {
		}

		protected void onPostExecute(Boolean b) {

			try{


				if(loginProgress != null)
					loginProgress.setVisibility(View.INVISIBLE);

				if(!b){
					Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
					return;
				}		

				if (returnValue.equals("-3")) {
					Toast.makeText(getApplicationContext(), R.string.user_already_exist, Toast.LENGTH_SHORT).show(); //Username not exists
				}else if (returnValue.equals("-2")) {
					Toast.makeText(getApplicationContext(), R.string.enter_username_pwd, Toast.LENGTH_SHORT).show(); //Please enter Username or Password
				}else if (returnValue.equals("-1")) {
					Toast.makeText(getApplicationContext(), R.string.invalid_username_pwd, Toast.LENGTH_SHORT).show(); //"Invalid Username or Password"
				}else {
					SharedPreferences pref = getSharedPreferences(Utilities.DOGCHOW, 0);
					SharedPreferences.Editor edit = pref.edit();
					edit.putString(Utilities.USER_ID, returnValue); 
					edit.commit();
					AppData.USER_ID = returnValue.trim();
					AppData.assignType(USER_LOGIN_TYPE.APPLICATION); 
					i =new Intent(ManualRegister.this,LoginMainScreen.class); 
					startActivity(i);
				}
			}catch (Exception e) { 

			}

		}
		@Override
		protected void onCancelled() {

			super.onCancelled();
			try{ 
				if(loginProgress != null)
					loginProgress.setVisibility(View.INVISIBLE);
			}catch (Exception e) {
			}

		}
	}


	private  class ManualRegisterHandler extends DefaultHandler{

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

		ManualRegisterHandler manualRegisterHanlder = new ManualRegisterHandler();
		xr.setContentHandler(manualRegisterHanlder);   
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			xr.parse(new InputSource(is));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}

	public void callPref() {


	}
}

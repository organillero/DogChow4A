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
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RetrievePassword extends Activity {

	private TextView title;
	private Button titleRight;
	private Button titleLeft;
	private ImageView titleImage;
	private Intent i;

	private EditText emailId; 
	private String userEmail;

	private String parseResult;
	private String returnValue;
	private ProgressBar sendProgress;
	private SoapParseRetrievePassword parseRetrievePassword;
	private RetrievePasswordHandler retrievePasswordHandler;


	private Context context;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);		
		setContentView(R.layout.retrieve_password);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

		context = this;
		
		titleLeft = (Button)findViewById(R.id.tbutton_left);
		titleLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, LoginScreen.class));
				finish();
			}
		});
		
		title = (TextView)findViewById(R.id.title_txt);
		title.setVisibility(View.INVISIBLE);

		
		titleImage =  (ImageView)findViewById(R.id.title_image);
		titleImage.setVisibility(View.VISIBLE);
		
		
		titleRight = (Button)findViewById(R.id.tbutton_right);
		titleRight.setVisibility(View.GONE);

		sendProgress = (ProgressBar)findViewById(R.id.send_progress);
		sendProgress.setVisibility(View.INVISIBLE);

		emailId = (EditText)findViewById(R.id.re_pwd_text);
	}

    public void onClickForgotPasswordSendButton(View view) {
        String input[] = {
            this.userEmail = emailId.getText().toString().trim()
        };
        this.parseRetrievePassword = new SoapParseRetrievePassword();
        this.parseRetrievePassword.execute(input);
    }

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private class SoapParseRetrievePassword extends AsyncTask<String, String, Boolean> {
		private String errorMsg;
		@Override
		protected void onPreExecute() {
			if(sendProgress != null){
				sendProgress.setVisibility(View.VISIBLE);

				int cornerRadius = 10;
				float[] outerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
				RectF inset = new RectF(2, 2, 2, 2);
				float[] innerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
				ShapeDrawable circle = new ShapeDrawable(new RoundRectShape(outerR, inset, innerR));
				circle.setPadding(6, 0, 6, 0);

				GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.BLUE, Color.RED});
				gradient.setCornerRadius(cornerRadius);
				gradient.setBounds(circle.getBounds());	
				
				sendProgress.setProgressDrawable(gradient);   
				sendProgress.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));


			}
		}


		protected Boolean doInBackground(String... results) {
			try {
				SoapObject request = new SoapObject(AppData.NAMESPACE,	AppData.METHOD_NAME_USER_RECOVERY_PWD);

				request.addProperty("username", userEmail);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet=true;

				envelope.setOutputSoapObject(request);

				HttpTransportSE androidHttpTransport = new HttpTransportSE(AppData.URL);
				androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				androidHttpTransport.debug=true;				

				androidHttpTransport.call(AppData.SOAP_ACTION+AppData.METHOD_NAME_USER_RECOVERY_PWD, envelope); 

				parseResult = androidHttpTransport.responseDump;	

				return getParsedMyXML(parseResult);
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
				return false;
			}


		}

		protected void onProgressUpdate(String... values ) {

		}

		protected void onPostExecute(Boolean b) {
			try{
				if(sendProgress != null)
					sendProgress.setVisibility(View.INVISIBLE);

				if (retrievePasswordHandler != null && !retrievePasswordHandler.getData())
					Toast.makeText(getApplicationContext(), R.string.service_error, Toast.LENGTH_SHORT).show();
				else if(!b){
					Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
					return;
				}	

				if (returnValue.equals(getString(R.string.retrieve_pwd_email_mandatory))){
					Toast.makeText(getApplicationContext(), R.string.retrieve_pwd_send_failure, Toast.LENGTH_SHORT).show();
				}else if(returnValue.equals(getString(R.string.retrieve_pwd_email_not_correct))){
					Toast.makeText(getApplicationContext(), R.string.retrieve_pwd_send_failure, Toast.LENGTH_SHORT).show();
				}else{
					
					i =new Intent(RetrievePassword.this, RetrievePasswordResult.class);
					i.putExtra(Utilities.RETRIEVE_PASSWORD_RESULT, returnValue);
					startActivity(i);
					finish();
					returnValue = null;
				}
			}catch (Exception e) { 

			}
		}

		@Override
		protected void onCancelled() {

			super.onCancelled();
			try{ 
				if(sendProgress != null)
					sendProgress.setVisibility(View.INVISIBLE);
			}catch (Exception e) {
			}

		}
	}


	private  class RetrievePasswordHandler extends DefaultHandler{

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

		retrievePasswordHandler = new RetrievePasswordHandler();
		xr.setContentHandler(retrievePasswordHandler);   
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			xr.parse(new InputSource(is));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} 

		if(!retrievePasswordHandler.getData())
			return false;


		return true;
	}



}

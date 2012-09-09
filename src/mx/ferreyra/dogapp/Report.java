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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Report extends Activity{
	private TextView txt_time;
	private TextView txt_distance;
	private TextView txt_speed;
	private ProgressBar statsProgressBar;
	private String result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.stats);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);
		statsProgressBar = (ProgressBar)findViewById(R.id.progress_title);
		statsProgressBar.setVisibility(View.VISIBLE);		

		((TextView)(findViewById(R.id.title_txt))).setText(R.string.title_stat);

		((Button)(findViewById(R.id.tbutton_left))).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i =new Intent(Report.this, ExerciseMenu.class); 
				startActivity(i);
				finish(); 
			}
		});

		((Button)(findViewById(R.id.tbutton_right))).setVisibility(View.GONE);

		txt_time = (TextView) findViewById(R.id.time_report);
		txt_distance = (TextView) findViewById(R.id.distance_report);		
		txt_speed = (TextView) findViewById(R.id.speed_report);		
		LoadPreferences();

		SharedPreferences pref = getSharedPreferences(Utilities.DOGCHOW, 0);
		String userId = pref.getString(Utilities.USER_ID, "");


		if(userId == null || userId.equals("") || !DogUtil.checkNumber(userId)){
			Toast.makeText(getApplicationContext(), getString(R.string.unable_to_process), Toast.LENGTH_SHORT).show();
			statsProgressBar.setVisibility(View.GONE);	
		}else{
			SoapParseLogin soapParseLogin = new SoapParseLogin();
			soapParseLogin.execute(userId);			
		}
	}


	private void LoadPreferences(){ 
		String time =  getResources().getString(R.string.route_time);
		String distance = getResources().getString(R.string.route_distance);
		String speed = getResources().getString(R.string.route_speed);


		txt_time.setText(time);
		txt_distance.setText(distance);
		txt_speed.setText(speed);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


	private class SoapParseLogin extends AsyncTask<String, String, Boolean> {
		private String errorMsg;
		@Override
		protected void onPreExecute() {
			if(statsProgressBar != null){
				statsProgressBar.setVisibility(View.VISIBLE);
			}
		}


		protected Boolean doInBackground(String... results) {
			try {
				SoapObject request = new SoapObject(AppData.NAMESPACE,	AppData.METHOD_NAME_USER_STATS);

				request.addProperty("userId", results[0]);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet=true;

				envelope.setOutputSoapObject(request);

				HttpTransportSE androidHttpTransport = new HttpTransportSE(AppData.URL);
				androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

				androidHttpTransport.debug=true;				

				androidHttpTransport.call(AppData.SOAP_ACTION+AppData.METHOD_NAME_USER_STATS, envelope); 
		
							
				result = androidHttpTransport.responseDump;
				
				//Log.i("Report","result "+result);

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
				errorMsg = getResources().getString(R.string.url_malformed);	
				e.printStackTrace();
				Log.e(this.getClass().getSimpleName(), errorMsg );
				return false;
			}catch (Exception e) {
				errorMsg = getResources().getString(R.string.unable_to_get_data);
				Log.e(this.getClass().getSimpleName(), errorMsg);
				return false;
			}


		}

		protected void onPostExecute(Boolean b) {
			try{
				
				if(statsProgressBar != null)
					statsProgressBar.setVisibility(View.INVISIBLE);

				if (reportHandler != null && !reportHandler.getData()){
					Toast.makeText(getApplicationContext(), R.string.service_error, Toast.LENGTH_SHORT).show();
					if(statsProgressBar != null)
						statsProgressBar.setVisibility(View.GONE);
					return;
				}
				else if(!b){
					Log.e("Report","Error: "+errorMsg);
					Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
					if(statsProgressBar != null)
						statsProgressBar.setVisibility(View.GONE);
					return;
				}

				StatsData data = reportHandler.getStatsData();

				if(data.getSecondTime() != null && !data.getSecondTime().equals(""))
					txt_time.setText(data.getSecondTime());

				if(data.getDistance() != null && !data.getDistance().equals("") ){
					txt_distance.setText(data.getDistance()+" km");
				}

				if(data.getSpeed() != null && !data.getSpeed().equals("")){
					txt_speed.setText(data.getSpeed()+" (km/h)");
				}


			}catch (Exception e) { 

			}
		}

		@Override
		protected void onCancelled() {

			super.onCancelled();
			try{ 
				if(statsProgressBar != null)
					statsProgressBar.setVisibility(View.INVISIBLE);
			}catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.getMessage());
			}

		}
	}

	private class StatsData{
		private String distance;
		private String secondTime;
		private String speed;

		public String getDistance() {
			return distance;
		}
		public void setDistance(String distance) {
			this.distance = distance;
		}
		public String getSecondTime() {
			return secondTime;
		}
		public void setSecondTime(String secondTime) {
			this.secondTime = secondTime;
		}
		public String getSpeed() {
			return speed;
		}
		public void setSpeed(String speed) {
			this.speed = speed;
		}




	}


	private  class ReportHandler extends DefaultHandler{

		private boolean isDistance;
		private boolean isSecondTime;
		private boolean isSpeed;
		private boolean hasData = false;
		private StatsData statsData = null; 

		public StatsData getStatsData(){
			return statsData;
		}

		@Override
		public void startDocument() throws SAXException {
			statsData = new StatsData();
		}

		@Override
		public void endDocument() throws SAXException {

		}

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {		
			if (localName.equals("Distance")){
				isDistance = true;
				hasData = true;
			}else if(localName.equals("SecondTime")){
				isSecondTime = true;
				hasData = true;
			}else if(localName.equals("Speed")){
				isSpeed = true;
				hasData = true;
			}
		} 
		@Override
		public void endElement(String namespaceURI, String localName, String qName)
		throws SAXException {
			if (localName.equals("Distance")){
				isDistance = false;
			}else if(localName.equals("SecondTime")){
				isSecondTime = false;
			}else if(localName.equals("Speed")){
				isSpeed = false;
			}
		}  

		@Override
		public void characters(char ch[], int start, int length) {
			String value = new String(ch, start, length);
			if(value == null || value.length()<=0)
				return;
			if (isDistance){
				statsData.setDistance(value);
			}else if(isSecondTime){
				statsData.setSecondTime(value);
			}else if(isSpeed){
				statsData.setSpeed(value);
			}
		}

		public boolean getData(){
			return hasData;
		}
	}
	ReportHandler reportHandler;
	private boolean getParsedMyXML(String xml) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();

		XMLReader xr = sp.getXMLReader();

		reportHandler = new ReportHandler();
		xr.setContentHandler(reportHandler);   
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			xr.parse(new InputSource(is));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} 

		if(!reportHandler.getData())
			return false;


		return true;
	}


}
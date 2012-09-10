package mx.ferreyra.dogapp;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class LoadPath extends Activity {

	private String result;
	private String userId;

	private String latitude,longitude;
	private int intValue=0;
	private ArrayList<Dataset> list = new ArrayList<Dataset>();

	private TextView nearByPlaces;
	private Button title_left , title_right;

	private ProgressBar progressBar;
	private SoapParseLogin soapParseLogin;
	private ListAdapter mAdapter;
	private ListView route_list;
	private GoogleAnalyticsTracker analyticsTracker;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar); 

		route_list = (ListView)findViewById(R.id.widget46);
		mAdapter=new ListAdapter(LoadPath.this);
		title_left = (Button)findViewById(R.id.tbutton_left);
		title_right = (Button)findViewById(R.id.tbutton_right);
		progressBar = (ProgressBar)findViewById(R.id.pathLoading);
		title_right.setBackgroundResource(R.drawable.home_btn);    
		title_right.setVisibility(View.INVISIBLE);
		nearByPlaces = (TextView)findViewById(R.id.widget51);

		title_left.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {  
				finish();
			}
		});
		title_right.setOnClickListener(new OnClickListener() {

			public void onClick(View v) { 
				Intent i =new Intent(LoadPath.this,ExerciseMenu.class);
				startActivity(i);
				finish(); 
			}
		});


		Intent mIntent = getIntent();
		intValue = mIntent.getIntExtra(Utilities.TRAINING_SPOT, 0);
		if(intValue == 1) {
			LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L,500.0f, locationListener);
			Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(location != null)                                
			{
				double lati = location.getLatitude();
				double longi = location.getLongitude();	


				latitude = Double.toString(lati); 
				longitude = Double.toString(longi);	


				String input[]={latitude,longitude};
				soapParseLogin = new SoapParseLogin();
				soapParseLogin.execute(input);

			}else{  
				Toast.makeText(getApplicationContext(), getString(R.string.details_are_not_available), Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.INVISIBLE);
			}

		}else if (intValue ==2) {
			SharedPreferences pref = getSharedPreferences(Utilities.DOGCHOW, 0); 
			userId = pref.getString(Utilities.USER_ID, "");

			soapParseLogin = new SoapParseLogin();
			soapParseLogin.execute(userId); 

		} 

		if(analyticsTracker == null)
			analyticsTracker = ((DogUtil)getApplication()).getTracker();

	}

	protected void onResume() {
		super.onResume();

		if(analyticsTracker == null)
			analyticsTracker = ((DogUtil)getApplication()).getTracker();

	};


	protected void onPause() {
		super.onPause();
		if(analyticsTracker != null)
			analyticsTracker.dispatch();
	};

	protected void onStop() {
		super.onStop();

		if(analyticsTracker != null)
			analyticsTracker.stopSession();

	};

	private class SoapParseLogin extends AsyncTask<String, String, Boolean> {

		private String errorMsg;
		@Override
		protected void onPreExecute() {
		}

		protected Boolean doInBackground(String... results) {			
			try {
				if(intValue==1) {
					SoapObject request = new SoapObject(AppData.NAMESPACE,
							AppData.METHOD_NAME_GET_TRAININGSPOT);
					request.addProperty("latitude", latitude);
					request.addProperty("longitude", longitude);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.dotNet=true;
					envelope.setOutputSoapObject(request);
					envelope.bodyOut = request;
					envelope.implicitTypes = true;
					envelope.encodingStyle = "utf-8";
					envelope.enc = SoapSerializationEnvelope.ENC2003;
					envelope.xsd = SoapEnvelope.XSD;
					envelope.xsi = SoapEnvelope.XSI;

					HttpTransportSE androidHttpTransport = new HttpTransportSE(
							AppData.URL);

					androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					androidHttpTransport.debug=true;
					androidHttpTransport.call(AppData.SOAP_ACTION+AppData.METHOD_NAME_GET_TRAININGSPOT, envelope); 

					result = androidHttpTransport.responseDump;
					getParsedMyXML(result);	
					//Log.d("Loadpath", "result: "+result);

				}else if(intValue==2){
					SoapObject request = new SoapObject(AppData.NAMESPACE,
							AppData.METHOD_NAME_GET_USER_TRAININGSPOT);

					request.addProperty("userId", userId);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.dotNet=true;
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(
							AppData.URL);
					androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					androidHttpTransport.debug=true;
					androidHttpTransport.call(AppData.SOAP_ACTION+AppData.METHOD_NAME_GET_USER_TRAININGSPOT, envelope); 

					result = androidHttpTransport.responseDump;	
					getParsedMyXML(result);
				} 
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
			}catch (XmlPullParserException e) {
				errorMsg = getResources().getString(R.string.unable_to_retrieve_data);	
				Log.e(this.getClass().getSimpleName(), errorMsg);	
				return false;
			}catch (Exception e) {
				errorMsg = getResources().getString(R.string.unable_to_get_data);	
				Log.e(this.getClass().getSimpleName(), "Exception : "+errorMsg);
				e.printStackTrace();
				return false;
			}
			return true;


		}

		protected void onProgressUpdate(String... values ) {
		}

		protected void onPostExecute(Boolean b) {


			if(!b){
				if(errorMsg != null || !errorMsg.equals(""))
					Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
				if(progressBar != null )
					progressBar.setVisibility(View.GONE);
				return;
			}		

			mAdapter.notifyDataSetChanged();

			route_list.setAdapter(mAdapter);
			route_list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {

					analyticsTracker.trackEvent(list.get(arg2).getRouteName(), 
							"List Item Click", "clicked", DogUtil.TRACKER_VALUE);
					DogUtil.TRACKER_VALUE++;

					Intent i = new Intent(LoadPath.this,ShowMapRoute.class);
					i.putExtra("routeID",list.get(arg2).getRouteId().toString());
					DogUtil dogUtil = (DogUtil)getApplication();
					dogUtil.setRouteName(list.get(arg2).getRouteName());
					//i.putExtra(Utilities.ROUTE_NAME,list.get(arg2).getRouteName().toString());
					i.putExtra(Utilities.INTENT_ROUTE_LATITUDE,list.get(arg2).getRouteLatitude().toString());
					i.putExtra(Utilities.INTENT_ROUTE_LONGITUDE, list.get(arg2).getRouteLongitude().toString());
					i.putExtra(Utilities.INTENT_SOURCE_LATITUDE, list.get(arg2).getSourceLatitude().toString());
					i.putExtra(Utilities.INTENT_SOURCE_LONGITUDE, list.get(arg2).getSourceLongitude().toString());
					i.putExtra(Utilities.ROUTE_DISTANCE, list.get(arg2).getDistance());
					i.putExtra(Utilities.ROUTE_TIMING, list.get(arg2).getTimeTaken());
					i.putExtra(Utilities.ROUTE_RATINGS, list.get(arg2).getRating());
					//Log.d("CargarRuta", "Ratings "+list.get(arg2).getRating());
					startActivity(i); 


				}
			});

			nearByPlaces.setText(mAdapter.getCount()+" "+getResources().getString(R.string.nearby_places));
			if(progressBar != null )
				progressBar.setVisibility(View.INVISIBLE);

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
	} 

	private Dataset getParsedMyXML(String xml) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser(); 
		Dataset parseddata = new Dataset();
		XMLReader xr = sp.getXMLReader();

		XMLHandler myExampleHandler = new XMLHandler();
		xr.setContentHandler(myExampleHandler);
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			xr.parse(new InputSource(is));

		} catch (UnsupportedEncodingException e) {
			Log.e("LoadRoute", e.getMessage());
			e.printStackTrace();
		} 
		return parseddata;
	}




	private class ListAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater mInflater;
		private int height, width;
		private int lastIndex = 0;
		private DecimalFormat twoDecimal = new DecimalFormat("#.###");
		private ColorDrawable oddcolorDrawable = new ColorDrawable(Color.parseColor("#a0a0a0"));
		private ColorDrawable evencolorDrawable = new ColorDrawable(Color.parseColor("#e4e4e4"));

		public ListAdapter(Context c) {
			mContext = c;
			mInflater=LayoutInflater.from(mContext);
			WindowManager window = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
			Display d = window.getDefaultDisplay();
			height = d.getHeight();
			width = d.getWidth();
		}

		@Override
		public int getCount() {
			return list.size();
		}


		@Override
		public Dataset getItem(int position) {
			return list.get(position);
		}


		@Override
		public long getItemId(int position) {
			return position;
		}

		public int getCurrentSelection(){
			return lastIndex;
		}

		private String twoDecimals(double distance){

			DecimalFormat df= new DecimalFormat("#0.00");  

			String fdistance = df.format(distance);  
			double dist = Double.parseDouble(fdistance);  

			return String.valueOf(dist);
		}

		private String timeTaken(String time){
			try {
				if(time.contains(":")){
					return trimLeadingZeros(time.substring(0, time.indexOf(":"))).trim();				
				}
			}catch (NumberFormatException e) {
				e.printStackTrace();			
			}
			return "0";			
		}

		private String trimLeadingZeros(String source)
		{
			for (int i = 0; i < source.length(); ++i)
			{
				char c = source.charAt(i);
				if (c != '0' && !Character.isSpaceChar(c))
					return source.substring(i);
			}

			return source;
		}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			lastIndex = position;
			ViewHolder holder;
			try {
				if(convertView==null) {					
					convertView=mInflater.inflate(R.layout.listcontent, null);
					holder=new ViewHolder();
					holder.route_text = (TextView)convertView.findViewById(R.id.textView0);
					holder.route_name = (TextView)convertView.findViewById(R.id.textView1);
					holder.route_distance = (TextView)convertView.findViewById(R.id.textView3);
					holder.route_mts = (TextView)convertView.findViewById(R.id.textView2);
					holder.difficulty = (ImageView)convertView.findViewById(R.id.imageView2);
					holder.ratingBar = (ImageView)convertView.findViewById(R.id.imageView4);					
					convertView.setTag(holder);
				} else {
					holder=(ViewHolder) convertView.getTag();
				}				


				final Dataset dataset = getItem(position);	
				if(position<list.size()) {			
					holder.route_name.setId(position);					
					holder.route_name.setText(dataset.getRouteName());
					holder.route_distance.setText(twoDecimals(Double.parseDouble(dataset.getDistance()))+" Km");
					//int min = (int)(Integer.valueOf(dataset.getTimeTaken())/60);
					//holder.route_mts.setText(String.valueOf(min) + " mts");

					holder.route_mts.setText(timeTaken(dataset.getTimeTaken())+" mts");
					if(position%2 == 0){
						convertView.setBackgroundDrawable(oddcolorDrawable.mutate());
						holder.route_text.setTextColor(Color.parseColor("#e4e4e4"));
						holder.route_distance.setTextColor(Color.parseColor("#e4e4e4"));				


						if(dataset.getDifficulty().equalsIgnoreCase(RouteNaming.DIFFICULTY_LEVEL.DIFFICULT.name())){
							holder.difficulty.setImageResource(R.drawable.diff_green);
						}else if (dataset.getDifficulty().equalsIgnoreCase(RouteNaming.DIFFICULTY_LEVEL.MEDIUM.name())){
							holder.difficulty.setImageResource(R.drawable.medio_green);
						}else{
							holder.difficulty.setImageResource(R.drawable.facil_green);

						}
					}
					else{
						convertView.setBackgroundDrawable(evencolorDrawable.mutate());
						holder.route_text.setTextColor(Color.parseColor("#a0a0a0"));
						holder.route_distance.setTextColor(Color.parseColor("#a0a0a0"));

						if(dataset.getDifficulty().equalsIgnoreCase(RouteNaming.DIFFICULTY_LEVEL.DIFFICULT.name())){
							holder.difficulty.setImageResource(R.drawable.diff_green);
						}else if (dataset.getDifficulty().equalsIgnoreCase(RouteNaming.DIFFICULTY_LEVEL.MEDIUM.name())){
							holder.difficulty.setImageResource(R.drawable.medio_green);
						}else{
							holder.difficulty.setImageResource(R.drawable.facil_green);

						}

					}

					if(dataset.getRating().equalsIgnoreCase("1")){
						holder.ratingBar.setImageResource(R.drawable.bones1);
					}else if(dataset.getRating().equalsIgnoreCase("2")){
						holder.ratingBar.setImageResource(R.drawable.bones2);
					}else if(dataset.getRating().equalsIgnoreCase("3")){
						holder.ratingBar.setImageResource(R.drawable.bones3);
					}else if(dataset.getRating().equalsIgnoreCase("4")){
						holder.ratingBar.setImageResource(R.drawable.bones4);
					}else{
						holder.ratingBar.setImageResource(R.drawable.bones5);
					}

				} 			

			}
			catch (Exception e) {
			}


			return convertView;
		}		

	}

	static class ViewHolder {
		TextView route_text;
		TextView route_name;
		TextView route_distance;
		TextView route_mts;
		ImageView difficulty;
		ImageView ratingBar;

	}


	public class XMLHandler extends DefaultHandler{

		private boolean isrouteId;
		private boolean isrouteName;
		private boolean issourceLatitude;
		private boolean issourceLongitude;
		private boolean isrouteLatitude;
		private boolean isrouteLongitude;
		private boolean isdistance;
		private boolean istimeTaken;
		private boolean isdifficulty;
		private boolean isuserId;
		private boolean israting;
		private Dataset data;

		private StringBuilder builder;


		@Override
		public void startDocument() throws SAXException {
			super.startDocument(); 
			builder = new StringBuilder();
		}


		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
			builder = null;
		}


		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			//Log.e("Start ********* ", localName);
			if (localName.equals("Table")) {
				data = new Dataset();
			}
			if(localName.equals("routeId")){
				isrouteId=true;
			}else if(localName.equals("routeName")){
				isrouteName=true;
			}else if(localName.equals("sourceLatitude")){
				issourceLatitude=true;
			}else if(localName.equals("sourceLongitude")){
				issourceLongitude=true;
			}else if(localName.equals("routeLatitude")){
				isrouteLatitude=true;
				builder = builder.delete(0, builder.length());
			}else if(localName.equals("routeLongitude")){
				isrouteLongitude=true;
				builder = builder.delete(0, builder.length());
			}else if(localName.equals("distance")){
				isdistance=true;
			}else if(localName.equals("timeTaken")){
				istimeTaken=true;
			}else if(localName.equals("difficulty")){
				isdifficulty=true;
			}else if(localName.equals("userId")){
				isuserId=true;
			}else if(localName.equals("rating")){
				israting=true;
			}

		}


		@Override
		public void endElement(String uri, String localName, String qName)
		throws SAXException {

			//Log.e("End ********** ", localName);
			if(localName.equals("Table")){
				//Log.e("Its comin here2", localName + " " + data);
				//Log.e("Its comin here2", localName + " " + data.getRating());
				//data.setRouteId(routeId);
				list.add(data);
				mAdapter.notifyDataSetChanged();
			}else
				if(localName.equals("routeId")){
					//data.setRouteId(routeId);
					isrouteId=false; 
				}else if(localName.equals("routeName")){
					//data.setRouteName(routeName);
					isrouteName=false;
				}else if(localName.equals("sourceLatitude")){
					//data.setSourceLatitude(sourceLatitude);
					issourceLatitude=false;
				}else if(localName.equals("sourceLongitude")){
					//data.setSourceLongitude(sourceLongitude);
					issourceLongitude=false;
				}else if(localName.equals("routeLatitude")){
					data.setRouteLatitude(builder.substring(0));
					//data.setRouteLatitude(routeLatitude);
					isrouteLatitude=false;
				}else if(localName.equals("routeLongitude")){
					data.setRouteLongitude(builder.substring(0));
					//data.setRouteLongitude(routeLongitude);
					isrouteLongitude=false;
				}else if(localName.equals("distance")){
					//data.setDistance(distance);
					isdistance=false;
				}else if(localName.equals("timeTaken")){
					//data.setTimeTaken(timeTaken);
					istimeTaken=false;
				}else if(localName.equals("difficulty")){
					//data.setDifficulty(difficulty);
					isdifficulty=false;
				}else if(localName.equals("userId")){
					//data.setUserId(userId);
					isuserId=false;
				}else if(localName.equals("rating")){
					//data.setRating(rating);
					israting=false;
				}
		}
		//}

		@Override
		public void characters(char[] ch, int start, int length)
		throws SAXException {
			String value = new String(ch, start,length);
			//.i("LoadPath", "Value: "+value);
			//Log.i("LoadPath", "israting : "+israting);

			if(value == null||value.length()<=0){
				return;
			} 
			//Log.i("LoadPath", "israting to be set : "+israting);
			if(isrouteId){
				data.setRouteId(value);
			}else if(isrouteName){
				data.setRouteName(value);
			}else if(issourceLatitude){
				data.setSourceLatitude(value);
			}else if(issourceLongitude){
				data.setSourceLongitude(value);
			}else if(isrouteLatitude){
				builder.append(value);
				//data.setRouteLatitude(value);
			}else if(isrouteLongitude){
				builder.append(value);
				//data.setRouteLongitude(value);
			}else if(isdistance){
				data.setDistance(value);
			}else if(istimeTaken){
				data.setTimeTaken(value);
			}else if(isdifficulty){
				data.setDifficulty(value);
			}else if(isuserId){
				data.setUserId(value);
			}else if(israting){				
				data.setRating(value);
				//Log.v("LoadPath", "Parsing : Ratings: "+value);
			}

		} 
	}

	private void updateWithNewLocation(Location location) {
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
		} else {
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

}

package mx.ferreyra.dogapp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TrackMapRoute extends MapActivity implements LocationListener{

	private MapView mapView;
	private LocationManager locationManager;
	private double srcLat, srcLong;
	private ImageView title_image;
	private TextView title_txt, bottom_timer;
	private Button title_left , title_right;
	private Button btnStart,btnStop;
	private Button mapa,stats,camera;
	private Button btn_save,btn_resume,btn_discard;
	private Dialog alert;
	private String time ="00.00.00";
	private String lastTime ="";
	private SharedPreferences sharedPreferences;
	private final Handler handler = new Handler();
	protected long startTime = 0L;
	private boolean isRunning = false;
	private static final String PERSIST_IS_RUNNING = "isRunning";
	private static final String PERSIST_START_MARKER_MILLIS = "startMarkerMillis";
	//	private static final long MINUTES = 1000*5;
	//	private static final long METERS = 3;
	//
	private static final long MINUTES = 0;
	private static final long METERS = 0;

	private GeoPoint previousGeoPoint;
	private Location sourceLocation;
	private FrameLayout bottomBar;
	private boolean isStat= false;

	private TextView txt_time_stats, bottom_txt_distance,  bottom_txt_speed, txt_report_speed, txt_report_distance;

	private float distance = 0.0f;
	private String speed = "0";
	private GoogleAnalyticsTracker analyticsTracker;
	private ProgressBar statsProgressBar;
	private String result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.trackmap);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);

		title_left = (Button)findViewById(R.id.tbutton_left);
		title_right = (Button)findViewById(R.id.tbutton_right);
		title_right.setVisibility(View.INVISIBLE);
		title_txt = (TextView)findViewById(R.id.title_txt);
		title_image = (ImageView)findViewById(R.id.title_image);
		title_image.setVisibility(View.VISIBLE);

		txt_time_stats = (TextView)findViewById(R.id.time_report);
		txt_time_stats.setText("00.00.00");

		statsProgressBar = (ProgressBar)findViewById(R.id.progress_title);
		statsProgressBar.setVisibility(View.INVISIBLE);


		bottom_timer =  (TextView)findViewById(R.id.bottom_time_txt);
		bottom_txt_distance = (TextView)findViewById(R.id.bottom_distance_txt);
		bottom_txt_speed = (TextView)findViewById(R.id.bottom_speed_txt);

		bottomBar = (FrameLayout)findViewById(R.id.bottom_bar);

		txt_report_speed = (TextView)findViewById(R.id.speed_report);
		txt_report_distance = (TextView)findViewById(R.id.distance_report);

		title_txt.setVisibility(View.GONE);

		title_left.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				Intent i =new Intent(TrackMapRoute.this,ExerciseMenu.class);
				startActivity(i);
				finish();
			}
		});

		title_right.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				List<Overlay> overlays =  mapView.getOverlays();
				List<Double> latitudes = new ArrayList<Double>();
				List<Double> longitudes = new ArrayList<Double>();
				for (int i = 0; i < overlays.size(); i++) {
					TrackMapRoute.MyOverLay overlay = (TrackMapRoute.MyOverLay) overlays.get(i);
					latitudes.add(overlay.getGeoPointSourceLatitude()/1e6);
					latitudes.add(overlay.getGeoPointDestLatitude()/1e6);

					longitudes.add(overlay.getGeoPointSourceLongitude()/1e6);
					longitudes.add(overlay.getGeoPointDestLongitude()/1e6);
				}

				Intent i =new Intent(TrackMapRoute.this,RouteNaming.class);
				i.putExtra("sourceLatitude", srcLat);
				i.putExtra("sourceLongitude", srcLong);
				i.putExtra("routeLatitude", latitudes.toString());
				i.putExtra("routeLongitude", longitudes.toString());
				i.putExtra("distance", distance);
				i.putExtra("timeTaken", lastTime);
				startActivity(i);
				finish();
			}
		});

		mapa = (Button)findViewById(R.id.button1);
		stats = (Button)findViewById(R.id.button2);
		//ipod = (Button)findViewById(R.id.button3);
		camera= (Button)findViewById(R.id.button5);
		camera.setVisibility(View.INVISIBLE);

		stats.setBackgroundResource(R.drawable.stats);
		//stats.setText("Stats");
		mapa.setBackgroundResource(R.drawable.mapa_sele);


		//mapa.setText("Mapa");

		//		ipod.setOnClickListener(new OnClickListener() {
		//
		//			public void onClick(View v) {
		//
		//				mapa.setBackgroundResource(R.drawable.map_grey);
		//				stats.setBackgroundResource(R.drawable.stats_grey);
		//				ipod.setBackgroundResource(R.drawable.ipod_green);
		//
		//			}
		//		});

		btnStart = (Button)findViewById(R.id.button4);
		btnStart.setOnClickListener(startClickListener);

		btnStop = (Button) findViewById(R.id.button6);
		btnStop.setOnClickListener(resetClickListener);

		mapView = (MapView) findViewById(R.id.myMap);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		mapView.getController().setZoom(18);
		mapView.setBuiltInZoomControls(true);


		//Set the America continent as default
		GeoPoint america_continent = new GeoPoint((int)(22.181981 * 1e6),   (int)(-104.238281 * 1e6));
		mapView.getController().animateTo(america_continent);
		mapView.getController().setZoom(3);

		if(analyticsTracker == null)
			analyticsTracker = ((DogUtil)getApplication()).getTracker();

		loadDefault();


	}

    public void onClickButton1Button(View v) {
	((RelativeLayout)findViewById(R.id.header)).setVisibility(View.INVISIBLE);
	mapView.setVisibility(View.VISIBLE);
	mapView.setFocusableInTouchMode(true);
	mapa.setBackgroundResource(R.drawable.mapa_sele);
	stats.setBackgroundResource(R.drawable.stats);
	bottomBar.setVisibility(View.VISIBLE);
//	btnStart.setVisibility(View.VISIBLE);
//	btnStop.setVisibility(View.VISIBLE);
	toggleStartButton(isRunning);

	analyticsTracker.trackEvent("Maps",                 // Category,
				    "Button",               // Action,
				    "clicked",              // Label
				    DogUtil.TRACKER_VALUE); // Value
	DogUtil.TRACKER_VALUE++;
	isStat = false;
    }

    public void onClickButton2Button(View v) {
	((RelativeLayout)findViewById(R.id.header)).setVisibility(View.VISIBLE);
	time = "00.00.00";
	txt_time_stats.setText(time);

	mapView.setVisibility(View.INVISIBLE);

	mapa.setBackgroundResource(R.drawable.mapa);
	stats.setBackgroundResource(R.drawable.stats_sele);
	bottomBar.setVisibility(View.INVISIBLE);
	btnStart.setVisibility(View.INVISIBLE);
	btnStop.setVisibility(View.INVISIBLE);

	SharedPreferences pref = getSharedPreferences(Utilities.DOGCHOW	, 0);
	String userId = pref.getString(Utilities.USER_ID, "");

	SoapParseLogin soapParseLogin = new SoapParseLogin();
	soapParseLogin.execute(userId);
	statsProgressBar.setVisibility(View.VISIBLE);
	isStat = true;
	analyticsTracker.trackEvent("Stats",  // Category,
				    "Button",  // Action,
				    "clicked", // Label
				    DogUtil.TRACKER_VALUE);   // Value
	DogUtil.TRACKER_VALUE++;
    }

    public void onClickButton5Button(View v) {
	startActivity(new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA));
    }

	private void SavePreferences(String key, String value){
		SharedPreferences sharedPreferences = getSharedPreferences(Utilities.DOGAPP, MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private void LoadPreferences(){

		SharedPreferences sharedPreferences = getSharedPreferences(Utilities.DOGAPP, MODE_PRIVATE);
		String time = sharedPreferences.getString(Utilities.ROUTE_TIMING, getResources().getString(R.string.route_time));
		String distance = sharedPreferences.getString(Utilities.ROUTE_DISTANCE, getResources().getString(R.string.route_distance));
		String speed = sharedPreferences.getString(Utilities.ROUTE_SPEED, getResources().getString(R.string.route_speed));


		txt_time_stats.setText(time);
		txt_report_distance.setText(distance);
		txt_report_speed.setText(speed);
	}


	public class MyOverLay extends Overlay
	{
		private final GeoPoint gp1;
		private final GeoPoint gp2;
		private int mode=0;
		private final int defaultColor;

		public MyOverLay(GeoPoint gp1,GeoPoint gp2,int mode) // GeoPoint is a int. (6E)
		{
			this.gp1 = gp1;
			this.gp2 = gp2;
			this.mode = mode;
			defaultColor = Color.BLACK; // no defaultColor

		}

		public MyOverLay(GeoPoint gp1,GeoPoint gp2,int mode, int defaultColor)
		{
			this.gp1 = gp1;
			this.gp2 = gp2;
			this.mode = mode;
			this.defaultColor = defaultColor;
		}


		public int getMode()
		{
			return mode;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			Projection projection = mapView.getProjection();
			Paint paint = new Paint();
			Point point = new Point();
			projection.toPixels(gp1, point);
			paint.setColor(defaultColor);
			Point point2 = new Point();
			projection.toPixels(gp2, point2);
			paint.setStrokeWidth(7);
			canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
		}


		public GeoPoint getGeoPointSource(){
			return gp1;
		}

		public GeoPoint getGeoPointDestination(){
			return gp2;
		}

		public int getGeoPointSourceLatitude(){
			return gp1.getLatitudeE6();
		}

		public int getGeoPointSourceLongitude(){
			return gp1.getLongitudeE6();
		}

		public int getGeoPointDestLatitude(){
			return gp2.getLatitudeE6();
		}

		public int getGeoPointDestLongitude(){
			return gp2.getLongitudeE6();
		}

	}


	@Override
	protected void onResume() {
		super.onResume();
		//begin();

		LoadPreferences();

		// Handle shake to reset
		if (sharedPreferences == null) {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TrackMapRoute.this);
		}

		if(analyticsTracker == null)
			analyticsTracker = ((DogUtil)getApplication()).getTracker();


	}

	@Override
	protected void onPause() {
		SavePreferences(Utilities.ROUTE_TIMING, time);
		saveDisplay();

		if(analyticsTracker != null)
			analyticsTracker.dispatch();

		super.onPause();
	}


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void drawPath(GeoPoint previousGeo, GeoPoint currentGeoPoint) {
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(new MyOverLay(previousGeo, currentGeoPoint, Color.BLACK));
		mapView.postInvalidate();

	}

	private void drawStartPoint(GeoPoint startGeoPoint) {
		mx.ferreyra.dogapp.MyOverlay myLocationOverlay = new mx.ferreyra.dogapp.MyOverlay(startGeoPoint);
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(myLocationOverlay);
	}

	public static double EARTH_RADIUS_KM = 6384; // km
	public static double calculateDistanceMeters(double aLong, double aLat,
			double bLong, double bLat) {

		double d2r = (Math.PI / 180);

		double dLat = (bLat - aLat) * d2r;
		double dLon = (bLong - aLong) * d2r;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
		+ Math.cos(aLat * d2r) * Math.cos(bLat * d2r)
		* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS_KM * c * 1000;

	}

	@Override
	public void onLocationChanged(Location location) {
		if (location == null) return;

		GeoPoint point = new GeoPoint((int)(location.getLatitude() * 1e6),   (int)(location.getLongitude() * 1e6));
		mapView.getController().animateTo(point); //<11>
		if(previousGeoPoint == null){
			previousGeoPoint = point;
			mapView.getController().animateTo(point);
			mapView.getController().setZoom(21);
			srcLat = location.getLatitude();
			srcLong = location.getLongitude();
			sourceLocation = location;
			drawStartPoint(point);
		}else{
			drawPath(previousGeoPoint, point);

			distance = sourceLocation.distanceTo(location);
			speed = String.valueOf(location.getSpeed());
			handler.post(updateDistanceTask);
			previousGeoPoint = point;
		}

	}




	@Override
	public void onProviderDisabled(String provider) {
		stopListining();
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}



	private final OnClickListener startClickListener = new OnClickListener() {
		@Override
        public void onClick(View v) {
			begin();
			toggleStartButton(isRunning = true);

			analyticsTracker.trackEvent(
					"Start",  // Category, i.e. Start Button
					"Button",  // Action, i.e. Start
					"clicked", // Label    i.e. Start
					DogUtil.TRACKER_VALUE);   // Value,
			DogUtil.TRACKER_VALUE++;

		}
	};

	private final Runnable updateTimeTask = new Runnable() {
		@Override
        public void run() {
			setTime();
			handler.postDelayed(this, 10);
		}
	};


	private final Runnable updateDistanceTask = new Runnable() {
		@Override
        public void run() {
			bottom_txt_distance.setText(String.valueOf(distance));
			txt_report_distance.setText(String.valueOf(distance));
			bottom_txt_speed.setText(speed);

			txt_report_speed.setText(speed);
		}
	};


	private void setTime() {


		final long start = startTime;
		long millis = System.currentTimeMillis() - start;
		long seconds = (int) (millis / 1000);
		long minutes = (int) (millis / (60 * 1000));
		seconds = seconds % 60;
		long remMillis = (minutes * 60 * 1000) + (seconds * 1000);
		if (remMillis > 0) {
			millis -= remMillis;
		}

		try{
			setDisplay(String.valueOf(minutes), String.valueOf(seconds),
					String.valueOf(millis / 100));
		}catch (NumberFormatException e) {
			Log.e(DogUtil.DEBUG_TAG, "Range may exceeds: "+e.getMessage());
		}
	}

	public void setDisplay(String minutes, String seconds, String millis) throws NumberFormatException{
		try{

			String  displayMinutes=(String.format("%02d", Long.parseLong(minutes)));


			String displaySeconds=(String.format("%02d", Long.parseLong(seconds)));

			String displayMillis=(String.format("%01d", Long.parseLong(millis)));



			time = displayMinutes+" : "+displaySeconds+" : "+displayMillis;
			bottom_timer.setText(displayMinutes+" : "+displaySeconds+" : "+displayMillis);
			if(!isStat)
				txt_time_stats.setText(time);
		}catch (Exception e) {
			Log.e(DogUtil.DEBUG_TAG, "Exception: "+e.getMessage());
			throw new NumberFormatException();
		}


	}

	private final OnClickListener stopClickListener = new OnClickListener() {
		@Override
        public void onClick(View v) {
			stopListining();
			gotoShare();
			done();
		}
	};

	private void stopListining(){

		analyticsTracker.trackEvent(
				"Pause",  // Category, i.e. Pause Button
				"Button",  // Action, i.e. Pause
				"clicked", // Label    i.e. Pause
				DogUtil.TRACKER_VALUE);   // Value
		DogUtil.TRACKER_VALUE++;

		lastTime = time;
		SavePreferences("MILLS", String.valueOf(startTime));
		handler.removeCallbacks(updateTimeTask);
		done();
	}

	private void toggleStartButton(boolean isOn) {
		if (isOn) {
			btnStart.setVisibility(View.INVISIBLE);
			btnStop.setVisibility(View.VISIBLE);
			btnStart.setOnClickListener(startClickListener);

		} else {
			btnStart.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.INVISIBLE);
			btnStop.setOnClickListener(stopClickListener);
		}
	}


	private final OnClickListener resetClickListener = new OnClickListener() {
		@Override
        public void onClick(View v) {
			reset();



		}
	};



	private void reset() {
		lastTime = time;
		done();
		setDisplay("0", "0", "0");
		toggleStartButton(true);
//		btnStart.setVisibility(View.VISIBLE);
//		btnStop.setVisibility(View.INVISIBLE);
	}

	private void done() {
		toggleStartButton(true);
		startTime = 0L;
		isRunning = false;

	}


	@Override
	protected void onStop() {
		saveDisplay();
		if(analyticsTracker != null)
			analyticsTracker.stopSession();

		super.onStop();
	}

	private void saveDisplay() {
		SharedPreferences.Editor editor = getPreferences(0).edit();
		editor.putBoolean(PERSIST_IS_RUNNING, isRunning);
		editor.putLong(PERSIST_START_MARKER_MILLIS, startTime);
		editor.commit();
	}


	private void begin() {

		startLocationUpdates();
		if (startTime == 0L) {
			startTime = System.currentTimeMillis();
			handler.removeCallbacks(updateTimeTask);
			handler.postDelayed(updateTimeTask, 10);
		} else if (isRunning) {
			handler.removeCallbacks(updateTimeTask);
			handler.postDelayed(updateTimeTask, 10);
		}
		toggleStartButton(isRunning);
	}

	private void begin(long lstartTime) {

		startLocationUpdates();
		if(lstartTime != 0L){
			startTime = lstartTime;
			handler.removeCallbacks(updateTimeTask);
			handler.postDelayed(updateTimeTask, 10);
		}else if (startTime == 0L) {
			startTime = System.currentTimeMillis();
			handler.removeCallbacks(updateTimeTask);
			handler.postDelayed(updateTimeTask, 10);
		} else if (isRunning) {
			handler.removeCallbacks(updateTimeTask);
			handler.postDelayed(updateTimeTask, 10);
		}
		toggleStartButton(false);
	}

	private void startLocationUpdates(){
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINUTES, METERS, this);
	}
	TextView textView;

	public void gotoShare() {

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.send, null);
		textView = (TextView)view.findViewById(R.id.txt_pause);
		btn_save  = (Button)view.findViewById(R.id.btn_save);
		btn_resume = (Button)view.findViewById(R.id.btn_resume);
		btn_discard = (Button)view.findViewById(R.id.btn_discard);

		AlertDialog.Builder builder = new AlertDialog.Builder( TrackMapRoute.this);
		builder.setView(view);
		alert = builder.create();
		alert.show();

		btn_save.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View arg0) {
				distance = distance/1000.0f;
				SavePreferences(Utilities.ROUTE_TIMING, time);
				SavePreferences(Utilities.ROUTE_DISTANCE, String.valueOf(distance));
				SavePreferences(Utilities.ROUTE_SPEED, speed);
				List<Overlay> overlays =  mapView.getOverlays();
				List<Double> latitudes = new ArrayList<Double>();
				List<Double> longitudes = new ArrayList<Double>();
				for (int i = 0; i < overlays.size(); i++) {
					Overlay overlay = overlays.get(i);
					if(overlay instanceof mx.ferreyra.dogapp.TrackMapRoute.MyOverLay){
						TrackMapRoute.MyOverLay myoverlay = (TrackMapRoute.MyOverLay)overlay;
						latitudes.add(myoverlay.getGeoPointSourceLatitude()/1e6);
						latitudes.add(myoverlay.getGeoPointDestLatitude()/1e6);

						longitudes.add(myoverlay.getGeoPointSourceLongitude()/1e6);
						longitudes.add(myoverlay.getGeoPointDestLongitude()/1e6);
					}

				}

				Intent i = new Intent(TrackMapRoute.this, RouteNaming.class);
				i.putExtra("sourceLatitude", srcLat);
				i.putExtra("sourceLongitude", srcLong);
				i.putExtra("routeLatitude", latitudes.toString().replace("[", "").replace("]", ""));
				i.putExtra("routeLongitude", longitudes.toString().replace("[", "").replace("]", ""));
				i.putExtra("distance", distance);
				i.putExtra("timeTaken", lastTime);
				startActivity(i);
				reset();
				alert.dismiss();
				handler.removeCallbacks(updateTimeTask);


				analyticsTracker.trackEvent(
						"Save",  // Category, i.e. Save Button
						"Button",  // Action, i.e. Save
						"clicked", // Label    i.e. Save
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;

			}
		});

		btn_resume.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View arg0) {

				saveDisplay();

				startTime = Long.parseLong(getSharedPreferences("DOGAPP", MODE_PRIVATE).getString("MILLS", "0L"));
				begin(startTime);

				alert.dismiss();

				analyticsTracker.trackEvent(
						"Resume",  // Category, i.e. Resume Button
						"Button",  // Action, i.e. Resume
						"clicked", // Label    i.e. Resume
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;

			}
		});

		btn_discard.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View arg0) {


				SavePreferences(Utilities.ROUTE_TIMING, getResources().getString(R.string.route_time));
				SavePreferences(Utilities.ROUTE_DISTANCE, getResources().getString(R.string.route_distance));
				SavePreferences(Utilities.ROUTE_SPEED, getResources().getString(R.string.route_speed));


				txt_time_stats.setText(getResources().getString(R.string.route_time));
				txt_report_distance.setText(getResources().getString(R.string.route_distance));
				txt_report_speed.setText(getResources().getString(R.string.route_speed));

				bottom_timer.setText(getResources().getString(R.string.route_time));
				bottom_txt_distance.setText(getResources().getString(R.string.route_distance));
				bottom_txt_speed.setText(getResources().getString(R.string.route_speed));

				if(mapView != null)
					mapView.getOverlays().clear();
				previousGeoPoint = null;
				sourceLocation = null;
				alert.dismiss();
				isRunning = false;
				btnStart.setVisibility(View.VISIBLE);
				btnStop.setVisibility(View.INVISIBLE);

				analyticsTracker.trackEvent(
						"Discard",  // Category, i.e. Discard Button
						"Button",  // Action, i.e. Discard
						"clicked", // Label    i.e. Discard
						DogUtil.TRACKER_VALUE);   // Value
				DogUtil.TRACKER_VALUE++;
			}
		});

	}

	//Adjusting the model to save battery and data exchange
	//	Reduce the size of the window
	//	Set the location providers to return updates less frequently
	//	Restrict a set of providers

	//Defining a Model for the Best Performance

	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > MINUTES;
		boolean isSignificantlyOlder = timeDelta < -MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	//latitude has a minimum of -90 (south pole) and a maximum of 90 (north pole). 0 degrees latitude is the equator.
	//Longitude has a minimum of -180 (west of the prime meridian) and a maximum of 180 (east of the prime meridian). -180 and 180 are the same line, approached from opposite directions. 0 degrees of longitude runs through London, England and is called the "prime meridian."

	//So you could have -180, -90 through 180,90 as values. At the north and south poles, longitude is essentially meaningless since all lines of longitude meet there and are all equivalent.
	static boolean isnumber = false;

	static NumberFormat nf = NumberFormat.getInstance();

	private static String getNumberOfDecimalPlaces(String  decimal) {
		int index = decimal.indexOf(".");
		return String.valueOf(index < 0 ? 0 : decimal.length() - index - 1);
	}


	public static boolean checkDecimalPlaces(String number) {
		//String[] str = new String[]{"10.20", "123456", "12.invalid"};
		if( number.indexOf(".") > 0 ){
			try	{
				/*
				 * To check if the number is valid decimal number, use
				 * double parseDouble(String str) method of
				 * Double wrapper class.
				 * This method throws NumberFormatException if the
				 * argument string is not a valid decimal number.
				 */

				if(Double.parseDouble(getNumberOfDecimalPlaces(number))>5){
					//System.out.println(number + " is a valid decimal number");
					isnumber = true;
				}
				else
					isnumber = false;
			}
			catch(NumberFormatException nme){
				//System.out.println(number + " is not a valid decimal number");
				isnumber = false;
			}
		}
		else{
			try	{
				/*
				 * To check if the number is valid integer number, use
				 * int parseInt(String str) method of
				 * Integer wrapper class.
				 *
				 * This method throws NumberFormatException if the
				 * argument string is not a valid integer number.
				 */
				int n = Integer.parseInt(number);
				if(n >= -180 && n <= 180)
					isnumber = true;
				else
					isnumber = false;
				//System.out.println(number + " is valid integer number");
			}
			catch(NumberFormatException nme)	{
				//System.out.println(number + " is not a valid integer number");
				isnumber = false;
			}

		}

		return isnumber;
	}

	private class SoapParseLogin extends AsyncTask<String, String, Boolean> {
		private String errorMsg;



		@Override
		protected void onPreExecute() {
			if(statsProgressBar != null){
				statsProgressBar.setVisibility(View.VISIBLE);
			}
		}


		@Override
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
				errorMsg =  getResources().getString(R.string.unable_to_get_data);
				Log.e(DogUtil.DEBUG_TAG, errorMsg);
				return false;
			}


		}

		@Override
        protected void onPostExecute(Boolean b) {
			try{
				if(statsProgressBar != null)
					statsProgressBar.setVisibility(View.INVISIBLE);

				if (reportHandler != null && !reportHandler.getData())
					Toast.makeText(getApplicationContext(), R.string.service_error, Toast.LENGTH_SHORT).show();
				else if(!b){
					Log.e(DogUtil.DEBUG_TAG,"Error: "+errorMsg);
					Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
					return;
				}

				StatsData data = reportHandler.getStatsData();
				txt_time_stats.setText(data.getSecondTime());
				//txt_report_distance.setText(data.getDistance());
				//txt_report_speed.setText(data.getSpeed());

				//if(DogUtil.checkNumber(data.getDistance())){
				txt_report_distance.setText(data.getDistance()+" km");
				//}

				//if(DogUtil.checkNumber(data.getSpeed())){
				txt_report_speed.setText(data.getSpeed()+" (km/h)");
				//}


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
				Log.e(DogUtil.DEBUG_TAG, e.getMessage());
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



	private void loadDefault(){

		String time =  getResources().getString(R.string.route_time);
		String distance = getResources().getString(R.string.route_distance);
		String speed = getResources().getString(R.string.route_speed);


		txt_time_stats.setText(time);
		txt_report_distance.setText(distance);
		txt_report_speed.setText(speed);
	}

}
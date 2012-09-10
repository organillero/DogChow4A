package mx.ferreyra.dogapp;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;


public class ShowMapRoute extends MapActivity implements LocationListener{
	private MapView mapView;
	private double srcLat, srcLong;
	private String routeLat, routeLong, routeId;
	private GeoPoint gp1, gp2;
	private TextView title_txt;
	private Button title_left , title_right; 
	private ImageView title_image;
	private TextView txt_speed, txt_time, txt_distance;
	private LocationManager locationManager;
	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.showmaproute);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);		

		title_left = (Button)findViewById(R.id.tbutton_left);
		title_right = (Button)findViewById(R.id.tbutton_right);
		title_txt = (TextView)findViewById(R.id.title_txt);
		title_txt.setVisibility(View.GONE);

		title_image = (ImageView)findViewById(R.id.title_image);
		title_image.setVisibility(View.VISIBLE);

		title_right.setBackgroundResource(R.drawable.i_ratings_title);
		title_right.setWidth(35);


		title_left.setOnClickListener(new OnClickListener() {

			public void onClick(View v) { 

				finish();
			}
		});
		title_right.setOnClickListener(new OnClickListener() {

			public void onClick(View v) { 
				//Log.d("ShowMap Route", "Ratings "+getIntent().getStringExtra(Utilities.ROUTE_RATINGS));
				//Log.d("ShowMap Route", "ROUTE_ID "+routeId);
				Intent i =new Intent(ShowMapRoute.this,RatingsActivity.class); 
				i.putExtra(Utilities.ROUTE_ID,routeId);
				//i.putExtra(Utilities.ROUTE_NAME, getIntent().getStringExtra(Utilities.ROUTE_NAME));
				i.putExtra(Utilities.ROUTE_RATINGS, getIntent().getStringExtra(Utilities.ROUTE_RATINGS));
				startActivity(i);
				finish(); 
			}
		});

		progressBar = (ProgressBar)findViewById(R.id.progress_map);
		progressBar.setVisibility(View.VISIBLE);

		txt_speed = (TextView)findViewById(R.id.bottom_distance_txt);
		txt_time = (TextView)findViewById(R.id.bottom_time_txt);
		txt_distance = (TextView)findViewById(R.id.bottom_speed_txt);

		//Log.d("ShowMapRoute", "Distance: "+getIntent().getStringExtra(Utilities.ROUTE_DISTANCE));
		//Log.d("ShowMapRoute", "Time "+getIntent().getStringExtra(Utilities.ROUTE_TIMING));
		String time = timeTaken(getIntent().getStringExtra(Utilities.ROUTE_TIMING));
		double distance = 0;
		try{
			distance =Double.parseDouble(getIntent().getStringExtra(Utilities.ROUTE_DISTANCE))/Double.parseDouble(time);
		}catch (NumberFormatException e) {
			e.printStackTrace();
		}
		DecimalFormat df= new DecimalFormat("#0.00000000000");  
		String fdistance = df.format(distance);  
		if(fdistance != null && !fdistance.equals("")){			
			Log.d("ShowMapRoute","fdistance: "+fdistance);
			try{
				double dist = Double.parseDouble(fdistance);
				txt_speed.setText(String.valueOf(dist) + " km");
			}catch (NumberFormatException e) {
				Log.e("ShowMapRoute", "Error in format distance : "+e.getMessage());
			}

			
		}else{
			txt_speed.setText(fdistance+" km");
		}

		txt_time.setText(time);
		txt_distance.setText(getIntent().getStringExtra(Utilities.ROUTE_DISTANCE) + " (km/h)");		

		mapView = (MapView) findViewById(R.id.myMap);
		mapView.getController().setZoom(18);
		mapView.setBuiltInZoomControls(true);

		double startLat = 0l, startLong=0l;
		try {

			if (getIntent() != null) {
				routeId = getIntent().getStringExtra(Utilities.ROUTE_ID);
				srcLat = Double.parseDouble(getIntent().getStringExtra(Utilities.INTENT_SOURCE_LATITUDE));
				srcLong = Double.parseDouble(getIntent().getStringExtra(Utilities.INTENT_SOURCE_LONGITUDE));
				routeLat = getIntent().getStringExtra(Utilities.INTENT_ROUTE_LATITUDE);
				routeLong = getIntent().getStringExtra(Utilities.INTENT_ROUTE_LONGITUDE);
			}

			gp1 = new GeoPoint((int) (srcLat * 1E6), (int) (srcLong * 1E6));
			mapView.getController().animateTo(gp1);
			mapView.getOverlays().add(new MyOverLay(gp1, gp2, 1, Color.RED));
			String[] latArray = routeLat.split(",");
			String[] longArray = routeLong.split(",");			

			int minLatLon = Math.min(latArray.length, longArray.length);
			for (int i = 0; i < minLatLon; i++) {
				if(latArray[i]== null || latArray[i].equals("") || longArray[i]== null || longArray[i].equals(""))
					continue;				
				if(TrackMapRoute.checkDecimalPlaces(latArray[i]) && TrackMapRoute.checkDecimalPlaces(longArray[i])){
					if (i == 0) {
						gp2 = new GeoPoint((int) (Double.parseDouble(latArray[i]) * 1E6), (int) (Double.parseDouble(longArray[i]) * 1E6));
					} else {
						gp1 = gp2;
						gp2 = new GeoPoint((int) (Double.parseDouble(latArray[i]) * 1E6), (int) (Double.parseDouble(longArray[i]) * 1E6));					
					}
					if(gp1 == null || gp2 == null)
						continue;

					if(startLat <=0L && startLong<= 0L){
						startLat = Double.parseDouble(latArray[i]);
						startLong = Double.parseDouble(longArray[i]);
						mapView.getOverlays().add(new MyOverLay(gp1, gp2, 2, Color.GREEN));
					}else{
						if(!(startLat == Double.parseDouble(latArray[i]) && startLong== Double.parseDouble(longArray[i]) )){
							mapView.getOverlays().add(new MyOverLay(gp1, gp2, 2, Color.BLUE));
						}
					}
				}
			}

		}catch (NumberFormatException e) {
			Log.e(this.getClass().getSimpleName(), "Error message: "+e.getMessage());
		}

		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				if(progressBar != null)
					progressBar.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {

				if(progressBar != null)
					progressBar.setVisibility(View.INVISIBLE);

				GeoPoint geoPoint = new GeoPoint((int) location.getLatitude() * 1000000, (int) location.getLongitude() * 1000000);
				mapView.getController().animateTo(geoPoint);
				MyLocationOverlay locationOverlay = new MyLocationOverlay(geoPoint);
				List<Overlay> list = mapView.getOverlays();
				list.add(locationOverlay);

				locationManager.removeUpdates(this);



			}
		});

		if(progressBar != null)
			progressBar.setVisibility(View.INVISIBLE);
	}


	class MyLocationOverlay extends com.google.android.maps.Overlay {

		GeoPoint in;
		MyLocationOverlay(GeoPoint geoPoint){
			in = geoPoint;
		}
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {

			super.draw(canvas, mapView, shadow);
			Paint paint = new Paint();
			Point myScreenCoords = new Point();
			mapView.getProjection().toPixels(in, myScreenCoords);
			paint.setStrokeWidth(1);
			paint.setARGB(255, 255, 255, 255);
			paint.setStyle(Paint.Style.STROKE);	
			paint.setStrokeWidth(Color.GRAY);
			canvas.drawCircle(myScreenCoords.x, myScreenCoords.y, 5, paint);
			return true;

		} 
	} 
	
	private String timeTaken(String time){
		try {
			if(time.contains(":")){
				return trimLeadingZeros(time.substring(0, time.indexOf(":")));				
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



	public class MyOverLay extends Overlay {
		private GeoPoint gp1;
		private GeoPoint gp2;
		private int mRadius = 3;
		private int mode = 0;
		private int defaultColor;
		private String text = "";
		private Bitmap img = null;

		public MyOverLay(GeoPoint gp1, GeoPoint gp2, int mode) // GeoPoint is a
		{
			this.gp1 = gp1;
			this.gp2 = gp2;
			this.mode = mode;
			defaultColor = 999; // no defaultColor
		}

		public MyOverLay(GeoPoint gp1, GeoPoint gp2, int mode, int defaultColor) {
			this.gp1 = gp1;
			this.gp2 = gp2;
			this.mode = mode;
			this.defaultColor = defaultColor;

		}

		public void setText(String t) {
			this.text = t;
		}

		public void setBitmap(Bitmap bitmap) {
			this.img = bitmap;
		}

		public int getMode() {
			return mode;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			Projection projection = mapView.getProjection();
			if (shadow == false) {
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				Point point = new Point();
				projection.toPixels(gp1, point);
				if (mode == 1) {
					if (defaultColor == 999)
						paint.setColor(Color.BLUE);
					else
						paint.setColor(defaultColor);
					RectF oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y + mRadius);
					canvas.drawOval(oval, paint);
				}
				else if (mode == 2) {
					if (defaultColor == 999)
						paint.setColor(Color.RED);
					else
						paint.setColor(defaultColor);
					Point point2 = new Point();
					projection.toPixels(gp2, point2);
					paint.setStrokeWidth(10);
					paint.setAlpha(120);
					canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
				}
				else if (mode == 3) {

					if (defaultColor == 999)
						paint.setColor(Color.GREEN);
					else
						paint.setColor(defaultColor);
					Point point2 = new Point();
					projection.toPixels(gp2, point2);
					paint.setStrokeWidth(5);
					paint.setAlpha(120);
					canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
					RectF oval = new RectF(point2.x - mRadius, point2.y - mRadius, point2.x + mRadius, point2.y + mRadius);
					paint.setAlpha(255);
					canvas.drawOval(oval, paint);
				}
			}
			super.draw(canvas, mapView, shadow);
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}

package mx.ferreyra.dogmap;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mx.ferreyra.dogapp.R;
import mx.ferreyra.dogapp.DogUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;



public class MapDirectionActivity extends MapActivity {
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
    	super.onDestroy();
	}

	MapView myMapView = null;
    MapController myMC = null; 
    GeoPoint geoPoint = null;
    OverlayItem source, destination;
   static String srcAdd, desAdd, time, distance, money, srcLat, desLat;
    TextView txtSrc, txtDes, txtDistance, txtMoney;
    Drawable drawable;
    Itemization itemOverLay;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapdirection);
        Intent in = getIntent();
        if(in != null)
        {
        	srcAdd = in.getStringExtra("srcAd");
        	desAdd = in.getStringExtra("desAd");
        	time = in.getStringExtra("time");
        	distance = in.getStringExtra("distanceBykm");
        	money = in.getStringExtra("money");
        	srcLat = in.getStringExtra("srcAdlat");
        	desLat = in.getStringExtra("desAdlat");
        }
        System.out.println(distance+" Distance "+money+" Money");
        Log.i(DogUtil.DEBUG_TAG, srcLat + "and"+ desLat);
        txtDes = (TextView) findViewById(R.id.txt_desAdd);
        txtDistance = (TextView) findViewById(R.id.txt_distance);
        txtMoney = (TextView) findViewById(R.id.txt_money);
        txtSrc = (TextView) findViewById(R.id.txt_srcAdd);
        
        txtDes.setText("TO: "+desAdd);
        txtDistance.setText("DISTANCE: "+distance+" KM");
        txtSrc.setText("FROM: "+srcAdd);
        txtMoney.setText("FARE ESTIMATE: "+ money+" "+time);
        
        drawable = this.getResources().getDrawable(R.drawable.icon0);
        itemOverLay = new Itemization(drawable);
        
        myMapView = (MapView) findViewById(R.id.mapview);
        geoPoint = null;
        myMapView.setSatellite(false);

        String pairs[] = getDirectionData(srcLat, desLat);
        System.out.println("getDirectionData "+pairs[0]);
        String[] lngLat = pairs[0].split(",");

        // STARTING POINT
        GeoPoint startGP = new GeoPoint(
                (int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
                        .parseDouble(lngLat[0]) * 1E6));

        myMC = myMapView.getController();
        geoPoint = startGP;
        myMC.setCenter(geoPoint);
        myMC.setZoom(15);
        myMapView.getOverlays().add(new DirectionPathOverlay(startGP, startGP));
        
        // NAVIGATE THE PATH
        GeoPoint gp1;
        GeoPoint gp2 = startGP;
        itemOverLay.addOverlay(new OverlayItem(gp2, "Start: " + srcAdd, ""));
        
        for (int i = 1; i < pairs.length; i++) {
            lngLat = pairs[i].split(",");
            gp1 = gp2;
            // watch out! For GeoPoint, first:latitude, second:longitude
            gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6),
                    (int) (Double.parseDouble(lngLat[0]) * 1E6));
            myMapView.getOverlays().add(new DirectionPathOverlay(gp1, gp2));
            Log.d(DogUtil.DEBUG_TAG, "pair:" + pairs[i]);
        }

        // END POINT
        myMapView.getOverlays().add(new DirectionPathOverlay(gp2, gp2));
        itemOverLay.addOverlay(new OverlayItem(gp2, "End: "+desAdd, ""));
        myMapView.getController().animateTo(startGP);
        myMapView.setBuiltInZoomControls(true);
        myMapView.displayZoomControls(true);
        myMapView.getOverlays().add(itemOverLay);
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

    private String[] getDirectionData(String srcPlace, String destPlace) {

        /*String urlString = "http://maps.google.com/maps?f=d&hl=en&saddr="
                + srcPlace + "&daddr=" + destPlace
                + "&ie=UTF8&0&om=0&output=kml";
        */
       String urlString =  "http://maps.google.com/maps?output=kml&saddr=" + srcPlace + "&daddr=" + destPlace;
//       http://maps.google.com/maps?output=kml&saddr=chennai&daddr=bangalore
        Log.d(DogUtil.DEBUG_TAG, urlString);
        Document doc = null;
        HttpURLConnection urlConnection = null;
        URL url = null;
        String pathConent = "";
        try {

            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(urlConnection.getInputStream());

        } catch (Exception e) {
        }

        NodeList nl = doc.getElementsByTagName("LineString");
        for (int s = 0; s < nl.getLength(); s++) {
            Node rootNode = nl.item(s);
            NodeList configItems = rootNode.getChildNodes();
            for (int x = 0; x < configItems.getLength(); x++) {
                Node lineStringNode = configItems.item(x);
                NodeList path = lineStringNode.getChildNodes();
                pathConent = path.item(0).getNodeValue();
                Log.i(DogUtil.DEBUG_TAG, pathConent);
            }
        }
        String[] tempContent = pathConent.split(" ");
        return tempContent;
    }

    
    public class DirectionPathOverlay extends Overlay {
        private GeoPoint gp1;
        private GeoPoint gp2;
        public DirectionPathOverlay(GeoPoint gp1, GeoPoint gp2) {
            this.gp1 = gp1;
            this.gp2 = gp2;
        }

        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
                long when) {
            // TODO Auto-generated method stub
            Projection projection = mapView.getProjection();
            if (shadow == false) {
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                Point point = new Point();
                
                projection.toPixels(gp1, point);
                paint.setColor(Color.BLUE);
                Point point2 = new Point();
                projection.toPixels(gp2, point2);
                paint.setStrokeWidth(2);
                canvas.drawLine((float) point.x, (float) point.y, (float) point2.x,
                        (float) point2.y, paint);
            }
            return super.draw(canvas, mapView, shadow, when);
        }

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            // TODO Auto-generated method stub

            super.draw(canvas, mapView, shadow);
        } 
    }
    
    public class Itemization extends ItemizedOverlay<OverlayItem> {

        private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

        public Itemization(Drawable defaultMarker) {

            super(boundCenterBottom(defaultMarker));
            // super(defaultMarker);

        }

        @Override
        protected OverlayItem createItem(int i) {
            return mOverlays.get(i);
        }

        public void addOverlay(OverlayItem overlay) {
            mOverlays.add(overlay);
            populate();
        }

        @Override
        public int size() {
            return mOverlays.size();
        }
        
        @Override
        protected boolean onTap(int pIndex) {
           Toast.makeText(MapDirectionActivity.this, mOverlays.get(pIndex).getTitle(),
                Toast.LENGTH_LONG).show();
           return true;
        }
    }
}
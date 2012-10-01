package mx.ferreyra.dogapp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import mx.ferreyra.dogapp.map.MyMarkerLayer;
import mx.ferreyra.dogapp.map.MyOverlayItem;
import mx.ferreyra.dogapp.pojos.FotosMascotaByLatLonResponse;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;


public class MapNearDogPhotos extends MapActivity {

    private MapView mapView;
    private MapController mc;
    private MyLocationOverlay myLocationOverlay;
    private LocationManager locationManager;
    private GeoUpdateHandler locationListener;

    private Drawable drawable;

    private Context context;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        context = this;

        setContentView(R.layout.my_map);

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = new GeoUpdateHandler();

        mapView = (MapView) findViewById(R.id.mapView);
        mc = mapView.getController();

        GeoPoint geoPoint = new GeoPoint(19426900, -99182200);
        mc.animateTo(geoPoint);
        mc.setZoom(20);
        mapView.invalidate();

        this.myLocationOverlay = new MyLocationOverlay(this, mapView);

        drawable = context.getResources().getDrawable(android.R.drawable.btn_star);

        new MyAsyncTask().execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // when our activity resumes, we want to register for location updates
        myLocationOverlay.enableMyLocation();
        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10*60000, 10, this.locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when our activity pauses, we want to remove listening for location updates
        myLocationOverlay.disableMyLocation();
        this.locationManager.removeUpdates(this.locationListener);
    }


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }


    private class MyAsyncTask extends AsyncTask<Void, Void, List<FotosMascotaByLatLonResponse>> {


        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress =ProgressDialog.show(context, "Espere de favor", "Actualizando informaci—n", true, false);

        }

        @Override
        protected List<FotosMascotaByLatLonResponse> doInBackground(Void... arg0) {


            String bestProvider;
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            bestProvider = locationManager.getBestProvider(criteria, true);      
            Location location = locationManager.getLastKnownLocation(bestProvider);

            List<FotosMascotaByLatLonResponse> ans = null;

            WsDogUtils dogUtils = new WsDogUtils();

            try {
                ans = dogUtils.fotosMascotaByLatLonToPojo( dogUtils.getFotosMascotaByLatLon (location.getLatitude(), location.getLongitude(), 20 ) );
            } catch (Exception e) {
                e.printStackTrace();
                ans = null;
            } 

            return ans;
        }


        @Override
        protected void onPostExecute(List<FotosMascotaByLatLonResponse> fotosMascotas) {
            super.onPostExecute(fotosMascotas);
            progress.dismiss();
            if(fotosMascotas != null && fotosMascotas.size() > 0){     

                MyMarkerLayer mml = new MyMarkerLayer(drawable, mapView);

                for ( FotosMascotaByLatLonResponse fotoMascota : fotosMascotas){

                    Double lat = ((Double) Double.valueOf(fotoMascota.getLatitude())*1E6);
                    Double lng = Double.valueOf(fotoMascota.getLongitude())*1E6;

                    MyOverlayItem overlayItem = new MyOverlayItem(new GeoPoint(lat.intValue(), lng.intValue()), "Reporte " , "Inicio");
                    overlayItem.setMarker(getDrawable (fotoMascota.getPhotoAsBase64Binary()));

                    overlayItem.setObject(fotoMascota);
                    mml.addOverlayItem(overlayItem);
                }

                List<Overlay> mapOverlays = mapView.getOverlays();
                mapOverlays.add(mml);
                mapView.invalidate();


            }

        }


        //TODO kill me
        Drawable getDrawable (String str){
            byte[] bytes = Base64.decode(str, Base64.DEFAULT);
            InputStream is = new ByteArrayInputStream(bytes);
            Bitmap bmp = BitmapFactory.decodeStream(is);

            Drawable icon = new BitmapDrawable(getResources(),bmp);

            icon.setBounds(
                    0 - icon.getIntrinsicWidth() / 2, 0 - icon.getIntrinsicHeight(), 
                    icon.getIntrinsicWidth() / 2, 0);

            return  icon;
        }


    }


    public class GeoUpdateHandler implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            int lat = (int) (location.getLatitude() * 1E6);
            int lng = (int) (location.getLongitude() * 1E6);
            GeoPoint point = new GeoPoint(lat, lng);
            mc.animateTo(point);
            mapView.invalidate();
        }

        @Override
        public void onProviderDisabled(String provider) {
            /**/
        }

        @Override
        public void onProviderEnabled(String provider) {
            /**/
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            /**/
        }
    }

}

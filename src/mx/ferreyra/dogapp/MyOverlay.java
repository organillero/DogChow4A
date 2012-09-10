package mx.ferreyra.dogapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


class MyOverlay extends com.google.android.maps.Overlay {

	GeoPoint p = null ;
	public MyOverlay() {
	}
	
	MyOverlay(GeoPoint geoPoint){
		p = geoPoint;
	}
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);
		Paint paint = new Paint();
		// Converts lat/lng-Point to OUR coordinates on the screen.
		Point myScreenCoords = new Point();
		if(p!= null)
			mapView.getProjection().toPixels(p, myScreenCoords);
		paint.setStrokeWidth(1);
		paint.setARGB(255, 255, 255, 255);
		paint.setStyle(Paint.Style.STROKE);
		Bitmap bmp = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.drawable.ic_maps_indicator_current_position);
		canvas.drawBitmap(bmp, myScreenCoords.x, myScreenCoords.y, paint);
		return true;
	}


}

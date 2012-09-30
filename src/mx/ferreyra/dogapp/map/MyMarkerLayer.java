package mx.ferreyra.dogapp.map;

import java.util.ArrayList;

import mx.ferreyra.dogapp.ShowDogPhoto;
import mx.ferreyra.dogapp.pojos.FotosMascotaByLatLonResponse;
import mx.ferreyra.dogapp.ui.UI;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;


public class MyMarkerLayer extends ItemizedOverlay {

    private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
    private Context context;
    
    public MyMarkerLayer(Drawable defaultMarker) { 

        super(boundCenterBottom(defaultMarker));
        populate();
    }
    
    public MyMarkerLayer(Drawable defaultMarker, MapView mapView) {
        super(boundCenter(defaultMarker));
        context  = mapView.getContext();
    }
    
    public void addOverlayItem(MyOverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }
    
    @Override
    protected boolean onTap(int index) {

        
        FotosMascotaByLatLonResponse foto  = (FotosMascotaByLatLonResponse) mOverlays.get(index).getObject();
        
        Intent intent = new Intent (context, ShowDogPhoto.class);
        intent.putExtra(ShowDogPhoto.PHOTO_ID, foto.getPhotoId().intValue());
        context.startActivity(intent);
        
        //UI.showToast("ok, idFoto: " + foto.getPhotoId().toString() , context);
        return super.onTap(index);
    }
}

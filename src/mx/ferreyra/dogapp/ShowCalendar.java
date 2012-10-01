package mx.ferreyra.dogapp;

import java.util.Date;
import java.util.List;

import mx.ferreyra.dogapp.pojos.FotosMascotaByLatLonResponse;
import mx.ferreyra.dogapp.pojos.FotosMascotaByUsuarioMesAnoResponse;
import mx.ferreyra.dogapp.recursos.Recursos;
import mx.ferreyra.dogapp.ui.UI;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class ShowCalendar extends Activity {


    private View activityRootView;
    private Context context;
    private GridView gridview;

    private Button calMonth;
    private Button calYear;

    public ImageAdapter adapter=null;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_show_calendar);

        activityRootView = findViewById(R.id.activityRootView);

        gridview = (GridView) findViewById(R.id.gridview);
        calMonth = (Button) findViewById(R.id.cal_month);
        calYear = (Button) findViewById(R.id.cal_year);
        
        adapter =  new ImageAdapter(this);
        gridview.setAdapter(adapter);



        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                UI.showToast("pos." + position, context);

            }
        });

    }


    private int month = -1;
    private int year = -1;

    @SuppressLint("NewApi")
    public void  onClickCalMonthButton (View v){

        AlertDialog.Builder builder;
        checkAndHideKeyboard(null);
        builder = Build.VERSION.SDK_INT>=11 ?
                new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT) :
                    new AlertDialog.Builder(context);
                builder.setTitle("Mes");
                builder.setSingleChoiceItems(Recursos.MONTHS_CAL,  year, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        calMonth.setHint(Recursos.MONTHS_CAL[item]);
                        month = item;
                        dialog.dismiss();
                    }
                });
                builder.create().show();


    }

    @SuppressLint("NewApi")
    public void onClickCalYearButton (View v){

        AlertDialog.Builder builder;
        checkAndHideKeyboard(null);
        builder = Build.VERSION.SDK_INT>=11 ?
                new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT) :
                    new AlertDialog.Builder(context);
                builder.setTitle("A–o");
                builder.setSingleChoiceItems(Recursos.DOG_YEARS,  year, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        calYear.setHint(Recursos.DOG_YEARS[item]);
                        year = item;
                        dialog.dismiss();
                    }
                });
                builder.create().show();



    }

    public void  onClickConsultCalButton (View v){
        new MyCalAsyncTask().execute();
    } 


    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(80, 80));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(2, 2, 2, 2);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7
        };
    }


    private class MyCalAsyncTask extends AsyncTask<Void, Void,  List<FotosMascotaByUsuarioMesAnoResponse>> {


        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress =ProgressDialog.show(context, "Espere de favor", "Actualizando informaci—n", true, false);

        }

        @Override
        protected List<FotosMascotaByUsuarioMesAnoResponse> doInBackground(Void... arg0) {


            String bestProvider;
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            bestProvider = locationManager.getBestProvider(criteria, true);      
            Location location = locationManager.getLastKnownLocation(bestProvider);

            List<FotosMascotaByUsuarioMesAnoResponse> ans = null;

            WsDogUtils dogUtils = new WsDogUtils();

            Integer userId = DogUtil.getInstance().getCurrentUserId();
            Date date= new Date();

            userId = 10;
            
            try {
                ans = dogUtils.fotosMascotaByUsuarioMesAnoToPojo(dogUtils.getFotosMascotaByUsuarioMesAno( userId.toString(), "2012-09-01"));
            } catch (Exception e) {
                e.printStackTrace();
                ans = null;
            } 

            return ans;
        }


        @Override
        protected void onPostExecute( List<FotosMascotaByUsuarioMesAnoResponse> fotosMascotas) {
            super.onPostExecute(fotosMascotas);
            progress.dismiss();


            //adapter.add;
            
        }
    }

    public void checkAndHideKeyboard(View view){
        if(getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activityRootView.getWindowToken(), 0);
        }
    }


}

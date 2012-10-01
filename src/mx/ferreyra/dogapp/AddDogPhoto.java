package mx.ferreyra.dogapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mx.ferreyra.dogapp.pojos.FotosMascotaByUsuarioMesAnoResponse;
import mx.ferreyra.dogapp.ui.UI;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AddDogPhoto extends Activity {

    private Bitmap dogImage;
    private ImageView dogPhoto;
    private EditText dogPhotoFoot;
    private ImageButton btRemoveImage;
    private LocationManager locationManager;

    private Date date;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog_photo);

        // Load location service
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Load view controls
        dogPhoto     = (ImageView)findViewById(R.id.dog_photo);
        dogPhotoFoot = (EditText)findViewById(R.id.dog_photo_foot);

        btRemoveImage = (ImageButton) findViewById(R.id.remove_dog_photo_btn);


        // Check if parameters
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            FotosMascotaByUsuarioMesAnoResponse pojo = (FotosMascotaByUsuarioMesAnoResponse)extras.get("FOTO_MASCOTA");
            String strDate  = (String)extras.get("DATE");
            if(pojo != null) {
                
                dogImage = pojo.getImagen();
                dogPhoto.setImageBitmap( dogImage);
                date = pojo.getDate();
            }
            else if (strDate != null){
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }//new Date(strDate);
            }
            
        }




    }



    //TODO killme

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

    public void onClickDogPhotoButton(View view) {
        String[] items = {
                getString(R.string.take_from_images_stored),
                getString(R.string.take_from_camera)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.take_image));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0)
                    getPhotofromAlbum();
                else if (item == 1)
                    takePhoto();
            }
        });
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onClickRemoveDogPhotoButton(View view) {
        // TODO finish and verify this method
        dogImage = null;
        dogPhoto.setImageResource(R.drawable.bg_avatar_camera);
        btRemoveImage.setVisibility(View.INVISIBLE);

    }

    public void onClickAddPhotoButton(View view) {
        // Validate form
        if(!isValidForm())
            return;

        // Invoke webservices using asynctask
        AddDogPhotoTask task = new AddDogPhotoTask(this);
        String[] params = viewToArray();
        task.execute(params);
    }

    public String[] viewToArray() {
        // Dog Image
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        dogImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String encodedImageStr = Base64.encodeToString(byteArray,Base64.DEFAULT);

        // Dog photo foot
        String foot = dogPhotoFoot.getText().toString();

        return new String[] {
                encodedImageStr,
                foot
        };
    }

    private Location getLocation() {
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        return location;
    }

    private void getPhotofromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void takePhoto() {
        final String action = MediaStore.ACTION_IMAGE_CAPTURE;
        Intent intent = new Intent(action);
        List<ResolveInfo> list = this.getPackageManager()
                .queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if(list.size() > 0)
            startActivityForResult(new Intent(action), 0 );
    }

    private boolean isValidForm() {
        // Check selected image
        if(dogImage == null) {
            UI.showAlertDialog(getString(R.string.validation_alert_dialog_title),
                    getString(R.string.dog_image_not_selected_dialog_message),
                    getString(android.R.string.ok), this, null);
            return false;
        }

        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Bitmap mBitmap = null;
        if (resultCode == Activity.RESULT_OK && intent != null){
            if(requestCode == 0)
                mBitmap = (Bitmap)intent.getExtras().get("data");
            else if(requestCode == 1){
                try {
                    mBitmap = Media.getBitmap(getContentResolver(), intent.getData());
                }catch(FileNotFoundException e) {
                    Log.e(DogUtil.DEBUG_TAG, e.getMessage(), e);
                } catch (IOException e) {
                    Log.e(DogUtil.DEBUG_TAG, e.getMessage(), e);
                }
            }

            if (mBitmap != null){
                dogImage = UtilsBitmap.resize(mBitmap, 100, 100);
                dogPhoto.setImageBitmap(dogImage);
                btRemoveImage.setVisibility(View.VISIBLE);
            }
        }
    }



    private class AddDogPhotoTask extends AsyncTask<String, Integer, Integer> {

        private final Context context;
        private ProgressDialog dialog;

        public AddDogPhotoTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.please_wait_signing_up));
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            WsDogUtils wsDogUtils = new WsDogUtils();
            try {
                Integer userId = DogUtil.getInstance().getCurrentUserId();

                Integer result = wsDogUtils.insertFotoMascota(userId,
                        date, params[0], getLocation().getLatitude(),
                        getLocation().getLongitude(), params[1], null);
                return result;
            } catch(XmlPullParserException e) {
                Log.e(DogUtil.DEBUG_TAG, e.getMessage(), e);
                return null;
            } catch(IOException e) {
                Log.e(DogUtil.DEBUG_TAG, e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            // Stop and hide dialog
            dialog.dismiss();

            // Check result
            if(result == null || result < 0) {
                // Something wrong happened
            } else {
                // Notify successful process
            }
        }
    }
}

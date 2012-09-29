package mx.ferreyra.dogapp;

import java.util.List;

import mx.ferreyra.dogapp.ui.UI;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

public class AddDogPhoto extends Activity {

    private Bitmap dogImage;
    private ImageView dogPhoto;

    // Intent results
    private final int ADD_PHOTO_FROM_STORAGE = 0x01;
    private final int ADD_PHOTO_FROM_CAMARA = 0x00;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog_photo);

        // Load view controls
        dogPhoto = (ImageView)findViewById(R.id.dog_photo);
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
    }

    public void onClickAddPhotoButton(View view) {
        // Validate form
        if(!isValidForm())
            return;

        // TODO implement this method
    }

    private void getPhotofromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, ADD_PHOTO_FROM_STORAGE);
    }

    private void takePhoto() {
        final String action = MediaStore.ACTION_IMAGE_CAPTURE;
        Intent intent = new Intent(action);
        List<ResolveInfo> list = this.getPackageManager()
                                     .queryIntentActivities(intent,
                                                            PackageManager.MATCH_DEFAULT_ONLY);
        if(list.size() > 0)
            startActivityForResult(new Intent(action), ADD_PHOTO_FROM_CAMARA );
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
}

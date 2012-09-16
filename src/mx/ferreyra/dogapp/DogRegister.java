package mx.ferreyra.dogapp;

import java.util.List;

import mx.ferreyra.dogapp.recursos.Recursos;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class DogRegister extends Activity {
	
	static final int DATE_DIALOG_ID = 0;
	
	private View activityRootView;
	protected Activity context;
	
	//Variables internas para el control del registro
	//Vistas  del perro
	private Button dogGenderField;
	private Button dogLifeStyleField;
	private Button dogActivityField;
	private ImageView dogPhoto;
	private Button dogBirthday;
	
	
	//Vistas del dueno
	private Button ownerGenderField;
	private Button ownerStateField;
	
	
	
	
	private int dogGender = -1;
	private int dogLifeStyle =-1;
	private int dogActivity =-1;
	
	//vars. fec. nacimiento
	private int dogYear = 0;
	private int dogMonth = 0;
	private int dogDay = 0;
	
	
	private int ownerGender = -1;
	private int ownerState = -1;
	
	//vars. fec. nacimiento
	private int ownerYear = 0;
	private int ownergMonth = 0;
	private int ownerDay = 0;
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_dog);
		
		context = this;
		activityRootView = findViewById(R.id.activityrootview);
		activityRootView.setOnClickListener(checkAndHideKeyboardListener);
		
		
		//Vistas del pero
		dogGenderField = (Button) findViewById(R.id.dog_gender_field);
		dogLifeStyleField = (Button) findViewById(R.id.dog_life_style_field);
		dogActivityField = (Button) findViewById(R.id.dog_activity_field);
		dogPhoto = (ImageView) findViewById(R.id.dog_photo);
		dogBirthday = (Button) findViewById(R.id.dog_birthday);
		
		
		//Vistas del dueno
		ownerGenderField = (Button) findViewById(R.id.owner_gender_field);
		ownerStateField = (Button) findViewById(R.id.owner_state);
		
		
		
		
		
		//perro
		dogGenderField.setOnClickListener(this.displayDialogDogGender);
		dogLifeStyleField.setOnClickListener(this.displayDialogDogLifeStyle);
		dogActivityField.setOnClickListener(this.displayDialogDogActivity);
		dogPhoto.setOnClickListener(this.photoListener);
		dogBirthday.setOnClickListener(this.showDatePicker);
		
		//dueno
		ownerGenderField.setOnClickListener(this.displayDialogOwnerGender);
		ownerStateField.setOnClickListener(this.displayDialogOwnerState);
		
	};

	
	
	/*
	 * Listener de los botones que tienen funion de spinner para no tener una opci—n ya selecionada desde un princicpio
	 * */
	

	private OnClickListener displayDialogDogGender = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int version=Build.VERSION.SDK_INT;
			AlertDialog.Builder builder;
			checkAndHideKeyboard(null);
			if(version>=11){
				Log.d("Version", "Cambio de tema");
				builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
			}
			else
				builder = new AlertDialog.Builder(context);
			builder.setTitle("Genero");
			builder.setSingleChoiceItems(Recursos.GENDER,  dogGender, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {		    	
					//Registro.this.tvSexo.setText(Recursos.GENDER[item]);
					dogGenderField.setHint(Recursos.GENDER[item]);
					dogGender = item;
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

	};
	
	private OnClickListener displayDialogDogLifeStyle = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int version=Build.VERSION.SDK_INT;
			AlertDialog.Builder builder;
			checkAndHideKeyboard(null);
			if(version>=11){
				Log.d("Version", "Cambio de tema");
				builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
			}
			else
				builder = new AlertDialog.Builder(context);
			builder.setTitle("Tipo de Vida");
			builder.setSingleChoiceItems(Recursos.LIFE_STYLE,  dogLifeStyle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {		    	
					//Registro.this.tvSexo.setText(Recursos.GENDER[item]);
					dogLifeStyleField.setHint(Recursos.LIFE_STYLE[item]);
					dogLifeStyle = item;
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

	};
	
	private OnClickListener displayDialogDogActivity = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int version=Build.VERSION.SDK_INT;
			AlertDialog.Builder builder;
			checkAndHideKeyboard(null);
			if(version>=11){
				Log.d("Version", "Cambio de tema");
				builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
			}
			else
				builder = new AlertDialog.Builder(context);
			builder.setTitle("Actividad F’sica");
			builder.setSingleChoiceItems(Recursos.ACTIVITY,  dogActivity, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {		    	
					//Registro.this.tvSexo.setText(Recursos.GENDER[item]);
					dogActivityField.setHint(Recursos.ACTIVITY[item]);
					dogActivity = item;
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

	};
	
	
	private OnClickListener showDatePicker = new View.OnClickListener (){

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			showDialog(DATE_DIALOG_ID);
		}

	};
	
	
	
	private OnClickListener displayDialogOwnerGender = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int version=Build.VERSION.SDK_INT;
			AlertDialog.Builder builder;
			checkAndHideKeyboard(null);
			if(version>=11){
				Log.d("Version", "Cambio de tema");
				builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
			}
			else
				builder = new AlertDialog.Builder(context);
			builder.setTitle("Genero");
			builder.setSingleChoiceItems(Recursos.GENDER_OWNER,  ownerGender, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {		    	
					//Registro.this.tvSexo.setText(Recursos.GENDER[item]);
					ownerGenderField.setHint(Recursos.GENDER_OWNER[item]);
					ownerGender = item;
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

	};
	
	
	
	private OnClickListener displayDialogOwnerState = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int version=Build.VERSION.SDK_INT;
			AlertDialog.Builder builder;
			checkAndHideKeyboard(null);
			if(version>=11){
				Log.d("Version", "Cambio de tema");
				builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
			}
			else
				builder = new AlertDialog.Builder(context);
			builder.setTitle("Estado");
			builder.setSingleChoiceItems(Recursos.STATES,  ownerState, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {		    	
					//Registro.this.tvSexo.setText(Recursos.GENDER[item]);
					ownerStateField.setHint(Recursos.STATES[item]);
					ownerState = item;
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

	};
	
	
	android.view.View.OnClickListener photoListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			getPhoto(v);
		}
	};
	
	public void getPhoto (View view){

		final CharSequence[] items = {"Desde mis imagenes", "Tomar una foto"};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Tomar Fotograf’a");
		builder.setItems(items, new DialogInterface.OnClickListener() {
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
	
	
	private void getPhotofromAlbum(){

		int actionCode = 1;
		final String action = Intent.ACTION_GET_CONTENT;

		Intent intent = new Intent(action);  
		intent.setType("image/*");
		startActivityForResult(intent, actionCode);


	}

	private void takePhoto(){

		int actionCode = 0;
		final String action = MediaStore.ACTION_IMAGE_CAPTURE;

		if (isIntentAvailable(context, action)){
			Intent takePictureIntent = new Intent(action);
			startActivityForResult(takePictureIntent, actionCode);
		}
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		Bitmap mBitmap = null;
		if (resultCode == Activity.RESULT_OK && intent != null){
			if (requestCode == 0 ){
				Bundle extras = intent.getExtras();
				mBitmap = (Bitmap) extras.get("data");

			}
			else if (requestCode == 1){
				Uri chosenImageUri = intent.getData();
				try { mBitmap = Media.getBitmap(context.getContentResolver(), chosenImageUri);}
				catch (Exception e) {e.printStackTrace();}
			}

			if (mBitmap != null){
				dogPhoto.setImageBitmap(mBitmap);
				//this.avatar = saveImageToInternalStorage(mBitmap);
			}
		}
	}
	
	
	android.view.View.OnClickListener checkAndHideKeyboardListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			checkAndHideKeyboard(v);
		}
	};
	
	public void checkAndHideKeyboard (View view){

		if(getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO){
			InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activityRootView.getWindowToken(), 0);
		}

		return;
	}
	
}

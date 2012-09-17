package mx.ferreyra.dogapp;

import java.util.List;

import mx.ferreyra.dogapp.fragments.DatePickerFragment;
import mx.ferreyra.dogapp.fragments.DatePickerFragment.MyDate;
import mx.ferreyra.dogapp.recursos.Recursos;
import mx.ferreyra.dogapp.ui.UI;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class DogRegister extends FragmentActivity {

	static final int DATE_DIALOG_ID = 0;

	private View activityRootView;
	protected Activity context;

	//Variables internas para el control del registro
	//Vistas  del perro

	private EditText dogNameField;
	private EditText dogBreedField;

	private Button dogGenderField;
	private Button dogLifeStyleField;
	private Button dogActivityField;
	private ImageView dogPhoto;
	private Button dogBirthday;


	//Vistas del dueno

	private EditText ownerNameField;
	private Button ownerGenderField;
	private Button ownerStateField;
	private Button ownerBirthDay;




	private Button btSend;

	private int dogGender = -1;
	private int dogLifeStyle =-1;
	private int dogActivity =-1;

	//vars. fec. nacimiento
	private int dogYear = -1;
	private int dogMonth = -1;
	private int dogDay = -1;


	private int ownerGender = -1;
	private int ownerState = -1;

	//vars. fec. nacimiento
	private int ownerYear = -1;
	private int ownerMonth = -1;
	private int ownerDay = -1;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_dog);

		context = this;
		activityRootView = findViewById(R.id.activityrootview);
		activityRootView.setOnClickListener(checkAndHideKeyboardListener);


		//Vistas del pero
		dogNameField = (EditText) findViewById(R.id.dog_name_field);
		dogBreedField = (EditText) findViewById(R.id.dog_name_field);
		dogGenderField = (Button) findViewById(R.id.dog_gender_field);
		dogLifeStyleField = (Button) findViewById(R.id.dog_life_style_field);
		dogActivityField = (Button) findViewById(R.id.dog_activity_field);
		dogPhoto = (ImageView) findViewById(R.id.dog_photo);
		dogBirthday = (Button) findViewById(R.id.dog_birthday);


		//Vistas del dueno
		ownerNameField = (EditText) findViewById(R.id.owner_name_field);
		ownerGenderField = (Button) findViewById(R.id.owner_gender_field);
		ownerStateField = (Button) findViewById(R.id.owner_state);
		ownerBirthDay = (Button) findViewById(R.id.owner_birthday);


		btSend = (Button) findViewById(R.id.bt_send);



		//perro
		dogGenderField.setOnClickListener(this.displayDialogDogGender);
		dogLifeStyleField.setOnClickListener(this.displayDialogDogLifeStyle);
		dogActivityField.setOnClickListener(this.displayDialogDogActivity);
		dogPhoto.setOnClickListener(this.photoListener);
		dogBirthday.setOnClickListener(this.showDogDatePicker);

		//dueno
		ownerGenderField.setOnClickListener(this.displayDialogOwnerGender);
		ownerStateField.setOnClickListener(this.displayDialogOwnerState);
		ownerBirthDay.setOnClickListener(this.showOwnerDatePicker);


		//enviar
		btSend.setOnClickListener(sendListener);

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


	private OnClickListener showDogDatePicker = new View.OnClickListener (){

		@Override
		public void onClick(View v) {
			DatePickerFragment dogDialogBirtday = new DatePickerFragment();

			dogDialogBirtday.setDate(dogYear, dogMonth, dogDay);
			dogDialogBirtday.setInterface(myDogDate);

			dogDialogBirtday.show(getSupportFragmentManager(), "dateOwnerPicker");
		}

	};

	private OnClickListener showOwnerDatePicker = new View.OnClickListener (){

		@Override
		public void onClick(View v) {
			DatePickerFragment dogDialogBirtday = new DatePickerFragment();

			dogDialogBirtday.setDate(ownerYear, ownerMonth, ownerDay);
			dogDialogBirtday.setInterface(myOwnerDate);

			dogDialogBirtday.show(getSupportFragmentManager(), "dateDoogPicker");
		}

	};



	private OnClickListener sendListener  = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (validateForm()  == false ){
				UI.showAlertDialog("Upps!!", "Favor de llenar todos los campos antes de continuar", "OK", (Context)context, null);

			}
			
			WsDogUtils wsDogUtils = new WsDogUtils(context);


		}

		private boolean validateForm() {
			boolean ans = true;

			if (dogNameField.getText().equals("") || dogBreedField.getText().equals("")||
					dogGender== -1 || dogLifeStyle == -1 || dogActivity == -1 ||
					dogYear == -1 || dogMonth == -1 || dogDay == -1 ||

					ownerNameField.getText().equals("") || ownerGender == -1 ||
					ownerYear == -1 || ownerMonth == -1 || ownerDay == -1||

					ownerState == -1

					){
				ans = false;
			}

			return ans;
		}
	};

	MyDate myDogDate = new MyDate(){
		@Override
		public void getDate(int year, int month, int day) {
			dogYear = year;
			dogMonth = month;
			dogDay = day;

			dogBirthday.setHint(day + " / " + Recursos.MONTHS[month] + " / " + year);
		}

	};


	MyDate myOwnerDate = new MyDate(){
		@Override
		public void getDate(int year, int month, int day) {
			ownerYear = year;
			ownerMonth = month;
			ownerDay = day;

			ownerBirthDay.setHint(day + " / " + Recursos.MONTHS[month] + " / " + year);
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

package mx.ferreyra.dogapp;

import java.util.Map;

import mx.ferreyra.dogapp.ui.UI;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DogProfile extends Activity {

	private Context context;

	private TextView dogBreed;
	private TextView dogGender;
	private TextView dogLifeStyle;
	private TextView dogBirthDay;
	private TextView dogTip;
	
	private ImageView dogImage;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		if(DogUtil.getInstance().getCurrentUserId()==null) {
			startActivityForResult(new Intent(this, PreSignup.class), DogUtil.DOG_PROFILE);
		}

		setContentView(R.layout.profile_dog);
		
		dogBreed = (TextView) findViewById(R.id.tvBreed);
		dogGender = (TextView) findViewById(R.id.tvGender);
		dogLifeStyle = (TextView) findViewById(R.id.tvLyfeStyle);
		dogBirthDay = (TextView) findViewById(R.id.tvDate);
		dogBirthDay = (TextView) findViewById(R.id.tvDate);
		dogTip = (TextView) findViewById(R.id.tvDate);



	}


	protected class DogRegisterAsync extends AsyncTask<Void, Integer, String[][]> {
		private Context context;
		private ProgressDialog dialog;
		private Map<String, String> map;

		public DogRegisterAsync(Context context) {
			this.context = context;
		}

		/*
		public void setMap (Map<String, String> map ){
			this.map = map;
			return;
		}*/

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setMessage("Espere un moemento de favor");
			dialog.show();
		}

		@Override
		protected String[][] doInBackground(Void... params) {
			WsDogUtils wsDogUtils = new WsDogUtils(context);
			try {
				Integer userid = DogUtil.getInstance().getCurrentUserId() ;

				String[][]ans =  wsDogUtils.getDuenosMascotasByIdUsuario( userid  );
				return ans;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String[][] result) {
			super.onPostExecute(result);
			dialog.dismiss();

		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == Activity.RESULT_OK && intent != null){
			if ( requestCode == DogUtil.NEW_ROUTE){

				Bundle extras = intent.getExtras();
				Integer idPet = (Integer) extras.get("ID_PET");

				if (idPet<0){
					UI.showAlertDialog("Upss!!", "Ocurrio un error al registro al perro", "OK", context, null);

				}
			}
		}

	}
}

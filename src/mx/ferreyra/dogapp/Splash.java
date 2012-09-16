package mx.ferreyra.dogapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity {

	private String userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.splash);  

		SharedPreferences pref = getSharedPreferences(Utilities.DOGCHOW, 0);
		userId = pref.getString(Utilities.USER_ID, "");

		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if(manager != null){
			String deviceId = manager.getDeviceId();
			DogUtil.setDeviceID(deviceId);
		}


		Handler x = new Handler();
		//x.postDelayed(new splashhandler(), 2000);
		x.post(new splashhandler());

		//Log.i("Screen size", getWindow().getWindowManager().getDefaultDisplay().getWidth() + "X" + getWindow().getWindowManager().getDefaultDisplay().getHeight());

	}

	class splashhandler implements Runnable 
	{
		public void run() 
		{

			//TODO

			//			if(userId.equals("")|| userId == null){
			//				startActivity(new Intent(Splash.this, MainActivity.class));				
			//				finish(); 
			//			}else{
			//startActivity(new Intent(Splash.this, ExerciseMenu.class));
			startActivity(new Intent(Splash.this, DogRegister.class));
			finish();   
			//			}
		}

	} 	 


}

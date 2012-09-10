package mx.ferreyra.dogapp;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class PublishFacebookScreen extends Activity{


	private Button btnLeftTitle, btnRightTitle;
	private TextView txtTitle;
	private TextView shareText;
	private ProgressBar titleBar;

	private Button publishText, logoutFb;

	public boolean isWindowOpen = true;

	private static final String[] PERMISSIONS = new String[] {Utilities.PERMISSION_PUBLISH_STREAM};
	private Facebook facebook;
	private static String messageToPost;

	public void onCreate(Bundle savedInstanceState)
	{ 
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.publish_facebook); 
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);

		btnLeftTitle = (Button)findViewById(R.id.tbutton_left);
		txtTitle = (TextView)findViewById(R.id.title_txt);
		btnRightTitle = (Button)findViewById(R.id.tbutton_right);

		txtTitle.setText(getResources().getString(R.string.publish_fb));
		btnRightTitle.setVisibility(View.INVISIBLE);

		titleBar = (ProgressBar)findViewById(R.id.progress_title);
		titleBar.setVisibility(View.INVISIBLE);


		shareText = (TextView)findViewById(R.id.share_text);
		

		btnLeftTitle.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});

		publishText = (Button)findViewById(R.id.publish);
		publishText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				titleBar.setVisibility(View.VISIBLE);
				share();
			}
		});

		logoutFb = (Button)findViewById(R.id.logout_fb);
		logoutFb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				titleBar.setVisibility(View.VISIBLE);
				logoutFb();
			}
		});		

		facebook = new Facebook(DogUtil.FACEBOOK_APPID);
		SessionStore.restore(facebook, getApplicationContext());
		String facebookMessage = ((DogUtil)getApplication()).getRouteName();
		if (facebookMessage == null){
			facebookMessage = "Nueva ruta";
		}
		messageToPost = getString(R.string.route_share_text1)+" "+facebookMessage+" "+getString(R.string.route_share_text2)+getString(R.string.dogchow) 
		+"\n"+ getString(R.string.share_text) + "\n"+getString(R.string.share_android)+" "+getString(R.string.share_android_link) + " \n" +getString(R.string.share_iphone) 
		+ " "+ getString(R.string.share_iphone_link);
		
		Log.d("Publish", "Message to post: "+messageToPost);

		shareText.setText(getFacebookMsgParse());

	} 

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isWindowOpen = true;
	}
	@Override
	protected void onPause() {
		super.onPause();
	}

	private String getFacebookMsgParse() {
		return messageToPost;
	}
	
	private String getFacebookMsg() {
		String wall = "Acabo de correr la Ruta "+messageToPost+" con el Entrenador DOG CHOW®" 
		+"\n"+ "¡Tu también corre con tu perro en familia! \nDescarga la aplicación:" + "\n"+
		"Android:"+" "+"http://bitly.com/newsandroid" + " \n" +"iPhone:" + " "+ "http://bitly.com/iphoneDC" ;
		return wall;
	}

	public void logoutFb(){
		if(facebook.isSessionValid()){			
			AsyncFacebookRunner asyncFacebookRunner = new AsyncFacebookRunner(facebook);
			asyncFacebookRunner.logout(getApplicationContext(), new RequestListener() {

				@Override
				public void onMalformedURLException(MalformedURLException e, Object state) {
					Log.e("DogChowMX", "Error : "+e.getMessage());
					setTitleInvisible();
				}

				@Override
				public void onIOException(IOException e, Object state) {
					Log.e("DogChowMX", "Network unreachable: "+e.getMessage());
					setTitleInvisible();

				}

				@Override
				public void onFileNotFoundException(FileNotFoundException e, Object state) {
					Log.e("DogChowMX", "onFileNotFoundException: "+e.getMessage());
					setTitleInvisible();

				}

				@Override
				public void onFacebookError(FacebookError e, Object state) {
					Log.e("DogChowMX", "Facebook error: "+e.getMessage());
					setTitleInvisible();
				}

				@Override
				public void onComplete(String response, Object state) {
					setTitleInvisible();
					if(response.equals(true) || response.equals("true") ){
						handler.post(new Runnable() {
							@Override
							public void run() {
								SessionStore.clear(getApplicationContext());
								Toast.makeText(getApplicationContext(), getString(R.string.fb_user_logout_success), Toast.LENGTH_SHORT).show();
								finish();
							}
						});
					}else
						try {
							//Log.d("Publish","Fb response"+response);
							JSONObject myjson = new JSONObject(response);
							final String errorMessage = myjson.getString("error_msg");								
							handler.post(new Runnable() {
								@Override
								public void run() {
									if(errorMessage != null )
										Toast.makeText(getApplicationContext(),  getString(R.string.fb_user_logout_failure), Toast.LENGTH_SHORT).show();
									finish();
								}
							});


						} catch (JSONException e) {
							setTitleInvisible();
							e.printStackTrace();
						}catch (Exception e) {
							setTitleInvisible();
							e.printStackTrace();
						}

				}
			});

		}else{
			SessionStore.clear(getApplicationContext());
			setTitleInvisible();
		}
	}

	public void setTitleInvisible(){
		handler.post(new Runnable() {

			@Override
			public void run() {
				titleBar.setVisibility(View.INVISIBLE);
			}
		});
	}

	public void share(){
		if (!facebook.isSessionValid()) { 
			loginAndPostToWall();
		}
		else {
			postToWall(getFacebookMsgParse());
		}
	}

	public void loginAndPostToWall(){
		isWindowOpen = false;
		facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}

	Handler handler = new Handler();
	public void postToWall(String message){
		Bundle parameters = new Bundle();

		if(message.length()>420)
			message = message.substring(0, 419);
		parameters.putString("message", message);
		try {
			AsyncFacebookRunner asyncFacebookRunner = new AsyncFacebookRunner(facebook);
			asyncFacebookRunner.request("me/feed", parameters, "POST",  new RequestListener() {

				@Override
				public void onMalformedURLException(MalformedURLException e, Object state) {
					showToast(getString(R.string.unable_to_connect));
					setTitleInvisible();

				}

				@Override
				public void onIOException(IOException e, Object state) {
					showToast(getString(R.string.network_unreachable));
					setTitleInvisible();

				}

				@Override
				public void onFileNotFoundException(FileNotFoundException e, Object state) {
					showToast(getString(R.string.unable_to_process)); 
					setTitleInvisible();

				}

				@Override
				public void onFacebookError(FacebookError e, Object state) {
					showToast(getString(R.string.fb_error));
					setTitleInvisible();

				}

				@Override
				public void onComplete(final String response, Object state) {
					setTitleInvisible();
					Log.i("Publish", "response: "+response);
					
					handler.post(new Runnable() {					
						@Override
						public void run() {
							
							if(response.contains("error")){
//								final Dialog dialog = new Dialog(PublishFacebookScreen.this);									
//								dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//								dialog.setContentView(R.layout.route_naming_dialog);	
//								dialog.setCancelable(true);
//								TextView textView = (TextView)dialog.findViewById(R.id.naming_description);
//								textView.setText(getString(R.string.not_posted_successfully));
//								Button button = (Button) dialog.findViewById(R.id.naming_success);
//								button.setOnClickListener(new OnClickListener() {
//									@Override
//									public void onClick(View v) {
//										dialog.dismiss();
//										loginAndPostToWall();
//									}
//								});
//								dialog.show();
								loginAndPostToWall();
							}else {
								final Dialog dialog = new Dialog(PublishFacebookScreen.this);									
								dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
								dialog.setContentView(R.layout.route_naming_dialog);	
								dialog.setCancelable(true);
								Button button = (Button) dialog.findViewById(R.id.naming_success);
								button.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										dialog.dismiss();
									}
								});
								dialog.show();
							}	
						}
					});
				}
			}, new String());

		} catch (Exception e) {
			setTitleInvisible();
			showToast(getString(R.string.unable_to_publish));
			e.printStackTrace();
			finish();
		}
	}

	class LoginDialogListener implements DialogListener {

		@Override
		public void onCancel() {	
			isWindowOpen = true;
			showToast(getResources().getString(R.string.login_cancel));
			setTitleInvisible();
		}

		@Override
		public void onComplete(Bundle values) {	
			isWindowOpen = true;
			SessionStore.clear(getApplicationContext());
			SessionStore.save(facebook, getApplicationContext());
			if (messageToPost != null){
				postToWall(messageToPost);
			}
			
		}

		@Override
		public void onError(DialogError e) {
			isWindowOpen = true;
			showToast(getResources().getString(R.string.error_in_dialog));
			setTitleInvisible();
		}

		@Override
		public void onFacebookError(FacebookError e) {
			isWindowOpen = true;
			showToast(getResources().getString(R.string.fb_error_msg));
			setTitleInvisible();
		}

	}


	private void showToast(final String message){
		handler.post(new Runnable() {			
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();	
				finish();
			}
		});
	}


}

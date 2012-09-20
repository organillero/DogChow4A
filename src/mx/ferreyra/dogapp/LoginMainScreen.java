package mx.ferreyra.dogapp;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import mx.ferreyra.dogapp.AppData.USER_LOGIN_TYPE;
import mx.ferreyra.dogapp.SessionEvents.AuthListener;
import mx.ferreyra.dogapp.SessionEvents.LogoutListener;
import mx.ferreyra.dogapp.org.ksoap2.SoapEnvelope;
import mx.ferreyra.dogapp.org.ksoap2.serialization.SoapObject;
import mx.ferreyra.dogapp.org.ksoap2.serialization.SoapSerializationEnvelope;
import mx.ferreyra.dogapp.org.ksoap2.transport.HttpTransportSE;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class LoginMainScreen extends Activity{

    private Button manualBtn;
    private Intent i;

    public Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;

    private ProgressBar pb;
    private TextView txtTitle;
    private Button txtHomeBtn;
    private RegisterTask registerTask;
    private boolean isWindowOpen = true;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.login_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                                  R.layout.title_bar);

        txtTitle = (TextView)findViewById(R.id.title_txt);
        txtHomeBtn = (Button)findViewById(R.id.tbutton_right);

        txtHomeBtn.setVisibility(View.INVISIBLE);
        txtTitle.setText(getResources().getString(R.string.login_title));

        manualBtn = (Button)findViewById(R.id.registerBtn);
        manualBtn.setSelected(true);
        pb = (ProgressBar)findViewById(R.id.register_progress);
        pb.setVisibility(View.INVISIBLE);
    }

    public void onClickTButtonLeftButton(View v) {
        finish();
    }

    public void onClickLoginButton(View v) {
        fbLogin();
    }

    public void onClickRegisterButton(View v) {
        i =new Intent(this,LoginScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(pb != null)
            pb.setVisibility(View.INVISIBLE);

    }

    //Send facebook login to server and
    private class RegisterTask implements Runnable{

        private String errorMsg;
        private String resultData;
        private String response;

        @Override
        public void run() {
            isWindowOpen = true;
            try {
                // process the response here: executed in background thread)
                final JSONObject json = Util.parseJson(resultData);

                final String email = json.getString("email");

                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                SoapObject request = new SoapObject(AppData.NAMESPACE,
                                                    AppData.METHOD_NAME_USER_REGSTRATION);

                final String is_facebook = "1";
                final String first_name = json.getString("first_name");

                request.addProperty("username", email);

                request.addProperty("password", first_name);
                request.addProperty("isFacebook", is_facebook);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet=true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(AppData.URL);

                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

                androidHttpTransport.debug=true;

                androidHttpTransport.call(AppData.SOAP_ACTION+AppData.METHOD_NAME_USER_REGSTRATION, envelope);

                response = envelope.getResponse().toString();



            }
            catch (UnknownHostException e) {
                errorMsg = getResources().getString(R.string.no_connection);
                Log.e(DogUtil.DEBUG_TAG, errorMsg);
                progressTitle();

            }catch (UnknownServiceException e) {
                errorMsg = getResources().getString(R.string.service_unavailable);
                Log.e(DogUtil.DEBUG_TAG,  getResources().getString(R.string.service_unavailable));
                progressTitle();
            }catch (MalformedURLException e) {
                errorMsg = getResources().getString(R.string.url_malformed);
                e.printStackTrace();
                Log.e(DogUtil.DEBUG_TAG, errorMsg );
                progressTitle();
            }catch (JSONException e) {
                errorMsg = getResources().getString(R.string.parse_error);
                Log.e(DogUtil.DEBUG_TAG, errorMsg);
                e.printStackTrace();
                progressTitle();
            } catch (FacebookError e) {
                errorMsg = getResources().getString(R.string.fb_error);
                Log.e(DogUtil.DEBUG_TAG, errorMsg);
                e.printStackTrace();
                progressTitle();
            }catch (XmlPullParserException e) {
                errorMsg = getResources().getString(R.string.unable_to_parse);
                Log.e(DogUtil.DEBUG_TAG, errorMsg);
                progressTitle();
            } catch (IOException e) {
                errorMsg = getResources().getString(R.string.io_error);
                Log.e(DogUtil.DEBUG_TAG, errorMsg);
                progressTitle();
            } catch (Exception e) {
                errorMsg = getResources().getString(R.string.error_in_register);
                Log.e(DogUtil.DEBUG_TAG, errorMsg);
                progressTitle();
            }

            new Handler().post(new Runnable() {

                    @Override
                        public void run() {
                        progressTitle();
                        if(errorMsg == null){
                            Toast.makeText(LoginMainScreen.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                            if(response != null){
                                SharedPreferences pref = getSharedPreferences(Utilities.DOGCHOW, 0);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString(Utilities.USER_ID,response);
                                edit.commit();

                                AppData.USER_ID = response.trim();
                                AppData.assignType(USER_LOGIN_TYPE.FACEBOOK);


                                SharedPreferences sharedPreferences = getSharedPreferences(Utilities.DOGAPP, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Utilities.ROUTE_TIMING, getResources().getString(R.string.route_time));
                                editor.putString(Utilities.ROUTE_DISTANCE, getResources().getString(R.string.route_distance));
                                editor.putString(Utilities.ROUTE_SPEED, getResources().getString(R.string.route_speed));
                                editor.commit();

                                Intent signinPage = new Intent(LoginMainScreen.this, ExerciseMenu.class);
                                AppData.IS_FIRST = true;
                                startActivity(signinPage);
                                finish();
                            }
                        }else{
                            Toast.makeText(LoginMainScreen.this, errorMsg, Toast.LENGTH_SHORT).show();
                            progressTitle();
                        }
                    }
                });

        }

    }



    private void fbLogin() {
        try{
            mx.ferreyra.dogapp.DogUtil ac = (mx.ferreyra.dogapp.DogUtil) getApplication();
            ac.initFacebook();

            mFacebook = ac.getFacebook();
            mAsyncRunner = ac.getAsyncFacebookRunner();

            boolean isSessionValid = SessionStore.restore(mFacebook, this);
            SessionEvents.addAuthListener(new SampleAuthListener());
            SessionEvents.addLogoutListener(new SampleLogoutListener());

            if(isSessionValid)
                Log.i(DogUtil.DEBUG_TAG,"Aleady logged in");
            else
                Log.i(DogUtil.DEBUG_TAG,"Login necessary");

            //                if(!isSessionValid)
            //                        mFacebook.authorize(LoginMainScreen.this, ac.getPermissions(),         new UserInfoListener());

            if(!isSessionValid){
                isWindowOpen = false;
                mFacebook.authorize(LoginMainScreen.this, ac.getPermissions() ,Facebook.FORCE_DIALOG_AUTH, new UserInfoListener());
            } else {
                //locale
                Bundle parameters = new Bundle();
                parameters.putString("locale", "es-mx");
                parameters.putString(Facebook.TOKEN, mFacebook.getAccessToken());
                mAsyncRunner.request("me", parameters,  new FacebookUserRequestListener());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public class SampleAuthListener implements AuthListener {
        public void onAuthSucceed() {
            Log.i(DogUtil.DEBUG_TAG,"Success!");
            progressTitle();
        }

        public void onAuthFail(String error) {
            Log.i(DogUtil.DEBUG_TAG,"Fail!");
            progressTitle();
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            Log.i("DogChow-Logout","Begins...");
        }

        public void onLogoutFinish() {
            Log.i("DogChow-Logout","Finised");
            progressTitle();
        }
    }

    public class UserInfoListener implements DialogListener {

        public void onComplete(Bundle values) {
            try{
                isWindowOpen = true;
                handler.post(new Runnable() {

                        @Override
                            public void run() {
                            pb.setVisibility(View.VISIBLE);
                        }
                    });
                Log.i(DogUtil.DEBUG_TAG, "Logged in!");
                SessionStore.save(mFacebook, getApplicationContext());
                mAsyncRunner.request("me", new FacebookUserRequestListener());
            } catch (Exception e) {
                progressTitle();
                e.printStackTrace();
            }
        }

        public void onCancel() {
            isWindowOpen = true;
            progressTitle();
            Log.i(DogUtil.DEBUG_TAG, "Login cancelled");
        }

        public void onError(DialogError e) {
            isWindowOpen = true;
            progressTitle();
            Log.i(DogUtil.DEBUG_TAG, "Dialog error");
            e.printStackTrace();
        }

        public void onFacebookError(FacebookError e) {
            isWindowOpen = true;
            progressTitle();
            Log.i(DogUtil.DEBUG_TAG, "Facebook error ");
            e.printStackTrace();
        }
    }
    Handler handler = new Handler();

    private void progressTitle(){
        try{
            handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.INVISIBLE);
                    }
                });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public class FacebookUserRequestListener
        extends BaseRequestListener {
        public void onComplete(final String response,
                               final Object state) {
            try {
                isWindowOpen = true;
                registerTask = new RegisterTask();
                registerTask.resultData = response;
                runOnUiThread(registerTask);
            } catch(Exception e) {
                isWindowOpen = true;
                progressTitle();
                e.printStackTrace();
            }
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            // TODO Auto-generated method stub
            super.onFacebookError(e, state);
            isWindowOpen = true;
            Log.i(DogUtil.DEBUG_TAG, "onFacebookError error ");
            progressTitle();
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e,
                                            Object state) {
            // TODO Auto-generated method stub
            super.onFileNotFoundException(e, state);
            isWindowOpen = true;
            Log.i(DogUtil.DEBUG_TAG, "onFileNotFoundException error ");
            progressTitle();
        }

        @Override
        public void onIOException(IOException e, Object state) {
            // TODO Auto-generated method stub
            super.onIOException(e, state);
            isWindowOpen = true;
            Log.i(DogUtil.DEBUG_TAG, "onIOException error ");
            progressTitle();
        }

        @Override
        public void onMalformedURLException(MalformedURLException e,
                                                Object state) {
            // TODO Auto-generated method stub
            super.onMalformedURLException(e, state);
            isWindowOpen = true;
            Log.i(DogUtil.DEBUG_TAG, "onMalformedURLException error ");
            progressTitle();
        }
    }

    public void postServer(String response) {
        registerTask = new RegisterTask();
        registerTask.resultData = response;
        runOnUiThread(registerTask);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isWindowOpen = true;
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(DogUtil.DEBUG_TAG,requestCode + " " + resultCode + " " + data);
        mFacebook.authorizeCallback(requestCode, resultCode, data);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(isWindowOpen) {
                progressTitle();
                return super.onKeyDown(keyCode, event);
            }

            progressTitle();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

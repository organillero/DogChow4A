package mx.ferreyra.dogapp;

import mx.ferreyra.dogapp.persistence.Preferencias;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class DogUtil extends Application {


    public static final String DEBUG_TAG = "DogChowMx";

    public static final int NEW_ROUTE = 0x04;  //nueva ruta
    public static final int STATISTICS = 0x02; //estadisticas
    public static final int LOAD_ROUTE = 0x03; //cargar ruta
    public static final int DOGWELFARE = 0x01; //bienestar canico
    public static final int DOG_CALENDAR = 0x07; // calendario del perro

    public static final int DOG_PROFILE = 0x05;
    public static final int DOG_EDIT_PROFILE = 0x06;

    public static String FB_ACCESS_TOKEN_PREFERENCE_KEY = "fb_access_token";
    public static String FB_ACCESS_EXPIRES_PREFERENCE_KEY = "fb_access_expires";

    /**
     * Id for user logged.
     */
    // private Integer currentUserId;
    //private Integer currentDogId;


    Preferencias prefs;

    /**
     * Singleton reference for application object.
     */
    private static DogUtil app;

    public static final String FACEBOOK_APPID = "360694843999854";

    public static final String GOGGLE_ANALYTICS_TRACK_ID = "UA-27608007-1";
    public Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    private String routeName;
    private static String DEVICE_ID;

    private GoogleAnalyticsTracker gTracker;
    public static int TRACKER_VALUE = 55613156;

    private static final String[] PERMISSIONS =
            new String[] {Utilities.PERMISSION_OFFLINE_ACCESS,
        Utilities.PERMISSION_USER_RELATIONSHIP,
        Utilities.PERMISSION_EMAIL,
        Utilities.PERMISSION_PUBLISH_STREAM,
        Utilities.PERMISSION_READ_STREAM};

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(app == null)
            app = this;


        initFacebook();
        if(gTracker == null){
            gTracker = GoogleAnalyticsTracker.getInstance();
            gTracker.startNewSession(GOGGLE_ANALYTICS_TRACK_ID, this);
            setTrackerInformation();
        }
    }

    public void initFacebook(){
        if(DogUtil.FACEBOOK_APPID == null) {
            Util.showAlert(getApplicationContext(),
                    getResources().getString(R.string.warning),
                    getResources().getString(R.string.invalid_facebook_id));
        }

        if(mFacebook ==null)
            mFacebook = new Facebook(DogUtil.FACEBOOK_APPID);

        if(mAsyncRunner ==null)
            mAsyncRunner = new AsyncFacebookRunner(mFacebook);
    }

    public Facebook getFacebook(){
        return mFacebook;
    }

    public AsyncFacebookRunner getAsyncFacebookRunner(){
        return mAsyncRunner;
    }

    public String[] getPermissions(){
        return PERMISSIONS;
    }

    public GoogleAnalyticsTracker getTracker(){
        if(gTracker == null){
            gTracker = GoogleAnalyticsTracker.getInstance();
            gTracker.startNewSession(GOGGLE_ANALYTICS_TRACK_ID, this);
        }
        return gTracker;
    }

    public void setTrackerInformation(){
        gTracker.setCustomVar(1, "Application Details", "Android, "+getVersionName(this, DogUtil.class) +", "+
                Build.MODEL+ ", "+android.os.Build.VERSION.RELEASE  , 1);
    }

    public static String getVersionName(Context context, Class cls) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(new ComponentName(context, cls).getPackageName()
                            , 0).versionName;
        } catch(NameNotFoundException e) {
            return null;
        }
    }

    public static boolean checkNumber(String string){
        try {
            for(int i=0; i<string.length(); i++)
                if (!Character.isDigit(string.charAt(i)))
                    return false;
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static void setDeviceID(String deviceId){
        DogUtil.DEVICE_ID = deviceId;
    }

    public static String getDeviceUniqueNumber(){
        return DogUtil.DEVICE_ID;
    }

    public static DogUtil getInstance() {
        return app;
    }


    public Integer getCurrentUserId() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_name), 0);
        int possible = pref.getInt(getString(R.string.preference_user_id), -1);
        return possible>=0 ? possible : null;
    }

    public Integer getCurrentDogId() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_name), 0);
        int possible = pref.getInt(getString(R.string.preference_dog_id), -1);
        return possible>=0 ? possible : null;
    }






    public Integer getCurrentOwnerId() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_name), 0);
        int possible = pref.getInt(getString(R.string.preference_owner_id), -1);
        return possible>=0 ? possible : null;
    }

    public void saveCurrentUserId(Integer userId) {
        // Store user id on preferences
        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_name), 0);
        Editor e = pref.edit();
        if(userId == null || userId < 0) {
            // Store user id on preferences
            e.remove(getString(R.string.preference_user_id));
            e.remove(Utilities.USER_ID);
            e.commit();
        } else {
            // Store user id on preferences
            e.putInt(getString(R.string.preference_user_id), userId);
            e.putString(Utilities.USER_ID, userId.toString());
            e.commit();
        }
    }

    public void saveCurrentDogId(Integer dogId) {


        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_name), 0);
        Editor e = pref.edit();
        if(dogId == null || dogId < 0) {
            // Store dog id on preferences
            e.remove(getString(R.string.preference_dog_id));
            e.commit();
        } else {
            // Store dog id on preferences
            e.putInt(getString(R.string.preference_dog_id), dogId);
            e.commit();
        }
    }

    public void saveCurrentOwnerId(Integer ownerId) {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_name), 0);
        Editor e = pref.edit();
        if(ownerId == null || ownerId < 0) {
            // Store owner id on preferences
            e.remove(getString(R.string.preference_owner_id));
            e.commit();
        } else {
            // Store owner id on preferences
            e.putInt(getString(R.string.preference_owner_id), ownerId);
            e.commit();
        }
    }



}

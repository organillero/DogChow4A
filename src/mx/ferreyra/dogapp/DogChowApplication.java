package mx.ferreyra.dogapp;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * @author Israel Buitron
 */
public class DogChowApplication extends Application {
    private Integer currentUserId;
    private static DogChowApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        DogChowApplication.app = this;

        // Loading preferences
        String prefer = getString(R.string.preferences_name);
        String userId = getString(R.string.preference_user_id);
        SharedPreferences pref = getSharedPreferences(prefer, 0);
        userId = pref.getString(userId, null);
        if(userId!=null)
            this.currentUserId = Integer.valueOf(userId);
    }

    public static DogChowApplication getInstance() {
        return app;
    }

    public Integer getCurrentUserId() {
        return this.currentUserId;
    }
}

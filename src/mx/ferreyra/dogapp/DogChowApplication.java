package mx.ferreyra.dogapp;

import android.app.Application;

/**
 * @author Israel Buitron
 */
public class DogChowApplication extends Application {
    private static DogChowApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        DogChowApplication.app = this;
    }

    public static DogChowApplication getInstance() {
        return app;
    }
}

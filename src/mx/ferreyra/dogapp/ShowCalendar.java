package mx.ferreyra.dogapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShowCalendar extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_calendar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_show_calendar, menu);
        return true;
    }
}

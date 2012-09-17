package mx.ferreyra.dogapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
    }

    public void onClickRegisterButton(View v) {
        startActivity(new Intent(this,RegisterScreen.class));
    }

    public void onClickLoginButton(View v) {
        startActivity(new Intent(this,LoginMainScreen.class));
    }
}

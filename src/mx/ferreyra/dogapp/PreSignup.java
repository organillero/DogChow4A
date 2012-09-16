package mx.ferreyra.dogapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PreSignup extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_signup);
    }

    public void onClickSignupButton(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void onClickLoginButton(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onClickRecoverPasswordButton(View view) {
        startActivity(new Intent(this, RetrievePassword.class));
    }
}

package mx.ferreyra.dogapp;

import mx.ferreyra.dogapp.ui.UI;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;

public class PreSignup extends Activity {

    public static final Integer SIGN_UP = 10;
    public static final Integer LOGIN   = 11;
    public static final Integer RECOVER = 12;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_signup);
    }

    public void onClickSignupButton(View view) {
        startActivityForResult(new Intent(this, SignupActivity.class), SIGN_UP);
    }

    public void onClickLoginButton(View view) {
        startActivityForResult(new Intent(this, LoginScreen.class), LOGIN);
    }

    public void onClickRecoverPasswordButton(View view) {
        startActivityForResult(new Intent(this, RetrievePassword.class), RECOVER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK && intent != null){
            if (requestCode == SIGN_UP || requestCode == LOGIN){
                Bundle extras = intent.getExtras();
                Integer idUser = (Integer)extras.get("ID_USER");

                if(idUser < 0){
                    UI.showAlertDialog(getString(R.string.ups),
                                       getString(R.string.an_error_ocurred),
                                       getString(android.R.string.ok),
                                       this, null);
                } else {
                    // Save user id
                    DogUtil.getInstance().saveCurrentUserId(idUser);

		    // Return to parent activity
                    Intent goBack = new Intent();
                    goBack.putExtra("ID_USER", idUser);
                    setResult(RESULT_OK, goBack);
                    finish();
                }
            } else if (requestCode == RECOVER){
            }
        }
    }
}

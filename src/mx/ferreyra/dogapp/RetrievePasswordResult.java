package mx.ferreyra.dogapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class RetrievePasswordResult extends Activity {

	private TextView title;
	private Button titleRight;
	private Button titleLeft;
	private TextView retrievePasswordResult;
	private Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);		
		setContentView(R.layout.retrieve_password_result);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);


		titleLeft = (Button)findViewById(R.id.tbutton_left);
		titleLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {			
				//i =new Intent(RetrievePasswordResult.this, LoginScreen.class); 
				//startActivity(i);
				finish();	
			}
		});

		title = (TextView)findViewById(R.id.title_txt);
		title.setVisibility(View.VISIBLE);	
		title.setText("");
		title.setBackgroundResource(R.drawable.title_img);

		titleRight = (Button)findViewById(R.id.tbutton_right);
		titleRight.setVisibility(View.GONE);

		retrievePasswordResult = (TextView)findViewById(R.id.password_result);		

		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			String pwd =bundle.getString(Utilities.RETRIEVE_PASSWORD_RESULT);
			if(pwd != null)	{
				retrievePasswordResult.setText(pwd);
			}
		}


	}

}

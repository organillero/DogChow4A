package mx.ferreyra.dogapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity{
	
	private Button loginBtn,registerBtn;
	private Intent i; 
 
	
	public void onCreate(Bundle savedInstanceState)
	{ 
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main); 		
	 
		loginBtn = (Button)findViewById(R.id.loginBtn);
		registerBtn = (Button)findViewById(R.id.registerBtn); 
		loginBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				i =new Intent(MainActivity.this,LoginMainScreen.class);
				startActivity(i);
		}
		});
		
		registerBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				i =new Intent(MainActivity.this,RegisterScreen.class); 				
				startActivity(i);
			}
		});		

	} 
}

package mx.ferreyra.dogapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Starting extends Activity {

	private TextView textView;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4; 
	private String text = "<html><body bgcolor=\"#5c5c5c\"><b>Si est�s empezando a correr con tu perro, " +
	"<font color=\"#0f7243\">haz ejercicio poco a poco&nbsp;</font>" +
	"para que se acostumbre a correr en la calle.</b>";

	private String text2 = "<font color=\"#0f7243\"><b>Procura no alimentarlo 45 min. antes,&nbsp;</font>ni despu�s de correr.</b>";


	private String text3 = "<font color=\"#0f7243\"><b>Evita correr sobre el cemento&nbsp;</font>cuando haga mucho calor.</b>";

	private String text4 = "<b>Si te gusta pasear con tu perro de noche," +
	"<font color=\"#0f7243\">es recomendable colocarle un collar reflejante</font>&nbsp;o una lucecita intermitente.</b></body></html>";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.tips_before_starting);  	
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TextView title_txt = (TextView)findViewById(R.id.title_txt);
		title_txt.setText("");
		title_txt.setBackgroundResource(R.drawable.title_img);

		Button imageView = (Button) findViewById(R.id.tbutton_right);
		imageView.setVisibility(View.INVISIBLE);

		textView = (TextView)findViewById(R.id.tips1);
		textView.setText(Html.fromHtml(text));		

		textView2 = (TextView)findViewById(R.id.tips2);
		textView2.setText(Html.fromHtml(text2));

		textView3 = (TextView)findViewById(R.id.tips3);
		textView3.setText(Html.fromHtml(text3));

		textView4 = (TextView)findViewById(R.id.tips4);
		textView4.setText(Html.fromHtml(text4));

		final Button button = (Button)findViewById(R.id.button12);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {	
				int data = getIntent().getExtras().getInt(Utilities.LOAD_ROUTE);
				if(data == 1){
					Intent intent = new Intent(Starting.this, LoadPath.class);
					int intValue = 1;
					intent.putExtra(Utilities.TRAINING_SPOT, intValue);
					startActivity(intent);
				}else if(data == 2){
					Intent intent = new Intent(Starting.this, TrackMapRoute.class);
					int intValue = 2;
					intent.putExtra(Utilities.TRAINING_SPOT, intValue);
					startActivity(intent);
				}				
			}
		});

		Button previous = (Button)findViewById(R.id.tbutton_left);
		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Starting.this, ExerciseMenu.class);
				startActivity(intent);

			}
		});

	}


}

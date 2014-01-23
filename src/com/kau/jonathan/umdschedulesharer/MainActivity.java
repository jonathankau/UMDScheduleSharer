package com.kau.jonathan.umdschedulesharer;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView title = (TextView)findViewById(R.id.main_title);
		Button login = (Button)findViewById(R.id.fb_login);
	    Typeface face=Typeface.createFromAsset(this.getAssets(),
	                                          "fonts/Lato-Reg.ttf");

	    title.setTypeface(face);
	    login.setTypeface(face);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Sign In Method when the user presses the button
	
	void signIntoFacebook() {
		
		
	}

}

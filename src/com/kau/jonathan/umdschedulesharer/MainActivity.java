package com.kau.jonathan.umdschedulesharer;

import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class MainActivity extends Activity {
	ProgressDialog progressDialog;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);	    

		setContentView(R.layout.activity_main);

		TextView title = (TextView)findViewById(R.id.main_title);
		LoginButton login = (LoginButton)findViewById(R.id.fb_login);

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
	public void signIntoFacebook(View v) {
		LoginButton login = (LoginButton)findViewById(R.id.fb_login);
		login.setSessionStatusCallback(new Session.StatusCallback() {

	           @SuppressWarnings("deprecation")
			@Override
	           public void call(Session session, SessionState state, Exception exception) {

	            if (session.isOpened()) {
	               Log.e("Access Token","Access Token"+ session.getAccessToken());
	               Request.executeMeRequestAsync(session,new Request.GraphUserCallback() {
	                      @Override
	                      public void onCompleted(GraphUser user,Response response) {
	                          if (user != null) { 
	                           Log.e("FACEBOOK USER ID","User ID "+ user.getId());
	                           Log.e("FACEBOOK EMAIL","Email "+ user.asMap().get("email"));
	                          }
	                      }
	                    });
	                  }

	           }
	    }); 
		
		Session session = Session.getActiveSession();
		onSessionStateChange(session, session.getState(), null);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) { // Logged in
			String name = "saved_name";
			String fb_id = "saved_id";

			Intent signedIn = new Intent(MainActivity.this, SignInActivity.class);
			signedIn.putExtra("FB_NAME", name);
			signedIn.putExtra("FB_ID", fb_id);			
			startActivity(signedIn);

			//this.overridePendingTransition(0, 0);
		} else { // Logged out		    
			findViewById(R.id.main_title).setVisibility(View.VISIBLE);
			findViewById(R.id.fb_login).setVisibility(View.VISIBLE);
			findViewById(R.id.terp_logo).setVisibility(View.VISIBLE);
		}
	}

	// For Facebook Session
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		} else {
			findViewById(R.id.main_title).setVisibility(View.VISIBLE);
			findViewById(R.id.fb_login).setVisibility(View.VISIBLE);
			findViewById(R.id.terp_logo).setVisibility(View.VISIBLE);
		}

		uiHelper.onResume();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

}

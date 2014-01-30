package com.kau.jonathan.umdschedulesharer;

import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
		
		// Check to see if user has gotten schedule already
		SharedPreferences prefs = this.getSharedPreferences("com.kau.jonathan.umdschedulesharer", Context.MODE_PRIVATE);
		int obtained_schedule = prefs.getInt("com.kau.jonathan.umdschedulesharer.obtained_schedule", 0);

		if(obtained_schedule == 1) {
			// Take source and send with intent to Schedule Activity
			Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);

			// Attach source code
			intent.putExtra("SOURCE_CODE", prefs.getString("com.kau.jonathan.umdschedulesharer.schedule_code", ""));		


			// Start activity
			startActivity(intent);
		}


		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);	    

		setContentView(R.layout.activity_main);

		TextView title = (TextView)findViewById(R.id.main_title);
		LoginButton login = (LoginButton)findViewById(R.id.fb_login);
		login.setReadPermissions(Arrays.asList("basic_info"));

		Typeface face=Typeface.createFromAsset(this.getAssets(),
				"fonts/Lato-Reg.ttf");

		title.setTypeface(face);
		login.setTypeface(face);		
		
//		login.setSessionStatusCallback(new Session.StatusCallback() {
//
//			@SuppressWarnings("deprecation")
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//
//				if (session.isOpened()) {
//					Log.e("Access Token","Access Token"+ session.getAccessToken());
//					Request.executeMeRequestAsync(session,new Request.GraphUserCallback() {
//						@Override
//						public void onCompleted(GraphUser user,Response response) {
//							if (user != null) { 
//								String name = "saved_name";
//								String fb_id = "saved_id";
//
//								Intent signedIn = new Intent(MainActivity.this, SignInActivity.class);
//								signedIn.putExtra("FB_NAME", name);
//								signedIn.putExtra("FB_ID", fb_id);			
//								startActivity(signedIn);
//							}
//						}
//					});
//				}
//
//			}
//		}); 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Sign In Method when the user presses the button
	public void signIntoFacebook(View v) {

//		Session session = Session.getActiveSession();
//		if (!session.isOpened() && !session.isClosed()) {
//			session.openForRead(new Session.OpenRequest(this)
//			.setPermissions(Arrays.asList("basic_info"))
//			.setCallback(callback));
//		} else {
//			Session.openActiveSession(this, true, callback);
//		}
//		
//
//		onSessionStateChange(session, session.getState(), null);
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

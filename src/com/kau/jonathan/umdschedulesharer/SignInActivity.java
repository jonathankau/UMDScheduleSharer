package com.kau.jonathan.umdschedulesharer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class SignInActivity extends Activity {
	private static final int REAUTH_ACTIVITY_CODE = 100;
	ProgressDialog loggingIn;
	ProfilePictureView profilePictureView;
	TextView username;
	TextView title;
	String fb_id;

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		Intent intent = getIntent();
		
		// Generate typefaces
		
		final Typeface face=Typeface.createFromAsset(this.getAssets(),
				"fonts/Lato-Reg.ttf");
		final Typeface boldface=Typeface.createFromAsset(this.getAssets(),
				"fonts/Lato-Bol.ttf");


		// Populate Spinner choices
		Spinner spinner = (Spinner) findViewById(R.id.semester_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item){

			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);				
				((TextView) v).setTypeface(face);
				return v;
			}


			public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
				View v =super.getDropDownView(position, convertView, parent);
				((TextView) v).setTypeface(face);
				return v;
			}
		};
		
		String[] semesters = getResources().getStringArray(R.array.semesters_array);
		
		for(String s: semesters) {
			adapter.add(s);
		}
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		// Facebook Session
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		// Find the user's profile picture custom view
		profilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);

		// Change typeface for all text
		title = (TextView)findViewById(R.id.main_title);
		username = (TextView)findViewById(R.id.selection_user_name);

		EditText umd_username = (EditText) findViewById(R.id.umd_username);
		EditText umd_password = (EditText) findViewById(R.id.umd_password);
		Button umd_login = (Button) findViewById(R.id.umd_login);

		title.setTypeface(face);
		username.setTypeface(face);
		umd_username.setTypeface(face);
		umd_password.setTypeface(face);
		umd_login.setTypeface(boldface);

		// Get user data
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			loggingIn = ProgressDialog.show(
					SignInActivity.this, "", "Signing In", true);
			makeMeRequest(session);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_in, menu);
		return true;
	}

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a 
		// new callback to handle the response.
		Request request = Request.newMeRequest(session, 
				new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
				loggingIn.dismiss();

				// Show views
				//profilePictureView.setVisibility(View.VISIBLE);
				username.setVisibility(View.VISIBLE);
				title.setVisibility(View.VISIBLE);
				findViewById(R.id.login_box).setVisibility(View.VISIBLE);
				findViewById(R.id.picture_frame).setVisibility(View.VISIBLE);

				if (session == Session.getActiveSession()) {
					if (user != null) {
						// Set the id for the ProfilePictureView
						// view that in turn displays the profile picture.
						profilePictureView.setProfileId(user.getId());
						// Set the Textview's text to the user's name.
						username.setText(user.getName());

						// Get Facebook number ID
						fb_id = user.getId();


						// Playing with image stuff
						String imageUrl= "http://graph.facebook.com/" + fb_id + "/picture?type=large";
						new RetrieveImgTask().execute(imageUrl);
					}
				}
				if (response.getError() != null) {
					// Handle errors, will do so later.
				}
			}
		});
		request.executeAsync();
	} 

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onBackPressed() {

	}

	// FB State Managers

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();	

	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	// For circular profile image

	public void convertToCircle () {
		profilePictureView.buildDrawingCache();
		Bitmap bitmap = profilePictureView.getDrawingCache();

		// Copied code
		Bitmap output = Bitmap.createBitmap(profilePictureView.getWidth(),
				profilePictureView.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		profilePictureView.draw(canvas);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, profilePictureView.getWidth(), profilePictureView.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(profilePictureView.getWidth() / 2, profilePictureView.getHeight() / 2,
				profilePictureView.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(output, rect, rect, paint);

		// Set back to picture view
		profilePictureView.setForeground(bitmapToDrawable(output));
	}

	public Bitmap drawableToBitmap (Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap); 
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public Drawable bitmapToDrawable (Bitmap bitmap) {
		return new BitmapDrawable(getResources(),bitmap);
	}

	class RetrieveImgTask extends AsyncTask<String, Void, Void> {
		private Bitmap bitmap;
		private Bitmap output;
		private Exception exception;

		protected Void doInBackground(String... urls) {
			try {
				String imageUrl = urls[0];

				URL newurl = null;
				try {
					newurl = new URL(imageUrl);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				bitmap = null;
				try {
					bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			} catch (Exception e) {
				this.exception = e;
				return null;
			}
		}

		protected void onPostExecute(Void v) {


			if (bitmap != null) {
				Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
						bitmap.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(output);

				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
						bitmap.getWidth() / 2, paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap, rect, rect, paint);

				((ImageView)findViewById(R.id.test_prof_pic)).setImageBitmap(output);
			}
		}
	}

}

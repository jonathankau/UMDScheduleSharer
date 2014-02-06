package com.kau.jonathan.umdschedulesharer;

import java.util.Arrays;

import com.facebook.widget.LoginButton;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleFragment extends Fragment {
	Bitmap schedule;

	public ScheduleFragment(){
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

		// Loads HTML source for just the schedule
		WebView scheduleBrowser = (WebView) rootView.findViewById(R.id.schedule_browser);
		scheduleBrowser.getSettings().setLoadWithOverviewMode(true);
		scheduleBrowser.getSettings().setUseWideViewPort(true);
		scheduleBrowser.getSettings().setSupportZoom(true);
		scheduleBrowser.getSettings().setBuiltInZoomControls(true);
		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			// call something for API Level 11+
			scheduleBrowser.getSettings().setDisplayZoomControls(false);
		}
		scheduleBrowser.loadData(((ScheduleActivity) getActivity()).schedule_src, "text/html", null);

		// Set schedule img		
		/*final ImageView schedule_img = (ImageView) rootView.findViewById(R.id.schedule_img);
		schedule = ((ScheduleActivity) getActivity()).schedule;

		final FrameLayout frame = (FrameLayout) rootView.findViewById(R.id.schedule_frame);

		// Resize and fit bitmap
		Bitmap scaled = getResizedBitmap(schedule, (int) (schedule.getHeight()*1.2), (int) (schedule.getWidth()*1.2));
		schedule_img.setImageBitmap(schedule);
/*		final ViewTreeObserver observer= frame.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				ViewTreeObserver obs = frame.getViewTreeObserver();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		            obs.removeOnGlobalLayoutListener(this);
		        } else {
		            obs.removeGlobalOnLayoutListener(this);
		        }

				int new_height = frame.getHeight();
				int new_width = (int) ((new_height / schedule.getHeight()) * schedule.getWidth() * 1.5);

				Toast.makeText(getActivity(), "Height: " + new_height + " Width: " + new_width, Toast.LENGTH_SHORT).show();

				Bitmap scaled = getResizedBitmap(schedule, new_height, new_width);

				schedule_img.setImageBitmap(scaled);
			}
		});*/

		// Set facebook permissions
		Button share = (Button)rootView.findViewById(R.id.fb_share);
		//share.setPublishPermissions(Arrays.asList("publish_actions", "user_photos", "read_stream", "access_token"));


		// Generate typefaces
		final Typeface face=Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lato-Reg.ttf");
		final Typeface boldface=Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lato-Bol.ttf");

		// Set fonts
		TextView username = (TextView) rootView.findViewById(R.id.selection_user_name);
		//Button share = (Button) rootView.findViewById(R.id.fb_share);

		username.setTypeface(face);
		share.setTypeface(face);

		return rootView;
	}



	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}



}

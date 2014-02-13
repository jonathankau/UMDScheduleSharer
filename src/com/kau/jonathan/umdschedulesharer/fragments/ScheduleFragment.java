package com.kau.jonathan.umdschedulesharer.fragments;

import com.kau.jonathan.umdschedulesharer.R;
import com.kau.jonathan.umdschedulesharer.R.id;
import com.kau.jonathan.umdschedulesharer.R.layout;
import com.kau.jonathan.umdschedulesharer.activities.ScheduleActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

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
		scheduleBrowser.loadData(((ScheduleActivity) getActivity()).schedule_src, "text/html", "UTF-8");// 

		
		// Set facebook permissions
		Button share = (Button)rootView.findViewById(R.id.fb_share);


		// Generate typefaces
		final Typeface face=Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lato-Reg.ttf");
		final Typeface boldface=Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lato-Bol.ttf");

		// Set fonts
		TextView username = (TextView) rootView.findViewById(R.id.selection_user_name);

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

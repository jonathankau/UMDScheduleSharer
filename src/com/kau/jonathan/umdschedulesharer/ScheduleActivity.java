package com.kau.jonathan.umdschedulesharer;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.TextView;

public class ScheduleActivity extends ActionBarActivity {
	static final String HTML_SOURCE = "scheduleSource";
	ViewPager mViewPager;
	PagerAdapter mPageAdapter;
	final String[] tabNames = {"Schedule","Classes","Friends"};
	Bitmap schedule;
	String schedule_src;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		if (savedInstanceState != null) {
			schedule_src = savedInstanceState.getString(HTML_SOURCE);
		} else {
			// Process incoming intent data
			schedule_src = getIntent().getStringExtra("SOURCE_CODE");

			// Save data for later use
			SharedPreferences prefs = this.getSharedPreferences("com.kau.jonathan.umdschedulesharer", Context.MODE_PRIVATE);
			prefs.edit().putInt("com.kau.jonathan.umdschedulesharer.obtained_schedule", 1).commit();
			prefs.edit().putString("com.kau.jonathan.umdschedulesharer.schedule_code", schedule_src).commit();
		}

		// Access layout elements
		mViewPager = (ViewPager) findViewById(R.id.pager);

		mViewPager.setOffscreenPageLimit(2);

		mViewPager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						getSupportActionBar().setSelectedNavigationItem(position);
					}
				});

		mPageAdapter = new PagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPageAdapter);

		// Make magic
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}

		// Instantiate taps
		final ActionBar actionBar = getSupportActionBar();

		actionBar.setHomeButtonEnabled(false);


		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
		View homeIcon = findViewById(android.R.id.home);
		if(homeIcon != null) {
			((View) homeIcon.getParent()).setClickable(false);
		}

		actionBar.setCustomView(R.layout.actionbar);

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		// Set typeface of text elements in page
		final Typeface face=Typeface.createFromAsset(this.getAssets(),
				"fonts/Lato-Reg.ttf");
		final Typeface lightface=Typeface.createFromAsset(this.getAssets(),
				"fonts/Lato-Lig.ttf");







		// Set typefaces

		TextView title = (TextView)findViewById(R.id.main_title);
		title.setTypeface(face);

		// Add 3 tabs, specifying the tab's text and TabListener


		for(int i = 0; i< 3; i++) {
			LayoutInflater inflater = LayoutInflater.from(this);
			View customView = inflater.inflate(R.layout.tab_title, null);

			SpannableString formattedString = new SpannableString(tabNames[i]);
			formattedString.setSpan(face, 0, tabNames[i].length(), 0);	
			formattedString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_red)), 0, tabNames[i].length(), 0);

			TextView tab_title = (TextView) customView.findViewById(R.id.action_custom_title);
			tab_title.setText(tabNames[i]);
			tab_title.setTypeface(lightface);

			// This OnClickListener resolves the problem in which the custom TextView tab
			// does not allow the user to click on the actual tab title.
			tab_title.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					TextView tv = (TextView) v;
					String s = tv.getText().toString();
					int i;

					for(i = 0; i < 3; i++) {
						if(s.equals(tabNames[i])){
							break;
						}
					}

					mViewPager.setCurrentItem(i);

				}});

			actionBar.addTab(
					actionBar.newTab()
					.setCustomView(customView)
					.setTabListener(tabListener));
		}


	}

	// Since this isn't an object collection, use a FragmentPagerAdapter
	public class PagerAdapter extends FragmentPagerAdapter {
		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment;

			if(i == 0) {
				fragment = new ScheduleFragment();
			} else {
				fragment = new DummySectionFragment();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "OBJECT " + (position + 1);
		}
	}


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Bring up submenu items
		SubMenu subMenu = menu.addSubMenu("Navigation Menu");
		subMenu.add(0, 1, Menu.NONE, "Update Schedule").setIcon(R.drawable.ic_action_refresh);
		subMenu.add(0, 2, Menu.NONE, "Settings").setIcon(R.drawable.ic_action_settings);

		MenuItem overflow = subMenu.getItem();
		overflow.setIcon(R.drawable.ic_action_overflow);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			overflow.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		//		Intent back = new Intent(this, MainActivity.class);
		//
		//		// Save data for later use
		//		SharedPreferences prefs = this.getSharedPreferences("com.kau.jonathan.umdschedulesharer", Context.MODE_PRIVATE);
		//		prefs.edit().putInt("com.kau.jonathan.umdschedulesharer.obtained_schedule", 0).commit();
		//		prefs.edit().putString("com.kau.jonathan.umdschedulesharer.schedule_code", schedule_src).commit();
		//
		//		startActivity(back);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			return true;
		case 1: // Update schedule
			Intent back = new Intent(this, MainActivity.class);

			// Save data for later use
			SharedPreferences prefs = this.getSharedPreferences("com.kau.jonathan.umdschedulesharer", Context.MODE_PRIVATE);
			prefs.edit().putInt("com.kau.jonathan.umdschedulesharer.obtained_schedule", 0).commit();
			prefs.edit().putString("com.kau.jonathan.umdschedulesharer.schedule_code", schedule_src).commit();

			startActivity(back);
			return true;
		case 2: // Settings
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		savedInstanceState.putString(HTML_SOURCE, schedule_src);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

}

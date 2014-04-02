package com.kau.jonathan.umdschedulesharer.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kau.jonathan.umdschedulesharer.R;
import com.kau.jonathan.umdschedulesharer.activities.ScheduleActivity;
import com.kau.jonathan.umdschedulesharer.adapters.FriendListAdapter;
import com.kau.jonathan.umdschedulesharer.models.FriendDataHolder;

public class FriendsFragment extends ListFragment {
	private ProgressBar bar;
	private FrameLayout frame;
	private LinearLayout friends_frag;
	private TextView no_internet;
	private TextView tap_to_retry;
	private CheckBox allow_sharing;
	Set<String> classes;

	public FriendsFragment(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

		bar = (ProgressBar) rootView.findViewById(R.id.friends_progress_bar);
		frame = (FrameLayout) rootView.findViewById(R.id.progress_frame);
		friends_frag = (LinearLayout) rootView.findViewById(R.id.friends_frag);
		no_internet = (TextView) rootView.findViewById(R.id.no_internet);
		tap_to_retry = (TextView) rootView.findViewById(R.id.tap_to_retry);
		allow_sharing = (CheckBox) rootView.findViewById(R.id.allow_sharing);
		classes = ((ScheduleActivity) getActivity()).classes.keySet();

		// Generate typefaces
		final Typeface face=Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lato-Reg.ttf");
		final Typeface boldface=Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lato-Bol.ttf");

		no_internet.setTypeface(face);
		tap_to_retry.setTypeface(face);
		allow_sharing.setTypeface(face);

		// Set listener for checkbox
		allow_sharing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((CheckBox)v).isChecked()){
					new UpdateShareTask().execute(true);
				}else{
					new UpdateShareTask().execute(false);
				}
			}
		});

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(isNetworkAvailable()) {
			new RetrieveFriendsTask().execute();
		} else {
			no_internet.setVisibility(View.VISIBLE);
			tap_to_retry.setVisibility(View.VISIBLE);
			this.getListView().setVisibility(View.GONE);
			allow_sharing.setVisibility(View.GONE);

			friends_frag.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					no_internet.setVisibility(View.GONE);
					tap_to_retry.setVisibility(View.GONE);

					new RetrieveFriendsTask().execute();
				}

			});
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	private class UpdateShareTask extends AsyncTask <Boolean, Void, Void> {

		@Override
		protected Void doInBackground(Boolean... params) {

			boolean allowSharing = params[0];


			// Instantiate UMD Social Scheduler Session
			HttpClient httpClient = new DefaultHttpClient();  
			String url = "http://www.umdsocialscheduler.com/access?access_token=" + ((ScheduleActivity) getActivity()).accessToken;
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					entity.writeTo(out);
					out.close();
				} else {
					// handle bad response
				}

				response.getEntity().consumeContent();
			} catch (ClientProtocolException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			}

			// Send GET request to enable/disable sharing
			if(allowSharing) {
				url = "http://www.umdsocialscheduler.com/enable_sharing?";
				httpGet = new HttpGet(url);
				try {
					HttpResponse response = httpClient.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						entity.writeTo(out);
						out.close();
					} else {
						// handle bad response
					}

					response.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					// handle exception
				} catch (IOException e) {
					// handle exception
				}
			} else {
				url = "http://www.umdsocialscheduler.com/disable_sharing?";
				httpGet = new HttpGet(url);
				try {
					HttpResponse response = httpClient.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						entity.writeTo(out);
						out.close();
					} else {
						// handle bad response
					}

					response.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					// handle exception
				} catch (IOException e) {
					// handle exception
				}
			}

			return null;
		}		

	}

	private class RetrieveFriendsTask extends AsyncTask <Void,Void,Void>{
		String url;
		String responseStr;
		String dataStr;
		boolean success = false;
		boolean allowSharing = true;
		HashMap<String, FriendDataHolder> parsedData = new HashMap<String, FriendDataHolder>();

		@Override
		protected void onPreExecute(){
			bar.setVisibility(View.VISIBLE);
			frame.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			HttpClient httpClient = new DefaultHttpClient();  
			url = "http://www.umdsocialscheduler.com/access?access_token=" + ((ScheduleActivity) getActivity()).accessToken;
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					entity.writeTo(out);
					out.close();
					responseStr = out.toString();

					JSONObject data = new JSONObject(responseStr);
					allowSharing = data.getJSONObject("data").getBoolean("share");

					// do something with response 
					success = data.getBoolean("success");
				} else {
					// handle bad response
				}

				response.getEntity().consumeContent();
			} catch (ClientProtocolException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			url = "http://www.umdsocialscheduler.com/friends_with_app?";
			HttpGet getData = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(getData);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					entity.writeTo(out);
					out.close();
					dataStr = out.toString();

					// Parse JSON data
					parsedData = parseString(dataStr);
				} else {
					// handle bad response
				}

				response.getEntity().consumeContent();
			} catch (ClientProtocolException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for(String s: classes) {

				url = "http://www.umdsocialscheduler.com/friends?term=" + ((ScheduleActivity) getActivity()).converted_term + "&course=" + s;
				getData = new HttpGet(url);
				try {
					HttpResponse response = httpClient.execute(getData);
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						entity.writeTo(out);
						out.close();
						dataStr = out.toString();
						// Parse response

						JSONObject mainObject = new JSONObject(dataStr);

						boolean success = mainObject.getBoolean("success");

						if(success) {
							JSONArray classArray = mainObject.getJSONArray("data");


							for (int i = 0; i < classArray.length(); i++) {
								JSONObject row = classArray.getJSONObject(i);

								String name = row.getString("name");
								String fbid = row.getString("fbid");

								FriendDataHolder fdh = parsedData.get(name);
								Set<String> personClasses = fdh.getClasses();
								personClasses.add(s);
								fdh.setClasses(personClasses);
								parsedData.put(name, fdh);
							}

						}
					} else {
						// handle bad response
					}

					response.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					// handle exception
				} catch (IOException e) {
					// handle exception
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			bar.setVisibility(View.GONE);
			frame.setVisibility(View.GONE);

			if(allowSharing) {
				allow_sharing.setChecked(true);
			} else {
				allow_sharing.setChecked(false);
			}

			if(success) {				
				LinkedList<FriendDataHolder> data = new LinkedList<FriendDataHolder>();
				data.addAll(parsedData.values());
				Collections.sort(data);

				FriendListAdapter adapter = new FriendListAdapter(FriendsFragment.this.getActivity(), data, 
						((ScheduleActivity) getActivity()).classes.keySet(), ((ScheduleActivity) getActivity()).accessToken);
				setListAdapter(adapter);

				FriendsFragment.this.getListView().setVisibility(View.VISIBLE);
				allow_sharing.setVisibility(View.VISIBLE);
				no_internet.setVisibility(View.GONE);
				tap_to_retry.setVisibility(View.GONE);	

			} else { // Display retry window
				FriendsFragment.this.getListView().setVisibility(View.GONE);
				allow_sharing.setVisibility(View.GONE);
				no_internet.setVisibility(View.VISIBLE);
				tap_to_retry.setVisibility(View.VISIBLE);				

				friends_frag.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						no_internet.setVisibility(View.GONE);
						tap_to_retry.setVisibility(View.GONE);

						new RetrieveFriendsTask().execute();
					}

				});				
			}
		}
	}

	public HashMap<String, FriendDataHolder> parseString(String data) throws JSONException {
		HashMap<String, FriendDataHolder> output = new LinkedHashMap<String, FriendDataHolder>();
		JSONObject mainObject = new JSONObject(data);
		boolean success = mainObject.getBoolean("success");

		if(success) {
			JSONArray friendArray = mainObject.getJSONArray("data");

			for (int i = 0; i < friendArray.length(); i++) {
				JSONObject row = friendArray.getJSONObject(i);
				FriendDataHolder fdh = new FriendDataHolder(row.getString("name"), row.getLong("fbid"), row.getBoolean("share"));
				fdh.setClasses(new HashSet<String>());
				output.put(fdh.getName(), fdh);
			}
		}

		return output;
	}

}

package com.kau.jonathan.umdschedulesharer.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kau.jonathan.umdschedulesharer.R;
import com.kau.jonathan.umdschedulesharer.activities.ScheduleActivity;
import com.kau.jonathan.umdschedulesharer.adapters.ClassesAdapter;
import com.kau.jonathan.umdschedulesharer.models.ClassDataHolder;
import com.kau.jonathan.umdschedulesharer.models.ClassDataHolder.FriendSectionData;

public class ClassesFragment extends ListFragment {
	private ProgressBar bar;
	private FrameLayout frame;
	private LinearLayout classes_frag;
	private TextView no_internet;
	private TextView tap_to_retry;
	HashMap<String, String> classes;

	public ClassesFragment(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_classes, container, false);

		bar = (ProgressBar) rootView.findViewById(R.id.classes_progress_bar);
		frame = (FrameLayout) rootView.findViewById(R.id.classes_progress_frame);
		classes_frag = (LinearLayout) rootView.findViewById(R.id.classes_frag);
		no_internet = (TextView) rootView.findViewById(R.id.no_internet);
		tap_to_retry = (TextView) rootView.findViewById(R.id.tap_to_retry);

		classes = ((ScheduleActivity)getActivity()).classes;

		// Generate typefaces
		final Typeface face=Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lato-Reg.ttf");
		final Typeface boldface=Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Lato-Bol.ttf");

		no_internet.setTypeface(face);
		tap_to_retry.setTypeface(face);


		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(isNetworkAvailable()) {
			new RetrieveDataTask().execute();
		} else {
			no_internet.setVisibility(View.VISIBLE);
			tap_to_retry.setVisibility(View.VISIBLE);
			this.getListView().setVisibility(View.GONE);
			
			classes_frag.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					no_internet.setVisibility(View.GONE);
					tap_to_retry.setVisibility(View.GONE);
					
					new RetrieveDataTask().execute();
				}
				
			});
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	class RetrieveDataTask extends AsyncTask<Void, Void, Void> {
		String responseStr;
		String url;
		String dataStr;
		boolean success = false;

		LinkedList<ClassDataHolder> classSections = new LinkedList<ClassDataHolder>();

		@Override
		protected void onPreExecute(){
			bar.setVisibility(View.VISIBLE);
			frame.setVisibility(View.VISIBLE);
		}

		protected Void doInBackground(Void...urls) {

			//if(isNetworkAvailable()) {

				url = "http://www.umdsocialscheduler.com/access?access_token=" + ((ScheduleActivity) getActivity()).accessToken;
				HttpGet httpGet = new HttpGet(url);
				HttpParams httpParameters = new BasicHttpParams();

				// Set the timeout in milliseconds until a connection is established.
				// The default value is zero, that means the timeout is not used. 
				int timeoutConnection = 3000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 5000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


				HttpClient httpClient = new DefaultHttpClient(httpParameters);  
				try {
					HttpResponse response = httpClient.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						entity.writeTo(out);
						out.close();
						responseStr = out.toString();
						// do something with response 
						success = true;
					} else {
						success = false;
					}

					response.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					success = false;
				} catch (IOException e) {
					success = false;
				}

				for(String s: classes.keySet()) {
					String section = classes.get(s);

					url = "http://www.umdsocialscheduler.com/friends?term=201401&course=" + s;
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
							// Parse response

							JSONObject mainObject = new JSONObject(dataStr);

							boolean success = mainObject.getBoolean("success");

							if(success) {
								JSONArray classArray = mainObject.getJSONArray("data");

								ClassDataHolder holder;
								LinkedList<FriendSectionData> data = new LinkedList<FriendSectionData>();

								for (int i = 0; i < classArray.length(); i++) {
									JSONObject row = classArray.getJSONObject(i);

									FriendSectionData friend = new FriendSectionData();
									friend.setName(row.getString("name"));
									friend.setFacebookId(row.getString("fbid"));
									friend.setSection(row.getString("section"));

									data.add(friend);
								}

								holder = new ClassDataHolder(s, section, data);
								classSections.add(holder);
							}
						} else {
							success = false;
						}

						response.getEntity().consumeContent();
					} catch (ClientProtocolException e) {
						success = false;
					} catch (IOException e) {
						success = false;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						success = false;
					}

				}

			//}
			return null;

		}

		protected void onPostExecute(Void v) {	  

			//Toast.makeText(getActivity(), responseStr, Toast.LENGTH_SHORT).show();
			//Toast.makeText(getActivity(), dataStr, Toast.LENGTH_SHORT).show();

			bar.setVisibility(View.GONE);
			frame.setVisibility(View.GONE);

			if(success) {
				ClassesAdapter adapter = new ClassesAdapter(ClassesFragment.this.getActivity(), classSections);
				setListAdapter(adapter);

				ClassesFragment.this.getListView().setVisibility(View.VISIBLE);
				no_internet.setVisibility(View.GONE);
				tap_to_retry.setVisibility(View.GONE);
				
				classes_frag.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						no_internet.setVisibility(View.GONE);
						tap_to_retry.setVisibility(View.GONE);
						
						new RetrieveDataTask().execute();
					}
					
				});
			} else {
				ClassesFragment.this.getListView().setVisibility(View.GONE);
				no_internet.setVisibility(View.VISIBLE);
				tap_to_retry.setVisibility(View.VISIBLE);
			}
		}
	}

}

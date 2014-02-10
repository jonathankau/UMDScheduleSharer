package com.kau.jonathan.umdschedulesharer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kau.jonathan.umdschedulesharer.Models.FriendDataHolder;

public class FriendsFragment extends ListFragment {
	private ProgressBar bar;
	private FrameLayout frame;
	Set<String> classes;

	public FriendsFragment(){
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// do something with the data

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

		bar = (ProgressBar) rootView.findViewById(R.id.friends_progress_bar);
		frame = (FrameLayout) rootView.findViewById(R.id.progress_frame);
		
		classes = ((ScheduleActivity) getActivity()).classes.keySet();

		new RetrieveFriendsTask().execute();

		return rootView;
	}

	private class RetrieveFriendsTask extends AsyncTask <Void,Void,Void>{
		String url;
		String responseStr;
		String dataStr;
		LinkedList<FriendDataHolder> parsedData = new LinkedList<FriendDataHolder>();

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
					// do something with response 
				} else {
					// handle bad response
				}
			} catch (ClientProtocolException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
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
			} catch (ClientProtocolException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (FriendDataHolder fdh: parsedData) {		
				Set<String> sharedClasses = new HashSet<String>();
				
				url = "http://www.umdsocialscheduler.com/schedule?term=201401&fbid=" + fdh.getFacebookID();
				getData = new HttpGet(url);
				try {
					HttpResponse response = httpClient.execute(getData);
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						entity.writeTo(out);
						out.close();
						String dataStr = out.toString();

						// Parse JSON data
						JSONObject mainObject = new JSONObject(dataStr);
						boolean success = mainObject.getBoolean("success");

						if(success) {
							JSONArray classArray = mainObject.getJSONArray("data");

							for (int i = 0; i < classArray.length(); i++) {
								JSONObject row = classArray.getJSONObject(i);
								String className = row.getString("course_code");

								if(classes.contains(className)) sharedClasses.add(className);
							}
						}
					} else {
						// handle bad response
					}
				} catch (ClientProtocolException e) {
					// handle exception
				} catch (IOException e) {
					// handle exception
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// Set them for the fdh
				fdh.setClasses(sharedClasses);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
			Toast.makeText(getActivity(), responseStr, Toast.LENGTH_SHORT).show();
			Toast.makeText(getActivity(), dataStr, Toast.LENGTH_SHORT).show();
			Toast.makeText(getActivity(), ((ScheduleActivity) getActivity()).classes.keySet().toString(), Toast.LENGTH_SHORT).show();
			bar.setVisibility(View.GONE);
			frame.setVisibility(View.GONE);

			PicassoSampleAdapter adapter = new PicassoSampleAdapter(FriendsFragment.this.getActivity(), parsedData, 
					((ScheduleActivity) getActivity()).classes.keySet(), ((ScheduleActivity) getActivity()).accessToken);
			setListAdapter(adapter);
		}
	}

	public LinkedList<FriendDataHolder> parseString(String data) throws JSONException {
		LinkedList<FriendDataHolder> output = new LinkedList<FriendDataHolder>();
		JSONObject mainObject = new JSONObject(data);
		boolean success = mainObject.getBoolean("success");

		if(success) {
			JSONArray friendArray = mainObject.getJSONArray("data");
			
			for (int i = 0; i < friendArray.length(); i++) {
			    JSONObject row = friendArray.getJSONObject(i);
			    output.add(new FriendDataHolder(row.getString("name"), row.getLong("fbid"), row.getBoolean("share")));
			}
			
			Collections.sort(output);
		}

		return output;
	}

}

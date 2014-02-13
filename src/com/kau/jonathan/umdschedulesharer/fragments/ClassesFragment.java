package com.kau.jonathan.umdschedulesharer.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kau.jonathan.umdschedulesharer.R;
import com.kau.jonathan.umdschedulesharer.R.id;
import com.kau.jonathan.umdschedulesharer.R.layout;
import com.kau.jonathan.umdschedulesharer.activities.ScheduleActivity;
import com.kau.jonathan.umdschedulesharer.adapters.ClassesAdapter;
import com.kau.jonathan.umdschedulesharer.models.ClassDataHolder;
import com.kau.jonathan.umdschedulesharer.models.ClassDataHolder.FriendSectionData;

public class ClassesFragment extends ListFragment {
	private ProgressBar bar;
	private FrameLayout frame;
	HashMap<String, String> classes;

	public ClassesFragment(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_classes, container, false);

		bar = (ProgressBar) rootView.findViewById(R.id.classes_progress_bar);
		frame = (FrameLayout) rootView.findViewById(R.id.classes_progress_frame);
		
		classes = ((ScheduleActivity)getActivity()).classes;

		//Toast.makeText(getActivity(), classes.toString(), Toast.LENGTH_SHORT).show();
		new RetrieveDataTask().execute();

		return rootView;
	}

	class RetrieveDataTask extends AsyncTask<Void, Void, Void> {
		String responseStr;
		String url;
		String dataStr;
		
		LinkedList<ClassDataHolder> classSections = new LinkedList<ClassDataHolder>();
		
		@Override
		protected void onPreExecute(){
			bar.setVisibility(View.VISIBLE);
			frame.setVisibility(View.VISIBLE);
		}

		protected Void doInBackground(Void...urls) {

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
				
				response.getEntity().consumeContent();
			} catch (ClientProtocolException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
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

		protected void onPostExecute(Void v) {	  

			//Toast.makeText(getActivity(), responseStr, Toast.LENGTH_SHORT).show();
			//Toast.makeText(getActivity(), dataStr, Toast.LENGTH_SHORT).show();
			
			bar.setVisibility(View.GONE);
			frame.setVisibility(View.GONE);

			ClassesAdapter adapter = new ClassesAdapter(ClassesFragment.this.getActivity(), classSections);
			setListAdapter(adapter);
		}
	}

}

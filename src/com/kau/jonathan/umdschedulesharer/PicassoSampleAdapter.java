package com.kau.jonathan.umdschedulesharer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kau.jonathan.umdschedulesharer.Models.FriendDataHolder;
import com.squareup.picasso.Picasso;

final class PicassoSampleAdapter extends BaseAdapter {
	private final LayoutInflater inflater;
	LinkedList<FriendDataHolder> data;
	Context context;
	Set<String> classes;
	String accessToken;
	Typeface face;
	Typeface lightface;

	public PicassoSampleAdapter(Context context, LinkedList<FriendDataHolder> parsed, Set<String> classes, String accessToken) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		data = parsed;
		this.classes = classes;
		this.accessToken = accessToken;
		

		// Set typeface of text elements in page
		face=Typeface.createFromAsset(context.getAssets(),
				"fonts/Lato-Reg.ttf");
		lightface=Typeface.createFromAsset(context.getAssets(),
				"fonts/Lato-Lig.ttf");
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override public int getCount() {
		return data.size();
	}

	@Override public FriendDataHolder getItem(int position) {
		return data.get(position);
	}

	@Override public long getItemId(int position) {
		return ((FriendDataHolder) data.get(position)).getFacebookID();
	}

	@Override public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.friend_list_item, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.friend_pic);
			holder.text = (TextView) view.findViewById(R.id.friend_name);
			holder.num_classes = (TextView) view.findViewById(R.id.shared_classes);			
			holder.text.setTypeface(face);
			holder.num_classes.setTypeface(lightface);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		// Build Shared Classes text
		FriendDataHolder fdh = getItem(position);
		
		String classesText = fdh.getClassesText();		
		if(classesText != "") {
			holder.num_classes.setText(classesText);
		} else {
			holder.num_classes.setText("No mutual classes");
		}

		holder.text.setText(fdh.getName());
		

		// Trigger the download of the URL asynchronously into the image view.
		String imageUrl= "http://graph.facebook.com/" + getItemId(position) + "/picture?type=square";
		
		Picasso.with(context)
		.load(imageUrl)
		.placeholder(R.drawable.fb_default)
		.into(holder.image);

		return view;
	}

//	private class RetrieveClassesTask extends AsyncTask <SpecialParams,Void,Void>{
//		String classesText = "";
//		Set<String> output;
//		boolean success;
//		SpecialParams p;
//		int position;
//
//		@Override
//		protected Void doInBackground(SpecialParams... p) {
//			this.p = p[0];
//			this.position = p[0].position;
//			long facebook_id = getItemId(position);		
//
//			HttpClient httpClient = new DefaultHttpClient();  
//
//			String url = "http://www.umdsocialscheduler.com/access?access_token=" + accessToken;
//			HttpGet httpGet = new HttpGet(url);
//			try {
//				HttpResponse response = httpClient.execute(httpGet);
//				StatusLine statusLine = response.getStatusLine();
//				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//					HttpEntity entity = response.getEntity();
//					ByteArrayOutputStream out = new ByteArrayOutputStream();
//					entity.writeTo(out);
//					out.close();
//					String responseStr = out.toString();
//					// do something with response 
//				} else {
//					// handle bad response
//				}
//			} catch (ClientProtocolException e) {
//				// handle exception
//			} catch (IOException e) {
//				// handle exception
//			}
//
//			url = "http://www.umdsocialscheduler.com/schedule?term=201401&fbid=" + facebook_id;
//			HttpGet getData = new HttpGet(url);
//			try {
//				HttpResponse response = httpClient.execute(getData);
//				StatusLine statusLine = response.getStatusLine();
//				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//					HttpEntity entity = response.getEntity();
//					ByteArrayOutputStream out = new ByteArrayOutputStream();
//					entity.writeTo(out);
//					out.close();
//					String dataStr = out.toString();
//
//					// Parse JSON data
//					output = new HashSet<String>();
//					JSONObject mainObject = new JSONObject(dataStr);
//					success = mainObject.getBoolean("success");
//
//					if(success) {
//						JSONArray classArray = mainObject.getJSONArray("data");
//
//						for (int i = 0; i < classArray.length(); i++) {
//							JSONObject row = classArray.getJSONObject(i);
//							String className = row.getString("course_code");
//
//							if(classes.contains(className)) output.add(className);
//						}
//
//						Iterator<String> iterator = output.iterator();
//
//						while(iterator.hasNext()) {
//							String s = iterator.next();
//
//							classesText = classesText + s;
//							if(iterator.hasNext()) classesText = classesText  + ", ";
//						}
//					}
//				} else {
//					// handle bad response
//				}
//			} catch (ClientProtocolException e) {
//				// handle exception
//			} catch (IOException e) {
//				// handle exception
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			if(classesText != "") {
//				p.num_classes.setText(classesText);
//			}
//		}
//	}
//
//	static class SpecialParams {
//		int position;
//		TextView num_classes;
//	}

	static class ViewHolder {
		ImageView image;
		TextView text;
		TextView num_classes;
	}
}

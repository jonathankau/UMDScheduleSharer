package com.kau.jonathan.umdschedulesharer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ClassesFragment extends Fragment {

	public ClassesFragment(){
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_classes, container, false);

		new RetrieveDataTask().execute();
		
		return rootView;
	}
	
	class RetrieveDataTask extends AsyncTask<Void, Void, Void> {
		String responseStr;
		String url;
		String dataStr;
		
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
			} catch (ClientProtocolException e) {
			    // handle exception
			} catch (IOException e) {
			    // handle exception
			}
			
			url = "http://www.umdsocialscheduler.com/friends?term=201401&course=CMSC412&section=0101";
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
			        // do something with response 
			    } else {
			        // handle bad response
			    }
			} catch (ClientProtocolException e) {
			    // handle exception
			} catch (IOException e) {
			    // handle exception
			}
			
			return null;
			
		}
		
		protected void onPostExecute(Void v) {	  
	        //Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
	        //Toast.makeText(getActivity(), responseStr, Toast.LENGTH_SHORT).show();
	        //Toast.makeText(getActivity(), dataStr, Toast.LENGTH_SHORT).show();
		}
	}

}

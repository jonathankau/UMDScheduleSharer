package com.kau.jonathan.umdschedulesharer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
	//
	//	@Override
	//	public boolean isEnabled(int position) {
	//		return true; // false to disable click
	//	}

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
			holder.friend_schedule = (Button) view.findViewById(R.id.friend_schedule);
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
			holder.num_classes.setText("No Mutual Classes");
		}

		holder.text.setText(fdh.getName());

		// Show button if schedule sharing allowed
		if(fdh.isAllowShare()) {
			holder.friend_schedule.setVisibility(View.VISIBLE);			

			final long fb_id = getItemId(position);

			OnClickListener yourClickListener = new OnClickListener() {
				public void onClick(View v) {

					// Open image dialog
					Dialog dialog = new Dialog(context);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.dialog_image);


					Display display =((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

					int DisplayWidth = (int) (display.getWidth() * .90);

					// Set your dialog width and height dynamically as per your screen.

					Window window = dialog.getWindow();
					window.setLayout(DisplayWidth , LayoutParams.WRAP_CONTENT);
					window.setGravity(Gravity.CENTER);

					dialog.show();

					SpecialParams params = new SpecialParams();
					params.dialog = dialog;
					params.fb_id = fb_id;

					new RetrieveScheduleImg().execute(params);


				}
			};

			holder.friend_schedule.setOnClickListener(yourClickListener);
		} else {
			holder.friend_schedule.setVisibility(View.GONE);
		}


		// Trigger the download of the URL asynchronously into the image view.
		String imageUrl= "http://graph.facebook.com/" + getItemId(position) + "/picture?type=square";

		Picasso.with(context)
		.load(imageUrl)
		.placeholder(R.drawable.fb_default)
		.into(holder.image);

		return view;
	}

	private class RetrieveScheduleImg extends AsyncTask <SpecialParams,Void,Void>{
		Dialog dialog;
		long fb_id;
		Bitmap img;
		
		@Override
		protected void onPreExecute(){
			//dialog.findViewById(R.id.dialog_progress).setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(SpecialParams... p) {
			dialog = p[0].dialog;
			fb_id = p[0].fb_id;

			HttpClient httpClient = new DefaultHttpClient();  
			String url = "http://www.umdsocialscheduler.com/access?access_token=" + accessToken;
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					entity.writeTo(out);
					out.close();
					String responseStr = out.toString();
					// do something with response 
				} else {
					// handle bad response
				}
			} catch (ClientProtocolException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			}

			String imageUrl = "http://www.umdsocialscheduler.com/schedule_image?term=201401&fbid=" + fb_id;
			httpGet = new HttpGet(imageUrl);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					entity.writeTo(out);
					out.close();
					// do something with response 
					
					Options options = new BitmapFactory.Options();
				    options.inScaled = false;

					img = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length, options);
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

		@Override
		protected void onPostExecute(Void result) {
			// Trigger the download of the URL asynchronously into the image view.


			TouchImageView sched = (TouchImageView) dialog.findViewById(R.id.friend_img);

			if(img != null) {
				
				Display display =((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

				int displayWidth = (int) (display.getWidth() * .90);
				float floatHeight = (((float) displayWidth / (float) img.getWidth()) * (float) img.getHeight());
				int displayHeight = (int) floatHeight;
				
				Bitmap scaledBitmap = Bitmap.createBitmap(displayWidth, displayHeight, Config.ARGB_8888);

				float ratioX = displayWidth / (float) img.getWidth();
				float ratioY = displayHeight / (float) img.getHeight();
				float middleX = displayWidth / 2.0f;
				float middleY = displayHeight / 2.0f;

				Matrix scaleMatrix = new Matrix();
				scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

				Canvas canvas = new Canvas(scaledBitmap);
				canvas.setMatrix(scaleMatrix);
				canvas.drawBitmap(img, middleX - img.getWidth() / 2, middleY - img.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
				
				sched.setImageBitmap(scaledBitmap);

				
				Window window = dialog.getWindow();
				window.setLayout(displayWidth, displayHeight);
				window.setGravity(Gravity.CENTER);
			}

			dialog.findViewById(R.id.dialog_progress).setVisibility(View.GONE);
		}
	}

	static class SpecialParams {
		Dialog dialog;
		long fb_id;
	}

	static class ViewHolder {
		Button friend_schedule;
		ImageView image;
		TextView text;
		TextView num_classes;
	}
}

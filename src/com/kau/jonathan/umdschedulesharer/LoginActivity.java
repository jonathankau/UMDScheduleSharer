package com.kau.jonathan.umdschedulesharer;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;



@SuppressLint("JavascriptInterface")
public class LoginActivity extends Activity {
	public boolean loginCompleted = false;
	public String lastFileSaved = "";
	public static final String headerString = "<center><font size=\"+2\"><b><font color=\"darkblue\">Term</font>";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.share_image:
			share_image();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().startSync();
		CookieManager.getInstance().setAcceptCookie(true);

		String loadUrl = "https://mobilemy.umd.edu/portal/server.pt/gateway/PTARGS_0_340574_368_211_0_43/https%3B/www.sis.umd.edu/testudo/studentSched?term=201401";

		// Initialize the WebView and edit settings
		WebView browser = (WebView) findViewById(R.id.login_page);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.getSettings().setBuiltInZoomControls(true);
		browser.getSettings().setDomStorageEnabled(true);
		browser.clearCache(true);
		browser.clearHistory();

		// Sets the webview client for the initial my.umd.edu page
		browser.setWebViewClient(new WebViewClient() {  
			@Override  
			public boolean shouldOverrideUrlLoading(WebView view, String url)  
			{  
				return false; 
			}  

			@Override
			public void onPageFinished(WebView view, String url) {

				// Wait for completed login using UID       
				CookieManager manager = CookieManager.getInstance();

				if(manager.getCookie(url) != null && manager.getCookie(url).contains("true")) {
					// Once user logs in, turn the webview invisible and swap with a loading screen
					findViewById(R.id.login_page).setVisibility(View.GONE);
					findViewById(R.id.login_progress).setVisibility(View.VISIBLE);

					/* Register a new JavaScript interface called HTMLOUT */
					view.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

					view.getSettings().setLoadWithOverviewMode(true);
					view.getSettings().setUseWideViewPort(true);

					// Sets the webview client for loading and accessing the HTML source of the schedule
					view.setWebViewClient(new WebViewClient() {
						@Override  
						public boolean shouldOverrideUrlLoading(WebView view, String url)  
						{  
							return false; 
						}  

						@Override
						public void onPageFinished(WebView view, String url) {
							/* This calls inject JavaScript into the page which just finished loading. */
							
						}
					});

					// Load the actual schedule page
					view.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				}
			}
		});

		// Load the my.umd.edu login page url
		CookieManager.getInstance().removeSessionCookie();
		browser.loadUrl(loadUrl);
	}


	// Captures the HTML source of the schedule webpage
	class MyJavaScriptInterface
	{
		public void processHTML(String html)
		{
			// Determines the beginning and end of just the schedule
			int beginIndex = html.indexOf(headerString);
			int endIndex = html.indexOf("</table>", beginIndex) + 7;

			// Crops the substring of HTML source
			String scheduleTable = html.substring(beginIndex, endIndex + 1);

			WebView scheduleBrowser = (WebView) findViewById(R.id.login_page);
			String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

			// Sets the WebView which will just hold the schedule itself
			scheduleBrowser.setWebViewClient(new WebViewClient() {  

				public void onPageFinished(WebView view, String url) {
					// Swaps the views back
					findViewById(R.id.login_page).setVisibility(View.VISIBLE);
					findViewById(R.id.login_progress).setVisibility(View.GONE);
				}
			});

			// Loads HTML source for just the schedule
			scheduleBrowser.loadData(header + scheduleTable, "text/html", null);
		}    
	}


	// This function is called when the MenuItem is pressed, saving the screenshot of the WebView
	// as a Bitmap, cropping it, and sending it out as a share intent.
	public void share_image() {
		WebView view = (WebView) findViewById(R.id.login_page);

		// Take screenshot of Webview and convert to Bitmap
		Picture screenshot = view.capturePicture();

		PictureDrawable pictureDrawable = new PictureDrawable(screenshot);
		Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawPicture(pictureDrawable.getPicture());

		// Crop bitmap by calling function		
		Bitmap cropped = cropBitmap(bitmap);

		// Save bitmap to sdcard			        
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/saved_images");    
		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "Image-"+ n +".jpg";
		File file = new File (myDir, fname);
		if (file.exists ()) file.delete (); 
		try {
			FileOutputStream out = new FileOutputStream(file);
			cropped.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Share bitmap			        
		Uri fileLocation = Uri.parse("file://" + file.getAbsolutePath());

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setType("image/jpeg");
		sendIntent.putExtra(Intent.EXTRA_STREAM, fileLocation);
		startActivityForResult(Intent.createChooser(sendIntent, "share"), 1);

		lastFileSaved = file.getAbsolutePath();
	}

	// Cleanup last bitmap sent
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		File deleteFile = new File(lastFileSaved);
		deleteFile.delete();
	}


	// This function takes the WebView screenshot and crops away a large portion of the white space,
	// returning the cropped Bitmap file.
	public Bitmap cropBitmap(Bitmap original) {
		int x = 0, y = 0, width = 0, height = 0;

		// Calculate lower margin
		int tempX = original.getWidth() / 2;
		int tempY = original.getHeight() - 1;

		while(original.getPixel(tempX, tempY) == Color.WHITE) {
			tempY--;
		}

		height = tempY + 10;

		// Calculate side margin
		tempX = 0;
		tempY = original.getHeight() / 5;

		while(original.getPixel(tempX, tempY) == Color.WHITE) {
			tempX++;
		}

		x = tempX - 10;
		width = original.getWidth() - 2 * tempX + 20;

		// Crop bitmap
		return Bitmap.createBitmap(original, x, y, width, height, null, false);
	}

}

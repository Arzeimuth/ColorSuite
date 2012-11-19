package buxburt.anddev.colorharmonizer.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.constant.Constants;

/**
 * Perform operations based on the chosen palette.
 * 
 * Allows the user to export as message or image to camera library.
 * 
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class ExportMenuActivity extends Activity {

	/** The button used to send the information in text form */
	Button mailButton;
	/** Gives you the opportunity to export the color palette as an image */
	Button stripButton;
	/** A copy of the palette title */
	String paletteTitle;
	
	/** IMPORTANT: Used to gather data from previous activity. */
	//private static final String EXPORT_DATA = "ExportData";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_menu);
		
		mailButton = (Button)findViewById(R.id.EmailButton);
		
		/** Allow the data to be sent as intent extras */
		mailButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				SharedPreferences prefs = getSharedPreferences(Constants.EXPORT_DATA, MODE_PRIVATE);
				// TODO Auto-generated method stub
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				String[] recipients = new String[]{""};
				emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "YOUR PALETTE INFO");
				emailIntent.putExtra(Intent.EXTRA_TEXT, prefs.getString("PaletteInfo","None"));
				emailIntent.setType("text/plain");
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}
		});
			
		stripButton = (Button)findViewById(R.id.ImageButton);
		
		/** Call a custom listener class */
		stripButton.setOnClickListener(new MyOnButtonSelectedListener());	
			
		//get the shared information
		SharedPreferences exportData = getSharedPreferences(Constants.EXPORT_DATA, MODE_PRIVATE);
		int paletteSize=0;

		//called so the activity can accumulate enough views for each color
		paletteSize = exportData.getInt("PaletteSize",0);
		paletteTitle = exportData.getString("PaletteTitle","Untitled");
		
		//grab each respective layout
		LinearLayout prevLayout=(LinearLayout)findViewById(R.id.SwatchInfoLayout);
		LinearLayout prevInfo=(LinearLayout)findViewById(R.id.SwatchDetailsLayout);
		EditText textLabel=(EditText)findViewById(R.id.PaletteName);
		
		for (int i=0; i<paletteSize; i++)
		{
			//for creating the swatch
			TextView tmpSwatch;
			//the Swatch is the name of the data
			int tmpColor = exportData.getInt("Swatch"+i, Color.BLACK);
			tmpSwatch = new TextView(this);
			tmpSwatch.setBackgroundColor(tmpColor);
			tmpSwatch.setText(" ");
	    	tmpSwatch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, .5f));
			prevLayout.addView(tmpSwatch);
			
			//build the info, then insert into the view
			String info = buildInformation(tmpColor);
			
			TextView tmpInfo;
			tmpInfo = new TextView(this);
			tmpInfo.setBackgroundColor(Color.BLACK);
			tmpInfo.setText(info);
	    	tmpInfo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, .5f));
			prevInfo.addView(tmpInfo);
			
		}
		
		/** Handle the renaming of the palette */ 
		textLabel.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				TextView preView = (TextView)findViewById(R.id.PreviewLabel);
				TextView textLabel = (TextView)findViewById(R.id.PaletteName);
				paletteTitle = textLabel.getText().toString();
				preView.setText(paletteTitle);
			}
		});
		
		//paletteTitle = String.valueOf(System.currentTimeMillis());
		textLabel.setText(paletteTitle);
	}
	
	/**
	 * Parse the color information into the image
	 * @param color The color in integer form
	 * @return return the information String
	 */
	private String buildInformation(int color)
	{
		//conversion for red, green, blue
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		
		//conversion to floats for HSV
		float[] hsv;
		hsv = new float[3];
		Color.colorToHSV(color, hsv);
		int h = (int)hsv[0];
		int s = (int)(hsv[1]*100.0f);
		int v = (int)(hsv[2]*100.0f);
		
		//build the string
		StringBuilder strInfo = new StringBuilder();
		String hexString=String.format("#%X", color);
		hexString="#"+String.copyValueOf(hexString.toCharArray(), 3, 6);
		strInfo.append("Hex: "+hexString+"\n");
		strInfo.append("R: "+r+" G: "+g+" B: "+b+"\n");
		strInfo.append("H: "+h+" S: "+s+" V: "+v+"\n");
		
		return strInfo.toString();
	}
	
	@Override
	/**
	 * Called so that if the activity is rotated while booting, the app will not crash
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Create the pictures directory if it has not been created yet
	 * @param path The path to the folder
	 * @return If true, continue the operations
	 */
	public boolean createDirIfNotExists(String path) {
	    boolean ret = true;

	    File file = new File(Environment.getExternalStorageDirectory(), path);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("ERROR :: ", "Problem creating Image folder");
	            ret = false;
	        }
	    }
	    return ret;
	}
	
	/**
	 * This listener handles all gallery submission actions
	 * @author Brandon Burton
	 * Jul 2, 2012
	 *
	 */
    public class MyOnButtonSelectedListener implements View.OnClickListener {

    	public MyOnButtonSelectedListener()
    	{
    		
    		
    	}
    	
		public void onClick(View v) {
			
			LinearLayout paletteView = (LinearLayout)findViewById(R.id.PreviewLayout);
    		paletteView.setDrawingCacheEnabled(true);
    		paletteView.buildDrawingCache();
    		//after requiring the cache, build the image into a bitmap
    		Bitmap bm = paletteView.getDrawingCache();
 
    		//EditText textLabel=(EditText)findViewById(R.id.PaletteName);
    		String filename = paletteTitle;
    		
    		//fit it to 30 characters if the filename is longer than 30
    		filename.toLowerCase(Locale.US);
    		//somtimes on Linux platforms, files need to be read with an underscore separator so they are read as separate strings
    		filename.replace(" ","_");
    		filename = filename.substring(0, (filename.length() < 30) ? filename.length() : 29);
    		
			Log.i("NameOf",filename);
    		
			//add this to the content db
			ContentValues values = new ContentValues();
			values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
			values.put(Images.Media.MIME_TYPE, "image/jpeg");
			//values.put(Images.Media., value)
			values.put(Images.Media.TITLE, filename);

			
			//TODO: Get the external path export to work
			//Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			//Uri uri=Uri.parse(Environment.getExternalStorageDirectory()+"/palettes/"+filename+".png");
			//Log.i("Value of",uri.toString());
			//Log.i("Value of",Environment.getExternalStorageDirectory().toString());
			
			boolean created = createDirIfNotExists("palettes");
			
			if (created)
				Log.i("Directory created","OK");	
			else
				Log.i("Directory exists","OK");
			
			//if successful
			try 
			{
				/*
				OutputStream outStream = getContentResolver().openOutputStream(uri);
				bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
				Context context = getApplicationContext();
				Toast toast = Toast.makeText(context, "Image saved! Check Gallery for palette image.", Toast.LENGTH_LONG);
				toast.show();
				*/
				
				//ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				OutputStream fOut = null;
				
				//find the path to the external storage
				String longString = Environment.getExternalStorageDirectory()
                        + File.separator + "palettes" +File.separator + filename+".jpg";
				
				File f = new File(longString);
				fOut = new FileOutputStream(f);
				
				bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				
				//write the bytes in file
				//FileOutputStream fo = new FileOutputStream(f);
				//fo.write(bytes.toByteArray());
				//fo.close();
				
				fOut.flush();
				fOut.close();
				
				//insert the new data into the DB
				MediaStore.Images.Media.insertImage(getContentResolver(), bm, filename, filename);
				
				//let the user know the image has been saved
				Context context = ExportMenuActivity.this.getApplicationContext();
				Log.i("SAVE PATH",longString);
				Toast toast = Toast.makeText(context, "Image saved to Gallery! NOTE: Unintentional save in Camera folder. Fix in next update.", Toast.LENGTH_LONG);
				toast.show();
				
				//update the content, so you don't have to :)
				MediaScannerConnection.scanFile(context, new String[] {longString}, null, new MediaScannerConnection.OnScanCompletedListener() {
					
					public void onScanCompleted(String path, Uri uri) {
					}
				});
				
				
			}
			
			//otherwise, print out an error
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
    		
		}	
    }
}

package buxburt.anddev.colorharmonizer.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.adapter.SwatchAdapter;
import buxburt.anddev.colorharmonizer.constant.Constants;
import buxburt.anddev.colorharmonizer.model.NSwatch;

/**
 * Activity where the user selects the desired palette.
 * 
 * User can manage palettes by choosing one to edit or remove.
 * 
 * @author Brandon Burton
 * @version 2012.0718
 * @since 1.2
 */
public class ExportMenuImageActivity extends Activity 
{
	
	//private static final String EXPORT_DATA = "ExportData";
	
	private ProgressDialog m_ProgressDialog = null;
	
	private ArrayList<NSwatch> m_swatches = null;
	
	private SwatchAdapter m_adapter;
	
	private ListView swatchListView;
	/** Sets the command to call at the beginning. */
	private Runnable viewOrders; 
	
	private String paletteTitle;
	
	private Button exportButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_menu_listview);

		m_swatches = new ArrayList<NSwatch>();
		this.m_adapter = new SwatchAdapter(this, R.layout.export_menu_listitem, m_swatches);
		
		swatchListView = (ListView)findViewById(R.id.listViewTotalSwatch);
		swatchListView.setAdapter(m_adapter);
		
		//TODO: FINISH THIS
		exportButton = (Button)findViewById(R.id.confirmButton);
		exportButton.setOnClickListener(new MyOnButtonSelectedListener());
	}

	@Override
	public void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		m_adapter.clear();
		
		//getPaletteInfo();
		
		viewOrders = new Runnable() 
		{
			
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				getPaletteInfo();
			}
		};
		
		Thread thread = new Thread(null, viewOrders, "ExportAllActivity");
		thread.start();
		
		m_ProgressDialog = ProgressDialog.show(
				ExportMenuImageActivity.this,
				"Please wait...",
				"Retrieving data...",
				true);
				
		
	}

	@Override
	/**
	 * Called so that if the activity is rotated while booting, the app will not crash
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	
	/** This is to be called when there is a change in data*/
	
	private Runnable returnRes = new Runnable() 
	{
		public void run() 
		{
			if (m_swatches != null && m_swatches.size() > 0)
			{
				m_adapter.notifyDataSetChanged();
				//add the data
				for (int i=0; i<m_swatches.size(); i++)
					m_adapter.add(m_swatches.get(i));
			}
		
		m_ProgressDialog.dismiss();
		m_adapter.notifyDataSetChanged();
		}
	};
	
	private void getPaletteInfo()
	{
		m_swatches = new ArrayList<NSwatch>();
		
		//get the export data
		SharedPreferences swatchData = getSharedPreferences(Constants.EXPORT_DATA, MODE_PRIVATE);
		
		int size = swatchData.getInt("PaletteSize",0);
		paletteTitle = swatchData.getString("PaletteTitle", "No Name");
		
		TextView titleView;
		titleView = (TextView)findViewById(R.id.labelExportPaletteTitle);
		titleView.setText(paletteTitle);
		
		for (int i=0; i<size; i++)
		{
			int lcolor = swatchData.getInt("Swatch"+i,0);
			Log.i("COLOR: ",String.valueOf(lcolor));
			String info = buildInformation(lcolor);
			m_swatches.add(new NSwatch(lcolor, info));
		}
		
		runOnUiThread(returnRes);
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
		
		hexString=String.copyValueOf(hexString.toCharArray());
		strInfo.append("Hex: "+hexString+"\n");
		strInfo.append("R: "+r+" G: "+g+" B: "+b+"\n");
		strInfo.append("H: "+h+" S: "+s+" V: "+v);
		
		return strInfo.toString();
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
	            Log.e("TravellerLog :: ", "Problem creating Image folder");
	            ret = false;
	        }
	    }
	    return ret;
	}
	
	/**
	 * This listener handles all gallery submission actions
	 * @author Brandon Burton
	 * Jul 2, 2012
	 */
    public class MyOnButtonSelectedListener implements View.OnClickListener 
    {

    	public MyOnButtonSelectedListener()
    	{
    			
    	}
    	
		public void onClick(View v) 
		{	
			exportBitmap();
		}
		
    }
    
	private void exportBitmap()
	{
		LinearLayout paletteView = (LinearLayout)findViewById(R.id.ExportBitmapLayout);
		paletteView.setDrawingCacheEnabled(true);
		paletteView.buildDrawingCache();
		//after requiring the cache, build the image into a bitmap
		Bitmap bm = paletteView.getDrawingCache();
		
		//EditText textLabel=(EditText)findViewById(R.id.PaletteName);
		String filename = paletteTitle;
		//TODO: Fix this
		//String filename = "PALETTETITLE";
		//fit it to 30 characters if the filename is longer than 30
		filename.toLowerCase(Locale.US);
		//sometimes on Linux platforms, files need to be read with an underscore separator so they are read as separate strings
		filename.replace(" ","_");
		filename = filename.substring(0, (filename.length() < 30) ? filename.length() : 29);
		
		Log.i("NameOf",filename);
		
		//add this to the content db
		ContentValues values = new ContentValues();
		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		//values.put(Images.Media., value)
		values.put(Images.Media.TITLE, filename);
		
		boolean created = createDirIfNotExists("palettes");
		
		if (created)
			Log.i("Directory created","OK");	
		else
			Log.i("Directory exists","OK");
		
		try 
		{
			OutputStream fOut = null;
			
			//find the path to the external storage
			String longString = Environment.getExternalStorageDirectory()
                    + File.separator + "palettes" +File.separator + filename+".jpg";
			
			File f = new File(longString);
			fOut = new FileOutputStream(f);
			
			bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			
			fOut.flush();
			fOut.close();
			
			//insert the new data into the DB
			//MediaStore.Images.Media.insertImage(getContentResolver(), f.getAbsolutePath(), filename, filename);
			//MediaStore.Images.Media.
			
			//let the user know the image has been saved
			Context context = ExportMenuImageActivity.this.getApplicationContext();
			Log.i("SAVE PATH",longString);
			Toast toast = Toast.makeText(context, "Saved to the palettes folder!", Toast.LENGTH_LONG);
			toast.show();
			
			/*
			File f1 = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera");
	    	
	    	File[] files = f1.listFiles();
	    	
	    	Arrays.sort(files, new Comparator<Object>() 
	    	{
				@Override
				public int compare(Object o1, Object o2) 
				{
					if (((File)o1).lastModified() > ((File)o2).lastModified()) 
					{
	                    Log.i("Log", "Going -1");
	                    return -1;
	                } 
					
					else if (((File)o1).lastModified() < ((File)o2).lastModified()) 
					{
	                    Log.i("Log", "Going +1");
	                    return 1;
	                } 
					else 
					{
	                    Log.i("Log", "Going 0");
	                    return 0;
	                }
				}
			});
	    	
			files[0].delete();
			*/
			
			//update the content, so you don't have to :)
			MediaScannerConnection.scanFile(context, new String[] {longString}, null, new MediaScannerConnection.OnScanCompletedListener() {
				
				public void onScanCompleted(String path, Uri uri) 
				{
					Log.i("PATH",path);
					Log.i("URI",uri.toString());
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

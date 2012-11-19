package buxburt.anddev.colorharmonizer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.constant.Constants;

import com.actionbarsherlock.app.SherlockActivity;

public class ExportMenuHybridActivity extends SherlockActivity
{
	Button mailButton;
	
	Button stripButton;
	
	CheckBox checkRGB;
	CheckBox checkHSV;
	CheckBox checkHex;
	
	String paletteTitle;
	
	//EditText textPaletteTitleView;
	
	//private static final String EXPORT_DATA = "ExportData";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_menu_hybrid);
		
		mailButton = (Button)findViewById(R.id.buttonExportAsMessage);
		stripButton = (Button)findViewById(R.id.buttonExportAsImage);
		checkRGB = (CheckBox)findViewById(R.id.checkRGBFlag);
		checkHSV = (CheckBox)findViewById(R.id.checkHSVFlag);
		checkHex = (CheckBox)findViewById(R.id.checkHexFlag);		
		//textPaletteTitleView = (EditText)findViewById(R.id.textPaletteTitle);
		
		/*
		textPaletteTitleView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				paletteTitle = textPaletteTitleView.getText().toString();
				StringBuilder str = new StringBuilder();
				str.append(paletteTitle);
				str.setLength(24);
				
				SharedPreferences prefs = getSharedPreferences(Constants.EXPORT_DATA, MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
			
				editor.putString("PaletteTitle", paletteTitle);
				editor.commit();
			}
		});
		*/
		mailButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				SharedPreferences prefs = getSharedPreferences(Constants.EXPORT_DATA, MODE_PRIVATE);
				// TODO Auto-generated method stub
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				String[] recipients = new String[]{""};
				emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, prefs.getString("PaletteTitle", "Needs Title"));
				emailIntent.putExtra(Intent.EXTRA_TEXT, prefs.getString("PaletteInfo","None"));
				emailIntent.setType("text/plain");
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}
		});
		
		stripButton.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				SharedPreferences prefs = getSharedPreferences(Constants.EXPORT_DATA, MODE_PRIVATE);
				//SharedPreferences.Editor editor = prefs.edit();
				
				Log.i("Swatch0",String.valueOf(prefs.getInt("Swatch0",-1)));
				Log.i("Swatch1",String.valueOf(prefs.getInt("Swatch1",-1)));
				Log.i("Swatch2",String.valueOf(prefs.getInt("Swatch2",-1)));
				Log.i("Swatch3",String.valueOf(prefs.getInt("Swatch3",-1)));
				
				// TODO Auto-generated method stub
				Intent exportIntent = new Intent(getApplicationContext(), ExportMenuImageActivity.class);
				startActivity(exportIntent);
			}
		});
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
	 * Parse the color information into the image
	 * @since 1.2
	 * @param color The color in integer form
	 * @return return the information String
	 */
	/*
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
		
		if (checkRGB.isChecked())
		{
			strInfo.append("R: "+r+" G: "+g+" B: "+b+"\n");
		}
		
		if (checkHSV.isChecked())
		{
			strInfo.append("H: "+h+" S: "+s+" V: "+v+"\n");
		}
		
		if (checkHex.isChecked())
		{
			strInfo.append("Hex: "+hexString+"\n");
		}
		
		return strInfo.toString();
	}
	*/
	
	
}

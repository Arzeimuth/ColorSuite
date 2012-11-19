package buxburt.anddev.colorharmonizer.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.adapter.NSQLiteAdapter;
import buxburt.anddev.colorharmonizer.constant.Constants;
import buxburt.anddev.colorharmonizer.engine.ColorEngine;
import buxburt.anddev.colorharmonizer.model.NColor;
import buxburt.anddev.colorharmonizer.model.NPalette;
import buxburt.anddev.colorharmonizer.view.ColorChooserButton;
import buxburt.anddev.colorharmonizer.view.ColorWheelView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * An activity that employs a majority of the decision making.
 * Waiting for a list of TODO s.....
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class ColorHarmonizerActivity extends SherlockActivity {
	
	/** Called when the activity is first created. */
	//CONSTANTS (moved to Constants.java
	//private static final String PREFS_NAME = "PrefsData";
	//private static final String EXPORT_DATA = "ExportData";
	
	//FIELDS
	boolean hasNameChangeOccured = false;
	
	//all of the following are views based on the main.xml
	Context context;
	
	TextView textPaletteName;
	TextView textHexValue;
	
	ColorWheelView colorWheel;
	SeekBar progressValueBar;
	
	//Button helpButton;
	Button addSwatchButton;
	Button removeSwatchButton;
	Button wheelSettingButton;
	
	Button infoButton1;
	Button infoButton2;
	Button infoButton3;
	Button colorModelButton;
	
	ArrayList<ColorChooserButton> swatchButtons;
	
	LinearLayout swatchLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		context = this;
		
		//give default values to the color engine
		ColorEngine.start();
		
		//hook all of the views to their respective fields
		textPaletteName = (TextView)findViewById(R.id.textPaletteInfo);
		textHexValue = (TextView)findViewById(R.id.textInfoBlock1);
		
		colorWheel = (ColorWheelView)findViewById(R.id.colorWheelViewMain);
		removeSwatchButton = (Button)findViewById(R.id.buttonRemoveSwatch);
		addSwatchButton = (Button)findViewById(R.id.buttonAddSwatch);
		wheelSettingButton = (Button)findViewById(R.id.wheelSettingsButton);
		
		progressValueBar = (SeekBar)findViewById(R.id.progressBarValue);

		swatchLayout = (LinearLayout)findViewById(R.id.SwatchLayout);
		
		infoButton1 = (Button)findViewById(R.id.infoButton1);
		infoButton2 = (Button)findViewById(R.id.infoButton2);
		infoButton3 = (Button)findViewById(R.id.infoButton3);
		
		swatchButtons = new ArrayList<ColorChooserButton>();
		
		colorModelButton = (Button)findViewById(R.id.colorModelButton);
		
		//set the default values here
		ColorEngine.isHarmonizing = true;
		ColorEngine.harmonyMode = 0;
		ColorEngine.selectedSwatch = 0;		
		
		/**
		 * Handle the data for the ColorWheelView
		 */
		colorWheel.setOnTouchListener(
			new ColorWheelView.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				
				//get the appropriate x and y
				int x = (int)event.getX();
				int y = (int)event.getY();
				
				//operate based on gesture moment
				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						colorWheel.locateSampler(x, y);
						break;
					case MotionEvent.ACTION_MOVE:
						colorWheel.moveSampler(x, y);
						updateButtons();
						updateInfo();
						colorWheel.recieveMessage();
						break;
					case MotionEvent.ACTION_UP:
						break;
				}
				return true;
			}		
		});
		
		/** Handle data operation of the value slider. */
		progressValueBar.setOnSeekBarChangeListener(
			new SeekBar.OnSeekBarChangeListener() {
				
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//send the engine a value, engine performs operations
				int v = ColorEngine.sendValueMessage();
				infoButton3.setText(String.valueOf(v));
				//receive the data returned
				ColorEngine.recieveVMessage((float)progress/100.0f);
				updateButtons();
				updateInfo();
			}
		});
		
		/** removal of swatch is performed here */
		removeSwatchButton.setOnClickListener(
			new Button.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				removeSwatch();
			}
		});
		
		/** additon of swatch is performed here */
		addSwatchButton.setOnClickListener(
			new Button.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				addSwatch();
			}
		});
		
		/** setting of the color harmonizer starts here*/
		wheelSettingButton.setOnClickListener(
			new Button.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				
				//TODO: Create an array of strings
				final CharSequence[] choices = {
						"None", "Analogous (All)", "Shaded (All)", "Complementary (3+)",
						"Triadic (3+)", "Split-Comp (3+)","Left Comp (4+)","Right Comp (4+)","Y Complementary (4+)",
						"Square (4+)","Tetradic (4+)","Special 1 (All)","Special 2 (All)","Special 3 (All)","Greys (All)"
						};
				
				//build the dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(true);
				builder.setTitle("Select Harmony");
				builder.setItems(choices, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//based on the color harmony
						ColorEngine.harmonyMode = which;
						dialog.dismiss();
						//toggle the mode of the harmonizer button to it's appropriate state
						updateHarmonizerButton();
					}
				});
				
				AlertDialog alert = builder.create();
				alert.show();
				
				/* TODO: Finish special harmony view
				ArrayList<ColorRelationship> m_harmonies = null;
				ColorRelationshipAdapter m_adapter;
				ListView relation;
				*/
				
					
				/*
				m_harmonies = new ArrayList<ColorRelationship>();
				m_adapter = new ColorRelationshipAdapter(this, R.layout.row, m_harmonies);
				setListAdapter(m_adapter);
				
				//TODO: add all the palettes here, (externalize when possible)
				m_harmonies.add(new ColorRelationship(R.drawable.rel_none, "None", 1, 0));
				m_harmonies.add(new ColorRelationship(R.drawable.rel_analogous, "Analogous", 3, 1));
				m_harmonies.add(new ColorRelationship(R.drawable.rel_comp, "Complementary", 3, 2));
				m_harmonies.add(new ColorRelationship(R.drawable.rel_triadic, "Triadic", 3, 3));
				m_harmonies.add(new ColorRelationship(R.drawable.rel_split_comp, "Split-Complimentary", 3, 4));
				m_harmonies.add(new ColorRelationship(R.drawable.rel_left_comp, "Left Complementary", 3, 5));
				m_harmonies.add(new ColorRelationship(R.drawable.rel_right_comp, "Right Complementary", 3, 6));			
				m_harmonies.add(new ColorRelationship(R.drawable.rel_y_comp, "Y Complementary", 4, 7));
				m_harmonies.add(new ColorRelationship(R.drawable.rel_square, "Square", 4, 8));
				m_harmonies.add(new ColorRelationship(R.drawable.rel_tetradic, "Tetradic", 4, 9));	
				
				
				//TODO: Make dialog out of this
				//ColorRelationshipAdapter adapter = new ColorRelationshipAdapter(this, R.layout.row_harmony, m_harmonies);
				*/
				//TextView text = (TextView)dialog.findViewById(R.id.labelTestText);
				//text.setText("HEY");
			
				//dialog.show();
			}
		});
		
		//at the end of all the hookup, update everything
		loadColors();
		updateHarmonizerButton();
		updateButtons();
		updateSlider();
		updateInfo();
		//receive the color information
		colorWheel.recieveMessage();
	}
		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		SharedPreferences prefState = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		ColorEngine.paletteAuthor = prefState.getString("PaletteAuthor", "Your Name");
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		saveColors();
		super.onPause();
	}

	@Override
	public void onBackPressed() 
	{
		//save information to the database
		saveToDatabase();
	}

	/**
	 * Create the ActionBar options
	 */
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.abmenu, (com.actionbarsherlock.view.Menu) menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if (id == R.id.prefMenuItem)
		{
			exportPalette();
		}
		
		else if(id == R.id.settingsMenuItem)
		{
			Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
			startActivity(intent);
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Add a button, but other operations such as swatch addition is performed easier here
	 */
	private void addButton()
	{
		colorWheel.addSampler();
		ColorEngine.selectedSwatch = ColorEngine.size()-1;
		
		ColorChooserButton c;
		c = new ColorChooserButton(this);
		c.setBackgroundColor(Color.WHITE);
		c.setText(String.valueOf(ColorEngine.size()));
		c.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, .5f));
		c.setOnClickListener(new MyOnButtonSelectedListener(ColorEngine.size()-1));
		//TODO: set to density pixels
		c.setPadding(20, 20, 20, 20);
		
		swatchButtons.add(c);
		swatchLayout.addView(c);
	}
	
	/**
	 * Remove a button
	 */
	private void removeButton()
	{
		colorWheel.removeSampler();
		swatchButtons.remove(ColorEngine.size());
		swatchLayout.removeViewAt(ColorEngine.size());
	}
	
	/**
	 * Updates the state of the harmony button
	 */
	private void updateHarmonizerButton()
	{
		Log.i("COLORHARMONIZEMODE",String.valueOf(ColorEngine.harmonyMode));
		if (ColorEngine.harmonyMode==0)
			wheelSettingButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.harmonize_button));
		else
			wheelSettingButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.harmonize_button_lit));
		wheelSettingButton.invalidate();
	}
	
	/**
	 * Recieve the colors from the ColorEngine
	 */
	private void updateButtons()
	{
		int[] colors;
		colors = ColorEngine.sendColorMessage();
		
		for (int i = 1; i<(colors[0]+1); i++)
		{
			//Log.i("Color"+String.valueOf(i),String.valueOf(colors[i]));
			swatchButtons.get(i-1).setBackgroundColor(colors[i]);
			swatchButtons.get(i-1).setFree();
		}
		
		swatchButtons.get(ColorEngine.selectedSwatch).setActive();
	}
	
	/**
	 * Update the info labels at the bottom of the screen
	 */
	private void updateInfo()
	{
		infoButton1.setText(String.valueOf(ColorEngine.sendHueMessage() / 100));
		infoButton2.setText(String.valueOf(ColorEngine.sendSaturationMessage()));
		infoButton3.setText(String.valueOf(ColorEngine.sendValueMessage()));
		
		StringBuilder hexBuild = new StringBuilder();
		hexBuild.append(String.valueOf(ColorEngine.sendHexMessage()));
		textHexValue.setText(hexBuild.toString());
		//textHexValue.setText(hexBuild.substring(2, 8));
	}
	
	/**
	 * Update the slider based on the progress
	 */
	private void updateSlider()
	{
		progressValueBar.setProgress(ColorEngine.sendValueMessage());
	}
	
	/**
	 * Removal of swatch from activity
	 */
	private void removeSwatch()
	{
		if (ColorEngine.size() > 1)
		{
			ColorEngine.removeColor(ColorEngine.size()-1);
			ColorEngine.selectedSwatch = 0;
			removeButton();
			colorWheel.invalidate();
			swatchLayout.invalidate();
		}
		else
		{
			Toast toast = Toast.makeText(context, "Must have one color!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	/**
	 * Add a swatch to the activity
	 */
	private void addSwatch()
	{
		if (ColorEngine.size() < 7)
		{
			ColorEngine.addColor(Color.WHITE);
			addButton();
			colorWheel.invalidate();
			swatchLayout.invalidate();
		}
		else
		{
			Toast toast = Toast.makeText(context, "For now, max of 7 swatches.", Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}
	
	/**
	 * Gets the data saved from the NPaletteListView activity, absolutely necessary to run this activity!
	 */
	private void loadColors()
	{				
		SharedPreferences prefState = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		
		//ColorEngine.harmonyMode = prefState.getInt("HarmonizeMode", 0);
		ColorEngine.harmonyMode = 0;
		ColorEngine.selectedSwatch = prefState.getInt("SwatchSelected", 0);
		ColorEngine.colorModelMode = prefState.getInt("ColorModelMode", 0);
		ColorEngine.colorWheelType = prefState.getBoolean("ColorWheelMode", false);
		ColorEngine.paletteID = prefState.getInt("PaletteID", -1);
		ColorEngine.paletteName = prefState.getString("PaletteName", "New Palette");
		ColorEngine.paletteAuthor = prefState.getString("PaletteAuthor", "Your Name");
		
		int swatchNum = prefState.getInt("PaletteSize", 3);
		
		/*
		Log.i("HARMONY MODE",String.valueOf(ColorEngine.harmonyMode));
		Log.i("SELECTED SWATCH",String.valueOf(ColorEngine.selectedSwatch));
		Log.i("COLOR MODEL MODE",String.valueOf(ColorEngine.colorModelMode));
		Log.i("COLOR WHEEL TYPE",String.valueOf(ColorEngine.colorWheelType));
		Log.i("PALETTE ID",String.valueOf(ColorEngine.paletteID));
		Log.i("PALETTE NAME",String.valueOf(ColorEngine.paletteName));
		 */
		
		String colors;
		NPalette workingPalette;
		{
			//DEFAULT
			colors = "FFFFFFBB33DD2255CC";

			//Log.i("COLORS",prefState.getString("Colors", colors));
			//Log.i("SIZE",String.valueOf(prefState.getInt("PaletteSize", 3)));
			
			//LOAD PREFS THEN DEFAULT
			workingPalette = new NPalette(
					prefState.getInt("PaletteID", 0),
					prefState.getString("PaletteName", "Failsafe Name"),
					"undefined",
					"undefined",
					prefState.getString("Colors", colors),
					prefState.getInt("PaletteSize", 3),
					999999999,
					999999999);
						
		}
		
		ColorEngine.paletteID = Integer.valueOf(workingPalette.getPaletteID());
		ColorEngine.paletteName = workingPalette.getPaletteName();
		
		textPaletteName.setText(String.valueOf(ColorEngine.paletteName));
		
		colors = workingPalette.getAllColorSwatches();
		
		swatchNum = prefState.getInt("PaletteSize", 3);	
		
		//update the color engine here
		for (int i=0; i<swatchNum; i++)
		{
			String temp = colors.substring((i*6), (i*6)+6);
			NColor a = new NColor(Integer.valueOf(temp, 16));
			ColorEngine.addColor(a);
			if (ColorEngine.size() < 8)
				addButton();
		}
		
	}

	/**
	 * Save colors from performed operations
	 */
	private void saveColors()
    {
		SharedPreferences palettePrefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = palettePrefs.edit();
		//editor.clear();
		
		editor.putInt("PaletteSize",ColorEngine.size());
		editor.putString("Colors",ColorEngine.parseColorsToString());
		
		editor.putInt("HarmonizeMode", ColorEngine.harmonyMode);
		//editor.putInt("SwatchSelected", ColorEngine.selectedSwatch);	
		editor.putInt("SwatchSelected", 0);
		editor.putInt("ColorModelMode", ColorEngine.colorModelMode);
		editor.putBoolean("ColorWheelMode", ColorEngine.colorWheelType);
		
		editor.putString("PaletteName", ColorEngine.paletteName);
		editor.putInt("PaletteID", ColorEngine.paletteID);
		
		editor.commit();	
    }
	
	/**
	 * Create a dialog that gives the user a set of options to save their palette.
	 */
	private void saveToDatabase()
	{
		
		AlertDialog.Builder builder;
		EditText paletteNameText;
		Dialog popUpSaveDialog;
		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.menusaveswatch, (ViewGroup)findViewById(R.layout.main));
		
		builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		
		builder.setPositiveButton("Save", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//instance the adapter for the database
				NSQLiteAdapter mySQLite = new NSQLiteAdapter(context);
				mySQLite.openToWrite();
				
				if (hasNameChangeOccured)
				{
					mySQLite.insert(
							ColorEngine.paletteName,
							ColorEngine.paletteAuthor,
							"No description yet.",
							ColorEngine.parseColorsToString(),
							ColorEngine.size());
					//saveAuthor(ColorEngine.paletteAuthor);
				}
				
				else
				{
					//this is where the update becomes handy
					mySQLite.updateByTitle(ColorEngine.paletteName, ColorEngine.parseColorsToString(), ColorEngine.size());
				}
				
				mySQLite.close();
				
				//finish this dialog
				finish();
			}
		});
		
		builder.setNegativeButton("Skip", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				finish();
			}
		});
		
		popUpSaveDialog = builder.create();
		popUpSaveDialog.show();
	
		/**set the default information here*/
		paletteNameText = (EditText)popUpSaveDialog.findViewById(R.id.textPaletteName);
		paletteNameText.setText(ColorEngine.paletteName);
		paletteNameText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			/**
			 * Check for changes to the name, so the saving can be faster.
			 */
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				if (ColorEngine.paletteName == s.toString())
				{
					hasNameChangeOccured=false;
				}
				else
				{
					hasNameChangeOccured = true;
					ColorEngine.paletteName = s.toString();
				}
				
			}
			
		});
		
		//authorNameText = (EditText)popUpSaveDialog.findViewById(R.id.textPaletteAuthor);
		//authorNameText.setText(ColorEngine.paletteAuthor);
		/*
		authorNameText.addTextChangedListener(new TextWatcher() {
			
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
				if (ColorEngine.paletteAuthor == s.toString())
				{
					hasNameChangeOccured=false;
				}
				else
				{
					hasNameChangeOccured = true;
					ColorEngine.paletteAuthor = s.toString();
				}
			}
		});
		
		*/
		
		//Log.i("PALETTE NAME",paletteNameText.getText().toString());
		
		/*
		SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putString("PaletteName", paletteNameText.getText().toString());
		*/
		//super.onBackPressed();
	}
	
	/**
	 * Export this palette to the ExportMenuActivity
	 */
	private void exportPalette()
    {
    	Intent emailIntent = new Intent(getApplicationContext(), ExportMenuHybridActivity.class);
    	
    	SharedPreferences exportData = getSharedPreferences(Constants.EXPORT_DATA, MODE_PRIVATE);
    	SharedPreferences.Editor editor = exportData.edit();
    	
    	editor.clear();
    	
    	editor.putString("PaletteInfo", ColorEngine.parseColorsToExport());
		editor.putInt("PaletteSize", ColorEngine.size());
		editor.putString("PaletteTitle", ColorEngine.paletteName);
		
		Log.i("VALUEOF",ColorEngine.parseColorsToExport());
		Log.i("SIZEOF",String.valueOf(ColorEngine.size()));
    	
    	for (int i=0; i<ColorEngine.size(); i++)
    	{
    		editor.putInt("Swatch"+i,ColorEngine.getSwatchAsColor(i));
    	}
    	
    	editor.commit();
    	
    	//TODO: FIX THIS SO COLORS EXPORT
    	startActivity(emailIntent);
    	
    }
	
	/**
	 * TODO: Finish this implementation so harmony constraints work properly
	 * @author Brandon Burton
	 * Jul 2, 2012
	 *
	 */
	public class MyOnButtonSelectedListener implements ColorChooserButton.OnClickListener {
    	
    	private final int index;

    	public MyOnButtonSelectedListener(final int index)
    	{
    		this.index = index;
    	}
    	
		public void onClick(View v) {
			ColorEngine.selectedSwatch = index;
			updateSlider();
			updateInfo();
			for (int i=0; i<ColorEngine.size(); i++)
				swatchButtons.get(i).setFree();
			swatchButtons.get(index).setActive();
		}
    	
    }
	
	public class MyOnItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) 
		{
			
			
		}
    }
	
	/**
	 * Temporary class to hold information for the Custom ColorRelationship view
	 * @author Brandon Burton
	 * Jul 2, 2012
	 *
	 */
	static class ColorRelationshipHolder
	{
		int id;
		ImageView image;
		TextView name;
		TextView minimumQuantity;
	}
}
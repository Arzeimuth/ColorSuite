package buxburt.anddev.colorharmonizer.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.adapter.NSQLiteAdapter;
import buxburt.anddev.colorharmonizer.adapter.PaletteAdapter;
import buxburt.anddev.colorharmonizer.constant.Constants;
import buxburt.anddev.colorharmonizer.model.NColor;
import buxburt.anddev.colorharmonizer.model.NPalette;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Activity where the user selects the desired palette.
 * 
 * User can manage palettes by choosing one to edit or remove.
 * 
 * @author Brandon Burton
 * @version 2012.0702
 * @since 1.0
 */
public class NPalettesListView extends SherlockActivity 
{
	/** IMPORTANT: This holds the data shared between the ColorHarmonizer. */
	//private static final String PREFS_NAME = "PrefsData";
	/** IMPORTANT: This holds the data shared between the Export menus. */
	//private static final String EXPORT_DATA = "ExportData";
	/** Shown at the launch of the app. */
	private ProgressDialog m_ProgressDialog = null;
	/** This holds the classes of data retrieved by database. */
	private ArrayList<NPalette> m_palettes = null;
	/** This adapter allows the ListView to be managed by a list of palettes. */
	private PaletteAdapter m_adapter;
	/** Sets the command to call at the beginning. */
	private Runnable viewOrders;
	/** Holds the custom SQL adapter made for this activity. */
	private NSQLiteAdapter mySQLite;
	/** The button that creaets a new palette. */
	//private Button addButton;
	/** The physical list view. */
	private ListView paletteListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlist);
		
		//allocate a list of default palettes
		m_palettes = new ArrayList<NPalette>();
		this.m_adapter = new PaletteAdapter(this, R.layout.row, m_palettes);
		//setListAdapter(this.m_adapter);
		
		paletteListView = (ListView)findViewById(R.id.listViewPalettes);
		paletteListView.setAdapter(m_adapter);
		
		/**
		 * For this listener, start the activity with values retrieved from the array of NPalettes.
		 */
		paletteListView.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) 
			{
				createDefaultPalette(position);
				
				Intent optIntent = new Intent(getApplicationContext(), ColorHarmonizerActivity.class);
				startActivity(optIntent);
			}
			
		});
		
		/**
		 * Or, if it is a long hold, give a set of options for the user.
		 */
		paletteListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) 
			{
				//TODO: Align this menu with Android String resources
				final CharSequence[] items = {"Export","Remove"};
				AlertDialog.Builder builder;
				//TODO: Improve with more options
				Dialog removeDialog;
				
				//Log.i("LONGPERSS","OK");
				
				//create a dialog builder
				builder = new AlertDialog.Builder(NPalettesListView.this);
				builder.setTitle("Options");
				
				/** When the remove options receives the go, remove the appropriate palette */
				builder.setItems(items, new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						if (which == 0)
						{
							NSQLiteAdapter mySQLite = new NSQLiteAdapter(NPalettesListView.this);
							mySQLite.openToWrite();
							
							if (position != 0)
							{
								SharedPreferences exportData = getSharedPreferences(Constants.EXPORT_DATA, MODE_PRIVATE);
								SharedPreferences.Editor editor = exportData.edit();
								
								NPalette npal = NPalettesListView.this.m_adapter.getItem(position);
								
								editor.putString("PaletteTitle", npal.getPaletteName());
								editor.putInt("PaletteSize", npal.getPaletteSize());
								
								int size = npal.getPaletteSize();
								
								for (int i=0; i<size; i++)
								{
									NColor n = new NColor(npal.getColorSwatch(i));
									editor.putInt("Swatch"+i, n.toInt());
								}
								
								editor.commit();
								
								Intent exportIntent = new Intent(getApplicationContext(), ExportMenuImageActivity.class);
								startActivity(exportIntent);
								
							}
							
							else
							{
								//let the user know the palette cannot be removed
								Toast toast = Toast.makeText(NPalettesListView.this, "Cannot export from here!", Toast.LENGTH_SHORT);
								toast.show();
							}
							
							//close the DB
							mySQLite.close();
						}
						
						else if (which == 1)
						{
							NSQLiteAdapter mySQLite = new NSQLiteAdapter(NPalettesListView.this);
							mySQLite.openToWrite();
							//Log.i("QUEUEALLBEFORE",mySQLite.queueAll());
							
							//if this is a default palette
							if (position != 0)
							{
								//get the palette at the selected position
								NPalette npal = NPalettesListView.this.m_adapter.getItem(position);
								
								//remove this from the database
								mySQLite.deleteByID(Integer.valueOf(npal.getPaletteID()));
								
								//get the palette at the position
								npal = NPalettesListView.this.m_palettes.get(position);
								
								//after copying into npal, remove the actual data from the array
								NPalettesListView.this.m_palettes.remove(position);
								
								//update by removing the copy from the adapter
								NPalettesListView.this.m_adapter.remove(npal);
								//update the changes
								NPalettesListView.this.m_adapter.notifyDataSetChanged();
								
								//refresh the drawable state
								NPalettesListView.this.paletteListView.refreshDrawableState();
								
								//refresh again to be safe
								NPalettesListView.this.paletteListView.invalidate();
								
								//let the user know the palette is removed
								Toast toast = Toast.makeText(NPalettesListView.this, "Palette removed.", Toast.LENGTH_SHORT);
								toast.show();
							}
							
							else
							{
								//let the user know the palette cannot be removed
								Toast toast = Toast.makeText(NPalettesListView.this, "Cannot remove this one!", Toast.LENGTH_SHORT);
								toast.show();
							}
							
							//close the DB
							mySQLite.close();
						}
					}
				});
				
				//be sure to create and show the dialog after building it!
				removeDialog = builder.create();
				removeDialog.show();
						
				return true;
			}
		
		});
		
		/*
		/** This is for the New Palette in the bottom left of the interface
		addButton = (Button)findViewById(R.id.addPaletteButton);
		
		/** TODO: For now, it will take the saved palette 
		addButton.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				//default argument
				createDefaultPalette(0);
				
				//start the activity
				Intent optIntent = new Intent(getApplicationContext(), ColorHarmonizerActivity.class);
				startActivity(optIntent);
			}
		});
		*/
	}

	
	@Override
	public void onStart() {
		
		// TODO Auto-generated method stub
		super.onStart();
		
		SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		
		if (prefs.getString("PaletteAuthor", null) == null)
		{
			Toast toast = Toast.makeText(getApplicationContext(), "To change author, click the settings icon in the top right corner.", Toast.LENGTH_LONG);
			toast.show();
		}
		
		else
		{
			Toast toast = Toast.makeText(getApplicationContext(), "You can export or remove palettes by holding your finger over a palette.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}


	/**
	 * This is called when the app has loaded all of the UI components
	 */
	public void onResume() 
	{
		super.onResume();
		
		//clear the adapter, to ensure that there is no copy
		m_adapter.clear();
		
		viewOrders = new Runnable() {
			public void run()
			{
				
				//get the orders
				getPaletteInfo();
			}
		};
		
		//Start a thread that allows the information to be allocated
		Thread thread = new Thread(null, viewOrders, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(
				NPalettesListView.this,
				"Please wait...",
				"Retrieving data...",
				true);
	    
	}

	/**
	 * Create the ActionBar options
	 */
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.absettingsmenu, (com.actionbarsherlock.view.Menu) menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * TODO: Perform the export operation for now..
	 */
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		
		if( id == R.id.settingsMenuItem2 )
		{
			Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
			startActivity(intent);
		}
		
		else if (id == R.id.newPaletteMenuItem)
		{
			createDefaultPalette(0);
			
			Intent launchIntent = new Intent(getApplicationContext(), ColorHarmonizerActivity.class);
			startActivity(launchIntent);			
			
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Get the palette info from the SharedPreferences and SQLite
	 * FIX: Previously getOrders()
	 */
	private void getPaletteInfo()
	{	
		m_palettes = new ArrayList<NPalette>();
		
		//get the default preferences
		SharedPreferences palettePrefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		
		//prog hack to see if the PaletteID does not exist
		//if it's -1, create a default palette
		//else, get the saved data from previous session
		if (palettePrefs.getInt("PaletteID", -1) == -1)
		{
			m_palettes.add(new NPalette(0, "Default Palette", "Your Name","No description.","FF0000FFFF000000FF", 3, 999999999,999999999));			
		}
		
		else
		{
			m_palettes.add(
					new NPalette(
							palettePrefs.getInt("PaletteID", 0),
							"Current Palette",
							palettePrefs.getString("PaletteAuthor", "No Owner"),
							"Save me to change info.",
							palettePrefs.getString("Colors","FF0000FFFF000000FF"),
							palettePrefs.getInt("PaletteSize",3),
							999999999,
							999999999));
		}		
		
		//create instance of the sql adapater
		mySQLite = new NSQLiteAdapter(this);
			
		try 
		{
			mySQLite.openToRead();
			//get the table size
			int tableSize = (int)mySQLite.getTableSize();
			
			//for all records in the table
			for (int i=0; i<tableSize; i++)
			{
				//add the palettes by adapter
				m_palettes.add(mySQLite.selectByOrder(i));
			}

			//give some extra time for write
			Thread.sleep(1000);
		}
		
		catch (Exception e)
		{
			Log.e("BACKGROUND_PROC", "Cannot write to database.");
		}
		
		//call thread to update changes in the activity
		runOnUiThread(returnRes);
		mySQLite.close();
	}

	/** This is to be called when there is a change in data */
	private Runnable returnRes = new Runnable() 
	{
		public void run() 
		{
			if (m_palettes != null && m_palettes.size() > 0)
			{
				m_adapter.notifyDataSetChanged();
				//add the data
				for (int i=0; i<m_palettes.size(); i++)
					m_adapter.add(m_palettes.get(i));
			}
		
		m_ProgressDialog.dismiss();
		m_adapter.notifyDataSetChanged();
		}
	};
	
	/**
	 * The preferences will be set here so the ColorHarmonizerActivity can receive this message
	 * 
	 * @param index Choose which palette will be sent to the activity
	 */
	private void createDefaultPalette(int index)
	{
		//open the default preference
		SharedPreferences palettePrefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = palettePrefs.edit();
		
		//get the proper palette
		NPalette temp = m_palettes.get(index);
		
		//ensure that necessary information is placed, in addition, reset selected swatch to 0
		editor.putInt("PaletteID", Integer.valueOf(temp.getPaletteID())+1);
		editor.putInt("PaletteSize", Integer.valueOf(temp.getPaletteSize()));
		//if (palettePrefs.getString("PaletteAuthor", "UnanimousAuthor") != "UnanimousAuthor")
			//editor.putString("PaletteAuthor", temp.getAuthorName());
		/*
		else
			//editor.putString("PaletteAuthor", "Your Name");
		*/
		editor.putString("PaletteName", temp.getPaletteName());
		editor.putString("Colors", temp.getAllColorSwatches());
		editor.putInt("SelectedSwatch", 0);
		
		//MAKE SURE CHANGES ARE COMMITTED
		editor.commit();			
	}
	
}

package buxburt.anddev.colorharmonizer.adapter;

import java.util.Calendar;

import buxburt.anddev.colorharmonizer.model.NPalette;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * An SQLAdapter that handles database operations.
 * 
 * Allows the programmer to operate on records using insert, search, sort, and delete.
 * The following fields are "vital" to the operation of the program!
 * 
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class NSQLiteAdapter {
	
	 /** Name of the palette database */
	 protected static final String MYDATABASE_NAME = "PALETTE_DATABASE";
	 
	 /** Name of the table the records belong to */
	 protected static final String MYDATABASE_TABLE = "PALETTE_TABLE";
	 
	 /** Current version of the dB, so far, no records added */
	 protected static final int CURRENT_VERSION = 1;
	 //TODO: primary key at beginning
	 
	 /** Columns belonging to the data table */
	 protected static final String[] KEY_CONTENT = {"ID","TITLE","AUTHOR","DESCRIPTION","COLORS","AMOUNT","DATEADDED","DATECREATED"};
	 
	 /** Type of data belonging to each column */
	 protected static final String[] KEY_TYPES = {"INTEGER PRIMARY KEY ASC","VARCHAR(25)","VARCHAR(25)","VARCHAR(250)","VARCHAR(96)","INTEGER","INTEGER","INTEGER"};
	 
	 /** RAW sqlquery to create a database with table */
	 protected static final String SCRIPT_CREATE_DATABASE =
			  "create table " + MYDATABASE_TABLE + " ("
			  + KEY_CONTENT[0] + " " + KEY_TYPES[0] + ", " 
			  + KEY_CONTENT[1] + " " + KEY_TYPES[1] + ", " 
 			  + KEY_CONTENT[2] + " " + KEY_TYPES[2] + ", " 
 			  + KEY_CONTENT[3] + " " + KEY_TYPES[3] + ", " 
 			  + KEY_CONTENT[4] + " " + KEY_TYPES[4] + ", " 
 			  + KEY_CONTENT[5] + " " + KEY_TYPES[5] + ", "
 			  + KEY_CONTENT[6] + " " + KEY_TYPES[6] + ", "
 			  + KEY_CONTENT[7] + " " + KEY_TYPES[7] + "); ";
	 
	 /** A designated sqLiteHelper for easy operation */
	 protected SQLiteHelper sqLiteHelper;
	 
	 /** The instance database to perform operations on */
	 protected SQLiteDatabase sqLiteDatabase;

	 /** A context that can be shared between the activity and this class */
	 protected Context context;
	 
	 /**
	  * An SQLiteHelper that extends the functionality of the SQLiteOpenHelper.
	  * 
	  * Handles changes to the database.
	  * 
	  * @author Brandon Burton
	  * Jul 2, 2012
	  *
	  */
	 public class SQLiteHelper extends SQLiteOpenHelper {

		  public SQLiteHelper(Context context, String name,
		    CursorFactory factory, int version) {
			  super(context, name, factory, version);
		  }

		  @Override
		  public void onCreate(SQLiteDatabase db) 
		  {
		   // TODO Auto-generated method stub
			  db.execSQL(SCRIPT_CREATE_DATABASE);
		  }

		  @Override
		  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		  {
		   // TODO Auto-generated method stub

		  }

		@Override
		public synchronized void close() {
			// TODO Auto-generated method stub
			super.close();
		}
		  
		  
	 }
	 
	 /**
	  * The constructor receives the context the adapter belongs to.
	  * 
	  * @param c The context it belongs to.
	  */
	 public NSQLiteAdapter(Context c ){
		 context = c;

	 }
	 
	 /**
	  * Opens the database to read.
	  * 
	  * @return self Returns the adapter with readable permissions
	  * @throws android.database.SQLException
	  */
	 public NSQLiteAdapter openToRead() throws android.database.SQLException {
		 sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, CURRENT_VERSION);
		 sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		 return this; 
	 }
	 
	 /**
	  * Opens the database for writing.
	  * 
	  * @return self Returns the adapter with writable permissions
	  * @throws android.database.SQLException
	  */
	 public NSQLiteAdapter openToWrite() throws android.database.SQLException {
		 sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, CURRENT_VERSION);
		 sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		 return this; 
	 }
	 
	 /**
	  * Close database after functionality executed.
	  * 
	  */
	 public void close(){
		 
		 sqLiteHelper.close();
	 }
	 
	 /**
	  * Allows user to insert a new palette.
	  * 
	  * @param title The title of the palette
	  * @param author The author of the palette
	  * @param description A non-used description of the palette
	  * @param colors The colors to be sent to the database
	  * @param amountOfColors The amount of colors to be stored, used for later purposes
	  * @return long 
	  */
	 public long insert(String title, String author, String description, String colors, int amountOfColors)
	 {
		 //place content in the proper manner
		  ContentValues contentValues = new ContentValues();
		  contentValues.put(KEY_CONTENT[1], title);
		  contentValues.put(KEY_CONTENT[2], author);
		  contentValues.put(KEY_CONTENT[3], description);
		  contentValues.put(KEY_CONTENT[4], colors);
		  contentValues.put(KEY_CONTENT[5], amountOfColors);
		  
		  //used for sorting by date added
		  //TODO: refactor database from DATEADDED to DATEUPDATED
		  int s = getDBStoreTime();
		  
		  contentValues.put(KEY_CONTENT[6], s);
		  contentValues.put(KEY_CONTENT[7], s);
		  
		  //perform the insertion
		  return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	 }
	 
	 /**
	  * Update the fields in the database
	  * @param title Find the title of the palette
	  * @param colors The list of colors to insert
	  * @param amountOfColors The amount of colors to save
	  */
	 public void updateByTitle(String title, String colors, int amountOfColors)
	 {
		
		 sqLiteDatabase.execSQL("UPDATE "+MYDATABASE_TABLE+" SET "+
				 KEY_CONTENT[4]+"="+"'"+colors+"'"+" , "+
				 KEY_CONTENT[5]+"="+"'"+amountOfColors+"'"+" , "+
				 KEY_CONTENT[6]+"="+"'"+getDBStoreTime()+"'"+
				 " WHERE "+KEY_CONTENT[1]+"="+"'"+title+"'");
	 }
	 
	 /**
	  * Perform a deletion where the id matches the record's id
	  * @param id Position in the table using it's PrimaryID
	  */
	 public void deleteByID(int id)
	 {
		 //Log.i("DELETE INFO","DELETE FROM "+MYDATABASE_TABLE+" WHERE "+KEY_CONTENT[0]+"="+"'"+String.valueOf(id)+"'");
		 sqLiteDatabase.execSQL("DELETE FROM "+MYDATABASE_TABLE+" WHERE "+KEY_CONTENT[0]+"="+"'"+String.valueOf(id)+"'");
	 
	 }
	 
	 /**
	  * Erase table with it's records. Failsafe if app does not open.
	  * @return The operation to be performed
	  */
	 public int deleteAll(){
		 return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
	 }
	 
	 /**
	  * Returns all records in form of a string
	  * @return String A very large string containing all records from the table
	  */
	 public String queueAll()
	 {
		 Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+MYDATABASE_TABLE, null);
		 String result = "";
  
		  int title_CONTENT = cursor.getColumnIndex(KEY_CONTENT[1]);
		  int author_CONTENT = cursor.getColumnIndex(KEY_CONTENT[2]);
		  int desc_CONTENT = cursor.getColumnIndex(KEY_CONTENT[3]);
		  int colors_CONTENT = cursor.getColumnIndex(KEY_CONTENT[4]);
		  int amount_CONTENT = cursor.getColumnIndex(KEY_CONTENT[5]);
		  int dateadded_CONTENT = cursor.getColumnIndex(KEY_CONTENT[6]);
		  int dateupdated_CONTENT = cursor.getColumnIndex(KEY_CONTENT[7]);
		  
		  for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext())
		  {
			  result = result + cursor.getString(title_CONTENT) +
					  cursor.getString(author_CONTENT) +
					  cursor.getString(desc_CONTENT) +
					  cursor.getString(colors_CONTENT) +
					  cursor.getString(amount_CONTENT) +
					  cursor.getString(dateadded_CONTENT) +
					  cursor.getString(dateupdated_CONTENT) + "\n";
		  }
		
		  cursor.close();
		  return result;
	 }
	 
	 /**
	  * A basic query to return the table size.
	  * @return Size of the table.
	  */
	 public long getTableSize()
	 {
		String sql = "SELECT COUNT(*) FROM " + MYDATABASE_TABLE;
		SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
		long count = statement.simpleQueryForLong();
		return count;
	 }
	 
	 /**
	  * Automatically sorts the data to retrieve a record by DATE_ADDED
	  * @param position Gets the nth newest record.
	  * @return A palette with proper structure.
	  */
	 public NPalette selectByOrder(int position)
	 {
		 //TODO: Change PALETTE_TABLE to it's constant string form
		Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM PALETTE_TABLE ORDER BY "+KEY_CONTENT[6]+" DESC", null);
		
		int id_CONTENT = cursor.getColumnIndex(KEY_CONTENT[0]);
		int title_CONTENT = cursor.getColumnIndex(KEY_CONTENT[1]);
		int author_CONTENT = cursor.getColumnIndex(KEY_CONTENT[2]);
		int desc_CONTENT = cursor.getColumnIndex(KEY_CONTENT[3]);
		int colors_CONTENT = cursor.getColumnIndex(KEY_CONTENT[4]);
		int amount_CONTENT = cursor.getColumnIndex(KEY_CONTENT[5]);
		int dateadded_CONTENT = cursor.getColumnIndex(KEY_CONTENT[6]);
		int dateupdated_CONTENT = cursor.getColumnIndex(KEY_CONTENT[7]);
		
		NPalette tempPal = new NPalette();
		
		if (cursor.move(position+1))
		{
			int id = cursor.getInt(id_CONTENT);
			String title = 	cursor.getString(title_CONTENT);
			String author = cursor.getString(author_CONTENT);
			String desc = cursor.getString(desc_CONTENT);
			String colors = cursor.getString(colors_CONTENT);
			int amount = cursor.getInt(amount_CONTENT);
			int dateAdded = cursor.getInt(dateadded_CONTENT);
			int dateUpdated = cursor.getInt(dateupdated_CONTENT);
			tempPal = new NPalette(id, title, author, desc, colors, amount, dateAdded, dateUpdated);
		}

		cursor.close();
		
		return tempPal;
	 }
	 
	 /**
	  * Retrieve data from the record's id. Used to grab records outright.
	  * @param index The id to retrieve.
	  * @return A properly structured palette.
	  */
	 public NPalette selectAtIndex(int index)
	 {
		Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM PALETTE_TABLE WHERE ID="+String.valueOf(index+1)+" ORDER BY "+KEY_CONTENT[6]+ " DESC", null);
		
		int title_CONTENT = cursor.getColumnIndex(KEY_CONTENT[1]);
		int author_CONTENT = cursor.getColumnIndex(KEY_CONTENT[2]);
		int desc_CONTENT = cursor.getColumnIndex(KEY_CONTENT[3]);
		int colors_CONTENT = cursor.getColumnIndex(KEY_CONTENT[4]);
		int amount_CONTENT = cursor.getColumnIndex(KEY_CONTENT[5]);
		int dateadded_CONTENT = cursor.getColumnIndex(KEY_CONTENT[6]);
		int dateupdated_CONTENT = cursor.getColumnIndex(KEY_CONTENT[7]);	
		
		NPalette tempPal = new NPalette();
		
		if (cursor.moveToFirst() == true)
		{
			String title = 	cursor.getString(title_CONTENT);
			String author = cursor.getString(author_CONTENT);
			String desc = cursor.getString(desc_CONTENT);
			String colors = cursor.getString(colors_CONTENT);
			int amount = cursor.getInt(amount_CONTENT);
			int dateAdded = cursor.getInt(dateadded_CONTENT);
			int dateUpdated = cursor.getInt(dateupdated_CONTENT);
			tempPal = new NPalette(index, title, author, desc, colors, amount, dateAdded, dateUpdated);
		}
		
		cursor.close();
		
		return tempPal;
	 }
	 
	 /**
	  * Returns an algorithmic representation of the date.
	  * @return The preferred date algorithm.
	  */
	 private int getDBStoreTime()
	 {
		 Calendar ci = Calendar.getInstance();
		  //get this to work with the millenium
		  
		 int s = (ci.get(Calendar.YEAR)-2000) * 31557600 + ci.get(Calendar.DAY_OF_YEAR)*86400+ci.get(Calendar.HOUR)*3600+ci.get(Calendar.MINUTE)*60+ci.get(Calendar.SECOND);  
		 return s;
	 }
}

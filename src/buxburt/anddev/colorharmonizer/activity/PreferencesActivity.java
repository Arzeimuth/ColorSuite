package buxburt.anddev.colorharmonizer.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.constant.Constants;

public class PreferencesActivity extends Activity 
{
	EditText textAuthorName;
	String tempString;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		
		SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		
		tempString = prefs.getString("PaletteAuthor", "Your Name");
		
		textAuthorName = (EditText)findViewById(R.id.textPrefrencesAuthor);
		textAuthorName.setText(tempString);
		
		textAuthorName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) 
			{
				// TODO Auto-generated method stub
				tempString = s.toString();
			}
		});		
	}
	
	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		
		if (tempString != "Your Name")
			saveAuthorToPreferences(tempString);
		super.onPause();
	}

	private void saveAuthorToPreferences(String authorName)
	{
		SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putString("PaletteAuthor", authorName);
		
		editor.commit();
		
	}
}

package buxburt.anddev.colorharmonizer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import buxburt.anddev.colorharmonizer.R;

/**
 * Entry point for application.
 * 
 * This activity is shown prior to NPaletteListView (Activity).
 * 
 * @author Brandon Burton
 * @version 2012.0702
 * @since 1.0
 */
public class SplashScreenActivity extends Activity 
{
	/**
	 * Animation class that holds animation parameters. */
	AlphaAnimation animation;
	/**
	 * The ImageView performing the fade-in transition. */
	ImageView splashimg;
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splashscreen);

		//Set up fade in animation
		animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(1000);
		
		//the image that has the transition effect, play animation once
		splashimg = (ImageView) findViewById(R.id.bigLogo);
		splashimg.setAnimation(animation);

		// Thread to waste time while displaying splash screen
		Thread SplashThread = new Thread() 
		{
			@Override
			public void run() 
			{
				try 
				{
					synchronized (this) 
					{
						// Wait given period of time (3000 seconds)
						wait(3000);
					}
				} 
				
				catch (InterruptedException ex) 
				{
				}

				finish();

				// Run next activity
				Intent intent = new Intent();
				intent.setClass(SplashScreenActivity.this, NPalettesListView.class);
				startActivity(intent);

				//Terminate splash screen
				SplashScreenActivity.this.finish();

			}
		};

		//Start the thread
		SplashThread.start();
	}
}

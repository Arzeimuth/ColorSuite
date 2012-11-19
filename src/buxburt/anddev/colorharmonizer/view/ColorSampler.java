package buxburt.anddev.colorharmonizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * A view that holds information based on position. Commonly used in sampling views.
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class ColorSampler extends View {
	
	/** Unique ID of the sample */
	private int uniqueId;
	/** The radius of the drawn circle */
	private int radius;
	/** The x and y coordinates of the sample */
	private float x, y;
	/** Paint for the crosshair */
	private Paint crosshairPaint;

	public ColorSampler(Context context) {
		super(context);
		onInitialize();
		// TODO Auto-generated constructor stub
	}

	public ColorSampler(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		onInitialize();

	}

	public ColorSampler(Context context, AttributeSet attrs, int defStyle) {
		// TODO Auto-generated constructor stub
		super(context, attrs, defStyle);
		onInitialize();
	}
	
	/**
	 * Grant initial coordinates, so the app does not crash
	 */
	private void onInitialize()
	{
		x = 200;
		y = 200;
		uniqueId = 0;
		radius = 25;
		crosshairPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		crosshairPaint.setStrokeWidth(2);
		crosshairPaint.setStyle(Style.STROKE);
		crosshairPaint.setColor(0x88000000);
		crosshairPaint.setTextSize(12);
		
	}
	
	/**
	 * Perform the drawing of the effective area and it's sample
	 */
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawRect(new RectF(x - radius, y - radius, x + radius, y + radius), crosshairPaint);
		canvas.drawOval(new RectF(x - radius/5, y - radius/5, x + radius/5, y + radius/5), crosshairPaint);
		canvas.drawText(String.valueOf(uniqueId+1), x + radius/5, y - radius/5, crosshairPaint);
	}
	
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
	}
	
	/**
	 * Set the position based on the x and y coordinates given
	 * @param ix X Coordinate
	 * @param iy Y Coordinate
	 */
	public void setPosition(float ix, float iy)
	{
		x = (int)ix;
		y = (int)iy;
	}
	
	/**
	 * An absolutely vital function to set the id of the sampler
	 */
	public void setId(int uniqueId)
	{
		this.uniqueId = uniqueId;
		Log.d("Unique ID:", String.valueOf(uniqueId));
	}
	
	/**
	 * Get the x coordinate
	 * @return X X Coordinate
	 */
	public float getPositionX()
	{
		return x;
	}
	
	/**
	 * Get the y coordinate
	 * @return Y Y Coordinate
	 */
	public float getPositionY()
	{
		return y;
		//this.getB
	}
	
	/**
	 * Get the bounding rectangle for calculation reasons
	 * @return RectF The effective rectangle
	 */
	public RectF getBoundingRectangle()
	{
		return new RectF(x - radius, y - radius, x + radius, y + radius);
	}
	
	/**
	 * @return uniqueId Return the unique ID.
	 */
	public int getId()
	{
		return uniqueId;
	}

}


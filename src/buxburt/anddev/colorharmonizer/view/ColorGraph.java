package buxburt.anddev.colorharmonizer.view;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Base class for all ColorWheel, ColorView views
 * 
 * The ColorGraph view is a base view for many views that are dependent on their parent sizes
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class ColorGraph extends View {
	
	/** The initial viewWidth */
	protected int viewWidth = 400;
	/** The initial viewHeight */
	protected int viewHeight = 400;
	/** The initial center in X */
	protected int centerX = 60;
	/** The initial center in Y */
	protected int centerY = 60;
	/** The initial padding */
	protected int padding = 5;
	
	/** Holds all color data */
	protected ArrayList<ColorSampler> colorSamples;
	
	public ColorGraph(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		onInitialize();
	}

	public ColorGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		onInitialize();
	}

	public ColorGraph(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		onInitialize();
	}
	
	/**
	 * Construct the colorSamples data
	 */
	protected void onInitialize()
	{
		colorSamples = new ArrayList<ColorSampler>();
	}
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}

	/**
	 * A special method that sets the values when size change occurs.
	 */
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) 
	{
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		viewWidth = xNew;
		viewHeight = yNew;
		centerX = viewWidth / 2;
		centerY = viewHeight / 2;
	}
	
	/**
	 * A special method that sets the values when layout
	 */
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		viewWidth = right - left;
		viewHeight = bottom - top;
		centerX = viewWidth / 2;
		centerY = viewHeight / 2;
	}
	
	/**
	 * Certify that measurements are genuine by adding in all the other factors such as padding.
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}
	
	protected void onAttachedToWindow() 
	{
		super.onAttachedToWindow();
	}
	
	protected int measureWidth(int widthMeasureSpec)
	{
		int specSize = MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingLeft() - this.getPaddingRight();	
		return specSize;		
	}
	
	protected int measureHeight(int heightMeasureSpec)
	{
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		return specSize;
	}
}

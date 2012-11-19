package buxburt.anddev.colorharmonizer.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.engine.ColorEngine;
import buxburt.anddev.colorharmonizer.helper.ColorMessage;
import buxburt.anddev.colorharmonizer.model.NColor;

/**
 * A custom view that holds all sampling calculations. Relies on the ColorEngine.
 * @author Brandon Burton
 * Jul 2, 2012
 *
 * TODO: Extend this to cover OpenGL surface draw over software draw
 */
public class ColorWheelView extends ColorGraph implements ColorMessage {

	/** A list of all the colors */
	private int[] mColors;
	// TODO: add application level implementation
	/** The actual rainbow paint, artistic or scientific*/
	protected Paint mPaint;
	/** The border drawn around to separate from other views */
	protected Paint borderPaint;
	/** The border drawn around each sample */
	protected Paint smallBorderPaint;
	
	/** Dimensions to accelerate shader rendering*/
	protected RectF borderDim;
	protected RectF colorDim;

	public ColorWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onInitalize();
		// TODO Auto-generated constructor stub
	}

	public ColorWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onInitalize();
		// TODO Auto-generated constructor stub
	}

	public ColorWheelView(Context context) {
		super(context);
		onInitalize();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		super.onDraw(canvas);

		canvas.translate(centerX, centerY);
		
		canvas.drawOval(borderDim, borderPaint);
		canvas.drawOval(colorDim, mPaint);
		
		canvas.translate(-centerX, -centerY);
		
		for (int i=0; i<colorSamples.size(); i++)
		{
			//draw all samples here
			colorSamples.get(i).onDraw(canvas);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		//get the resources
		Resources n = getResources();
		//initial artistic shader
		mColors = n.getIntArray(R.array.artistic_shader);

		//radius of circle is obtained here
		float radius = Math.min(viewHeight / 2, viewWidth / 2);

		//the combination of both gradients
		Shader totalShade = new SweepGradient(0, 0, mColors, null);
		//the spectral representation of the color
		Shader hueShade = new SweepGradient(0, 0, mColors, null);
		//the white to hue of the color
		Shader saturationShade = new RadialGradient(0, 0, radius, 0xFFFFFFFF,
				0x00FFFFFF, Shader.TileMode.CLAMP);

		totalShade = new ComposeShader(hueShade, saturationShade,
				PorterDuff.Mode.SCREEN);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setDither(true);
		mPaint.setShader(totalShade);
		mPaint.setStyle(Paint.Style.FILL);
		
		float r = radius - padding;
		
		colorDim = new RectF(-r, -r, r, r);
		borderDim = new RectF(-r, -r, r, r);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(xNew, yNew, oldw, oldh);	
	}

	/**
	 * Perform similar operations to onCreate(), but this is because it's called after layout
	 */
	public void onInitalize() {

		super.onInitialize();
		
		Resources n = getResources();
		mColors = n.getIntArray(R.array.artistic_shader);

		float radius = Math.min(viewHeight / 2, viewWidth / 2);

		Shader totalShade = new SweepGradient(0, 0, mColors, null);
		Shader hueShade = new SweepGradient(0, 0, mColors, null);
		Shader saturationShade = new RadialGradient(0, 0, radius, 0xFFFFFFFF,
				0x00FFFFFF, Shader.TileMode.CLAMP);

		totalShade = new ComposeShader(hueShade, saturationShade,
				PorterDuff.Mode.SCREEN);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setDither(true);
		mPaint.setShader(totalShade);
		mPaint.setStyle(Paint.Style.FILL);
		
		borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setStrokeWidth(7);
		borderPaint.setColor(getResources().getColor(R.color.theme_grey));
		borderPaint.setStyle(Paint.Style.STROKE);
		
		this.invalidate();
	}

	@Override
	public boolean isInEditMode() {
		// TODO Auto-generated method stub
		return super.isInEditMode();
	}

	/**
	 * Add a sampler
	 */
	public void addSampler()
	{
		ColorSampler newSampler = new ColorSampler(this.getContext());
		newSampler.setId(colorSamples.size());
		colorSamples.add(newSampler);
	}
	
	/**
	 * Remove a sampler
	 */
	public void removeSampler()
	{
		colorSamples.remove(colorSamples.size()-1);
	}
	
	/**
	 * If the criteria matches, move a sampler to x, y
	 * @param x X coordinate
	 * @param y Y Coordinate
	 */
	public void moveSampler(float x, float y)
	{
		if (ColorEngine.selectedSwatch < colorSamples.size())
		{
			float userRadius = (float)Math.sqrt(Math.pow(x-centerX,2) +  Math.pow(y-centerY, 2))+2f;
			float limitRadius = Math.min(viewHeight / 2, viewWidth / 2)-padding;
			
			//make sure the sampler does not escape the radius
			if(userRadius > limitRadius)
			{
				x = (limitRadius/userRadius) * (x - centerX) + centerX;
				y = (limitRadius/userRadius) * (y - centerY) + centerY;
			}
			
			colorSamples.get(ColorEngine.selectedSwatch).setPosition(x, y);
		}
		
		//update
		sendMessage();
		this.invalidate();
	}
	
	/**
	 * Receives the gesture and locates each sample
	 * @param x The x coordinate of the gesture
	 * @param y The y coordinate of the gesture
	 */
	public void locateSampler(float x, float y)
	{
		int i = 0;
		
		//maximum radius
		//float maxr = Math.min(viewWidth/2, viewHeight/2) - padding;
		//input radius
		//float inputr = (float)Math.sqrt((float)(x*x) + (float)(y*y));

		while (i < colorSamples.size())
		{
			//TODO: the effective box is 20x20 big, make these values changable
			if (colorSamples.get(i).getBoundingRectangle().intersect(new RectF(x-10, y-10, x+10, y+10)))
			{
				ColorEngine.selectedSwatch = i;
				i=colorSamples.size() + 1;
				return;
			}
			i++;
		}
	}
	
	/**
	 * Update the ColorEngine with the performed data.
	 */
	public void sendMessage()
	{
		ColorSampler a = null;
		a = colorSamples.get(ColorEngine.selectedSwatch);
		
		float ix = a.getPositionX() - (viewWidth / 2);
		float iy = -1*(a.getPositionY() - (viewHeight / 2));
		
		//maximum radius
		float maxr = Math.min(viewWidth/2, viewHeight/2) - padding;
		//input radius
		float inputr = (float)Math.sqrt((float)(ix*ix) + (float)(iy*iy));
		
		float angle = 0;
		float sat = 0;
		
		if (inputr > 0.0)
		{
			angle = (float)Math.acos(ix / inputr) * 180 / (float)Math.PI;
			if (iy < 0)
				angle = 360 - (float)Math.acos(ix / inputr) * 180 / (float)Math.PI;
		}
		
		if (inputr < maxr)
		{
			sat = (float)(inputr / maxr);
		}
		
		ColorEngine.recieveHSVMessage(angle, sat, -1.0f);
	}
	
	/**
	 * Auto-align to colors received from the ColorEngine
	 */
	public void recieveMessage()
	{
		int i = 0;
		while (i < colorSamples.size())
		{
			NColor temp = ColorEngine.sendNColor(i);
			float hue;
			
			if (ColorEngine.colorWheelType == false)
				hue = temp.hy;
			else
				hue = temp.h;
			float sat = temp.s;
			float x = (viewWidth / 2) + (sat * (Math.min(viewWidth/2, viewHeight/2) - padding)*(float)Math.cos(hue * Math.PI/180));
			float y = (viewHeight / 2) + (sat * (Math.min(viewWidth/2, viewHeight/2) - padding)*(float)Math.sin(-1 * hue * Math.PI/180));
			colorSamples.get(i).setPosition(x,y);
			i++;
		}
	}
}

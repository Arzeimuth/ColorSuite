package buxburt.anddev.colorharmonizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * A custom UI button that has 3 modes.
 * 
 * A button that has an active, option2, and inactive state.
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class ColorChooserButton extends Button {
	/** Used for swatch operations */
	int id;
	/** The view width*/
	int viewWidth=10;
	/** The view height*/
	int viewHeight=10;
	/** Size of the drawn triangle */
	int triSize = 10;
	
	Paint selectedPaint;
	Paint linkedPaint;
	Paint freePaint;
	//is this active?
	boolean isActive;
	//is this linked?
	boolean isLinked;

	public ColorChooserButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		onInitialize();
	}

	public ColorChooserButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		onInitialize();
	}

	public ColorChooserButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		onInitialize();
	}
	
	/**
	 * Set defaults here
	 */
	private void onInitialize()
	{
		selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		selectedPaint.setColor(Color.WHITE);
		selectedPaint.setStyle(Paint.Style.FILL);
		
		linkedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linkedPaint.setColor(Color.GRAY);
		linkedPaint.setStyle(Paint.Style.FILL);
		
		freePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		freePaint.setColor(Color.BLACK);
		freePaint.setStyle(Paint.Style.FILL);
	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int w = viewWidth;
		int h = viewHeight;
		Path path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		path.moveTo(w, h);
		path.lineTo(w-triSize, h);
		path.lineTo(w, h-triSize);
		path.lineTo(w, h);
		path.close();
		
		Paint ptrPaint;
		if(isActive)
			ptrPaint = selectedPaint;
		else if(isLinked && !isLinked)
			ptrPaint = linkedPaint;
		else
			ptrPaint = freePaint;
			
		canvas.drawPath(path, ptrPaint);
	}
	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//MeasureSpec measureSpecs = new MeasureSpec();
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);
	}
	
	public void OnInitialize()
	{
		id = -1;
		isActive = false;
		isLinked = false;
	}
	
	/**
	 * Set it to State 1, or active swatch
	 */
	public void setActive()
	{
		isActive=true;
		isLinked=false;
	}
	
	/**
	 * Set it to State 2, or linked harmony swatch
	 */
	public void setLinked()
	{
		isActive=false;
		isLinked=true;
	}
	
	/**
	 * Set it to State 3, or inactive swatch
	 */
	public void setFree()
	{
		isActive=false;
		isLinked=false;
	}
}

package buxburt.anddev.colorharmonizer.model;

import buxburt.anddev.colorharmonizer.helper.MathExt;
import android.graphics.Color;

/**
 * The base color class to be used in all color intensive tasks.
 * @author Brandon Burton
 * Jul 2, 2012
 *
 * TODO: Remove all yellow component calculations and ensure stability
 * TODO: Remove all string based names and dates, no longer significant
 */
public class NColor {

	/** Red color */
	public float r;
	/** Green color */
	public float g;
	/** Blue color */
	public float b;
	//TODO: remove this next statement
	/** Yellow color (never used) */
	public float y;
	/** Alpha color (never used) */
	public float a;

	/** Hue component */
	public float h;
	/** Hue (artistic) component */
	public float hy;
	/** Saturation component */
	public float s;
	/** Value component */
	public float v;

	//TODO: remove these fields
	/** Optional swatch field */ 
	String userColorName;
	/** The visual color name */
	String progColorName;
	/** Datetime string*/
	String dateTime;

	/**
	 * Create a default color
	 */
	public NColor() {
		// TODO Auto-generated constructor stub
		setDefault();
	}

	/**
	 * Based on Android color, send in the integer value. Converts all from 0-255 to 0-1
	 * @param c The color as an integer.
	 */
	public NColor(int c) {
		setDefault();
		
		this.r = ((float)Color.red(c))/255.000f;
		this.g = ((float)Color.green(c))/255.000f;
		this.b = ((float)Color.blue(c))/255.000f;
		this.a = 1.0f;
		setHSVfromRGB();
		setYfromRGB();
		setHYfromH();

	}
	
	/**
	 * Create a color based on a normalized r,g,b,y,a color set
	 * @param r Red component
	 * @param g Green component
	 * @param b Blue component
	 * @param y Yellow component
	 * @param a Alpha component
	 */
	public NColor(float r, float g, float b, float y, float a) {
		setDefault();
		this.r = r;
		this.g = g;
		this.b = b;
		if (g == 0.0f)
			this.y = y;
		this.a = a;
		setHSVfromRGB();
		setYfromRGB();
		setHYfromH();
	}

	/**
	 * Sets the components to white
	 */
	public void setDefault() {
		r = 0.0f;
		g = 0.0f;
		b = 0.0f;
		y = 0.0f;
		a = 0.0f;

		h = 0.0f;
		hy = 0.0f;
		s = 0.0f;
		v = 1.0f;

		userColorName = "undefined";
		progColorName = "undefined";
		dateTime = "undefined";
	}

	/**
	 * Returns all components for debug.
	 * @return String For debug purposes, return all components.
	 */
	public String toString() {
		StringBuilder n = new StringBuilder();
		n.append("R: " + r + " ");
		n.append("G: " + g + " ");
		n.append("B: " + b + " ");
		n.append("Y: " + y + " ");
		n.append("A: " + a + "\n");
		n.append("h: " + h + " ");
		n.append("hy: " + hy + " ");
		n.append("s: " + s + " ");
		n.append("v: " + v + " ");
		return n.toString();
	}
	
	/**
	 * Special return color as hex
	 * @return String Hexadecimal representation
	 */
	public String toHex()
	{
		StringBuilder t = new StringBuilder();
		//exlcude alpha value
		t.append(String.valueOf(Integer.toHexString(Color.rgb((int)(r*255), (int)(g*255), (int)(b*255)))).substring(2,8));
		//if the string is not long enough
		if (t.length() < 6)
		{
			int j = t.length();
			for (int i = j; i<6; i++)
			{
				//append 0's until the color is full
				//THIS IS IMPORTANT TO RETURN A 6 LENGTH CHARACTER!
				t.insert(1, "0");
			}
		}
		
		return t.toString();
	}

	/**
	 * A special function that designates an assignment between RGB and RYB
	 * 
	 * @param mode
	 *            - 0 for scientific, 1 for artistic
	 * @param h The hue or hue (artistic) component value (0-360)
	 * @param s The saturation component
	 * @param v The value component
	 */
	public void setByHSV(boolean mode, float h, float s, float v) {
		
		this.s = (s >= 0.0) ? s : this.s;
		this.v = (v >= 0.0) ? v : this.v;

		if (h >= 0.0)
		{
			//if this is the artistic color setting
			if (!mode) {
				this.hy = h;
				setHfromHY();
			}
	
			//if this is the scientific color setting
			else {
				this.h = h;
				setHYfromH();
			}
		}
	
		//resolve RGB from HSV
		setRGBfromHSV();
		//resolve Y from RGB
		setYfromRGB();
	}

	/**
	 * Accepts normalized r, g, and b components
	 * @param r Red component
	 * @param g Green component
	 * @param b Blue component
	 */
	public void setByRGB(float r, float g, float b) {
		this.r = (r >= 0.0) ? r : this.r;
		this.g = (g >= 0.0) ? g : this.g;
		this.b = (b >= 0.0) ? b : this.b;

		setHSVfromRGB();
		setHYfromH();
		setYfromRGB();
	}
	
	/**
	 * Sets the Y component from RGB
	 * TODO: Remove this
	 */
	private void setYfromRGB() {
		float w, my, mg, n;

		float ir = r;
		float ig = g;
		float ib = b;

		w = MathExt.min_extended(ir, ig, ib);
		ir -= w;
		ig -= w;
		ib -= w;

		mg = MathExt.max_extended(ir, ig, ib);

		y = Math.min(ir, ig);
		ir -= y;
		ig -= y;

		if (ib > 0.0 && ig > 0.0) {
			ib /= 2.0;
			ig /= 2.0;
		}

		y += ig;
		ib += ig;

		my = MathExt.max_extended(ir, y, ib);

		if (my > 0.0) {
			n = mg / my;
			y *= n;
		}
	}

	/**
	 * This calculation is used for scientific color model
	 */
	private void setHYfromH() {
		if (h <= 60.0f) {
			hy = MathExt.linear_interpolate(0.0f, 60.0f, 0.0f, 120.0f, h);
		}

		else if (h > 60.0f && h <= 120.0f) {
			hy = MathExt.linear_interpolate(60.0f, 120.0f, 120.0f, 180.0f, h);
		}

		else if (h > 120.0f && h <= 180.0f) {
			hy = MathExt.linear_interpolate(120.0f, 180.0f, 120.0f, 210.0f, h);
		}

		else if (h > 180.0f && h <= 240.0f) {
			hy = MathExt.linear_interpolate(180.0f, 240.0f, 210.0f, 240.0f, h);
		}

		else
			hy = h;
	}

	/**
	 * This calculation is used for artsitic color model
	 */
	private void setHfromHY() {
		if (hy <= 120.0f) {
			h = MathExt.linear_interpolate(0.0f, 120.0f, 0.0f, 60.0f, hy);
		}

		else if (hy > 120.0f && hy <= 180.0f) {
			h = MathExt.linear_interpolate(120.0f, 180.0f, 60.0f, 120.0f, hy);
		}

		else if (hy > 180.0f && hy <= 210.0f) {
			h = MathExt.linear_interpolate(180.0f, 210.0f, 120.0f, 180.0f, hy);
		}

		else if (hy > 210.0f && hy <= 240.0f) {
			h = MathExt.linear_interpolate(210.0f, 240.0f, 180.0f, 240.0f, hy);
		}

		else
			h = hy;

	}

	/**
	 * An algorithm that overrides RGB components based on HSV
	 */
	private void setRGBfromHSV() {
		int i;
		float f, p, q, t;

		if (s == 0) {
			b = g = r = v;
		}

		float ih = h;
		ih /= 60;
		i = (int) Math.floor(ih);
		f = ih - i;
		p = v * (1 - s);
		q = v * (1 - s * f);
		t = v * (1 - s * (1 - f));

		switch (i) {
		case 0:
			r = v;
			g = t;
			b = p;
			break;
		case 1:
			r = q;
			g = v;
			b = p;
			break;
		case 2:
			r = p;
			g = v;
			b = t;
			break;
		case 3:
			r = p;
			g = q;
			b = v;
			break;
		case 4:
			r = t;
			g = p;
			b = v;
			break;
		default: // case 5:
			r = v;
			g = p;
			b = q;
			break;
		}
	}

	/**
	 * An algorithm that overrides HSV components from RGB components
	 */
	private void setHSVfromRGB() {
		float ir = r;
		float ig = g;
		float ib = b;

		v = MathExt.max_extended(ir, ig, ib);
		if (v == 0.0f) {
			h = 0.0f;
			s = 0.0f;
		}

		ir /= v;
		ig /= v;
		ib /= v;

		float min = MathExt.min_extended(ir, ig, ib);
		float max = MathExt.max_extended(ir, ig, ib);

		s = max - min;
		if (s == 0.0f) {
			h = 0.0f;
		}

		ir = (ir - min) / (max - min);
		ig = (ig - min) / (max - min);
		ib = (ib - min) / (max - min);

		min = MathExt.min_extended(ir, ig, ib);
		max = MathExt.max_extended(ir, ig, ib);

		if (max == ir) {
			h = 0.0f + (60.0f * (ig - ib));
			if (h < 0.0f) {
				h += 360.0f;
			}
		}

		else if (max == ig) {
			h = 120.0f + (60.0f * (ib - ir));
		}

		else {
			h = 240.0f + (60.0f * (ir - ig));
		}
	}
	
	/**
	 * Returns the color to integer form. For use with View components
	 * @return
	 */
	public int toInt()
	{
		return Color.argb(0xff, (int)(r * 255), (int)(g * 255),(int)(b * 255));
	}
}

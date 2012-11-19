package buxburt.anddev.colorharmonizer.engine;

import java.util.ArrayList;

import buxburt.anddev.colorharmonizer.model.NColor;

import android.graphics.Color;
import android.util.Log;

/**
 * ColorEngine is the class responsible for handling all color-based computations.
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class ColorEngine {
	
	/** For singleton purposes */
	private static ColorEngine ref;
	
	/** A list of NColors */
	private static ArrayList<NColor> colorPalette;
	
	/** The palette name */
	public static String paletteName;
	
	/** The author name */
	public static String paletteAuthor;
	
	/** Type of wheel */
	public int wheelType;
	
	/** What is the harmony mode */
	public static int harmonyMode;
	
	/** Is this harmonizing? */
	public static boolean isHarmonizing;

	/** What is the selected swatch to perform operations on? */
	public static int selectedSwatch;
	
	//TODO: Create enum to clear confusion
	/** The color wheel type, is either Artistic (true) or Scientific (false) */
	public static boolean colorWheelType;
	
	/** ColorModelMode is HSV by default*/
	public static int colorModelMode;
	
	//TODO: report this update
	//added for reference to DB
	public static int paletteID;
	
	private ColorEngine() 
	{
		//double keepsafe to ensure engine starts when activity requires it
		ColorEngine.start();
	}
	
	/**
	 * Grant default values to respective fields
	 */
	public static void start() 
	{
		
		colorPalette = new ArrayList<NColor>();
		ColorEngine.paletteName = "Untitled";
		ColorEngine.paletteAuthor = "Your Name";
		//artistic for false, scientific for true
		ColorEngine.colorWheelType = false;
		ColorEngine.selectedSwatch = 0;
		ColorEngine.harmonyMode = 0;
		ColorEngine.colorModelMode = 0;
	}
	
	/**
	 * This makes sure that there is can be threaded safely
	 * @return Reference to itself
	 */
	public static synchronized ColorEngine getColorEngine() {
		if (ref == null)
			ref = new ColorEngine();
		return ref;
	}
	 
	/**
	 * This class cannot be cloned
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * Add a color to the ColorEngine
	 * @param c Add the color (int)
	 */
	public static void addColor(int c)
	{
		colorPalette.add(new NColor(c));
	}
	
	/**
	 * Add a color by NColor
	 * @param c Add the NColor
	 */
	public static void addColor(NColor c) 
	{
		colorPalette.add(c);
	}
	
	/**
	 * Based on index, edit that color
	 * @param key The key the color belongs to
	 * @param c The to color requested
	 */
	public static void editColor(int key, NColor c)
	{
		if (key < colorPalette.size())
		{
			colorPalette.set(key, c);
		}
		
		else
		{
			Log.e("INDEX DOES NOT EXIST", "OK");
		}
	}
	
	/**
	 * Get a debug string for the color
	 * @param key
	 * @return String A color in string form
	 */
	public static String getNColorAttr(int key)
	{
		if (key < colorPalette.size())
			return colorPalette.get(key).toString();
		else
			return "DNE";
		
	}
	
	/**
	 * Remove a color based on your choice
	 * @param key The index to remove
	 */
	public static void removeColor(int key)
	{
		if (key != 0)
		{
			colorPalette.remove(key);
			if (size() > 1)
				ColorEngine.selectedSwatch--;
		}
		else
		{
			Log.i("CANNOT REMOVE","OK");
		}
	}
	
	/**
	 * The size of the color palette
	 * @return int Size of current palette
	 */
	public static int size()
	{
		return colorPalette.size();
	}
	
	/**
	 * Get an NColor
	 * @param index
	 * @return NColor Based on index
	 */
	public static NColor sendNColor(int index)
	{
		return colorPalette.get(index);
	}
	
	/**
	 * Handles all messages that depend on the hue
	 * @return Hue component
	 */
	public static int sendHueMessage()
	{
		int h;
		if (colorWheelType)
			h = (int)(colorPalette.get(selectedSwatch).hy * 100);
		else
			h = (int)(colorPalette.get(selectedSwatch).h * 100);
		return h;
	}
	
	/**
	 * Handles all messages that depend on the saturation
	 * @return Saturation component
	 */
	
	public static int sendSaturationMessage()
	{
		return (int)(colorPalette.get(selectedSwatch).s * 100);
	}
	
	/**
	 * Handles all messages that depend on value
	 * @return Value component
	 */
	public static int sendValueMessage()
	{

		return (int)(colorPalette.get(selectedSwatch).v  * 100);		
	}
	
	/**
	 * Handles all message that depend on hex value
	 * @return Hex value
	 */
	public static String sendHexMessage()
	{
		NColor c = colorPalette.get(selectedSwatch);
		int i = Color.rgb((int)(c.r*255), (int)(c.g*255), (int)(c.b*255));
		return "#"+Integer.toHexString(i).substring(2,8);
	}
	
	/**
	 * Send an array of colors as integers
	 * @return Array of Integers
	 */
	public static int[] sendColorMessage()
	{
		int size = colorPalette.size();
		int[] msg = new int[size+1];
		msg[0]=size;
		for (int i=1; i<msg[0]+1; i++)
			msg[i]=colorPalette.get(i-1).toInt();
		return msg;
	}
	
	/**
	 * Receive a change in value
	 * @param val Value component
	 */
	public static void recieveVMessage(float val)
	{
		NColor a = colorPalette.get(selectedSwatch);
		a.setByHSV(colorWheelType, -1, -1, val);
		colorPalette.set(selectedSwatch, a);
		updateConstraints();
	}
	
	
	/**
	 * Recieve a change in r, g, b components 
	 * @param r Red component 
	 * @param g Green component
	 * @param b Blue component
	 */
	public static void recieveRGBMessage(float r, float g, float b)
	{
		NColor a = colorPalette.get(selectedSwatch);
		a.setByRGB(r, g, b);
		colorPalette.set(selectedSwatch, a);
		updateConstraints();
	}
	
	/**
	 * Receive a change in HSV components
	 * @param hue Hue component
	 * @param sat Saturation component
	 * @param val Value component
	 */
	public static void recieveHSVMessage(float hue, float sat, float val)
	{
		NColor a = colorPalette.get(selectedSwatch);
		a.setByHSV(colorWheelType, hue, sat, val);
		colorPalette.set(selectedSwatch, a);
		updateConstraints();
	}
	
	/**
	 * Get swatch as an integer
	 * @param id Index of swatch
	 * @return int Integer color representaton 
	 */
	public static int getSwatchAsColor(int id)
	{
		return colorPalette.get(id).toInt();
	}
	
	/**
	 * Another function to retrieve Color data in String form
	 * @param id Index of swatch
	 * @return String String represenation of color
	 */
	public static String getSwatchAsData(int id)
	{
		StringBuilder str = new StringBuilder();
		NColor tmp;
		
		if (id < size() && id > -1)
		{
			tmp = colorPalette.get(id);
			
			int r = (int)(tmp.r * 255.0f);
			int g = (int)(tmp.g * 255.0f);
			int b = (int)(tmp.b * 255.0f);
			float h = tmp.h;
			float s = tmp.s;
			float v = tmp.v;
			
			//Red, Green, Blue
			str.append("Red: " + String.valueOf(r) + "  ");
			str.append("Green: " + String.valueOf(g) + "  ");
			str.append("Blue: " + String.valueOf(b) + "  " + "\n");
			
			//Hue, Saturation, and Value
			str.append("Hue: " + String.valueOf(h) + "  ");
			str.append("Sat: " + String.valueOf(s) + "  ");
			str.append("Val: " + String.valueOf(v) + "  " + "\n");
			
			//Hexadecimal information
			str.append("Hex: #" + tmp.toHex() + "\n\n");	
		}
		
		return str.toString();
	}
	
	/**
	 * Parses colors for export reasons
	 * @return String Parsed palette string
	 */
	public static String parseColorsToExport()
	{
		String tmp;
		tmp = "Palette Title: \n";
		for (int i=0; i<size(); i++)
		{
			tmp+="Palette Number"+String.valueOf(i) + "\n";
			tmp+=getSwatchAsData(i);
		}
		
		return tmp;
	}
	
	/**
	 * Parses colors for dB
	 * @return String Parsed palette string for dB
	 */
	public static String parseColorsToString()
	{
		String c = new String();
		if (size() < 1)
			c = "000000";
		for (int i=0; i < size(); i++)
		{
			c += colorPalette.get(i).toHex();
		}
		
		return c;
	}
	
	/**
	 * Called to perform constraints based on the user's harmony mode
	 */
	public static void updateConstraints()
	{
		if (isHarmonizing)
		{
			//TODO: Fix this! It is a hack to lock the first swatch when moving
			if (harmonyMode > 0)
				selectedSwatch = 0;
				
			switch (harmonyMode)
			{
				case 1:
					setAnalogous();
					break;
				case 2:
					setShaded();
					break;
				case 3:
					setComplementary();
					break;
				case 4:
					setTriadic();
					break;
				case 5:
					setSplitComplementary();
					break;
				case 6:
					setLeftComplementary();
					break;
				case 7:
					setRightComplementary();
					break;
				case 8:
					setYComplementary();
					break;
				case 9:
					setSquare();
					break;
				case 10:
					setTetradic();
					break;
				case 11:
					setCustom1();
					break;
				case 12:
					setCustom2();
					break;
				case 13:
					setCustom3();
					break;
				case 14:
					setGreys();
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Sets the color relationship to Analogous
	 */
	private static void setAnalogous() 
	{
		
		if (selectedSwatch < size() && selectedSwatch > -1)
		{
			
			NColor keyRGB = colorPalette.get(0);
			
			for (int i=1; i<colorPalette.size(); i++)
			{
				float tempHue;
				NColor temp = colorPalette.get(i);
				if (colorWheelType == false)
					tempHue = keyRGB.hy + 360;
				else
					tempHue = keyRGB.h + 360;
				
				float newHue = (tempHue + 120f * ((float)i) / (float)size()) % 360;
				temp.setByHSV(colorWheelType, newHue, keyRGB.s, keyRGB.v);
				
				colorPalette.set(i, temp);
			}
		}
	}
	
	/**
	 * Sets the color relationship to Shaded
	 */
	private static void setShaded() 
	{
		if (selectedSwatch < size() && selectedSwatch > -1)
		{
			NColor keyRGB = colorPalette.get(0);
			
			for (int i=1; i<colorPalette.size(); i++)
			{
				float tempHue;
				NColor temp = colorPalette.get(i);
				if (colorWheelType == false)
					tempHue = keyRGB.hy + 360;
				else
					tempHue = keyRGB.h + 360;
				
				float newHue1 = (float)((tempHue + (float)360) % 360);
				
				float newVal = (keyRGB.v - keyRGB.v * ((float)i) / (float)size());
				temp.setByHSV(colorWheelType, newHue1, keyRGB.s, newVal);
				
				colorPalette.set(i, temp);
			}
		}
	}
	
	/**
	 * Sets the color relationship to Complementary
	 */
	private static void setComplementary() 
	{
		if (selectedSwatch < size() && selectedSwatch > -1 && size() > 2)
		{	
			NColor keyRGB = colorPalette.get(selectedSwatch);
			NColor subRGB = colorPalette.get((selectedSwatch+1) % colorPalette.size());
			NColor tertRGB = colorPalette.get((selectedSwatch+2) % colorPalette.size());
			
			float oldHue;
			if (colorWheelType == false)
				oldHue = keyRGB.hy;
			else
				oldHue = keyRGB.h;
			
			float newHue1 = (float)((oldHue + (float)540) % 360);
			float newHue2 = (float)((oldHue + (float)540) % 360);
			
			subRGB.setByHSV(colorWheelType, newHue1, keyRGB.s, keyRGB.v);
			tertRGB.setByHSV(colorWheelType, newHue2, keyRGB.s * .5f, keyRGB.v);
			
			colorPalette.set(1, subRGB);
			colorPalette.set(2, tertRGB);
		}		
	}
	
	/**
	 * Sets the color relationship to Triadic
	 */
	private static void setTriadic() 
	{
		// 

		if (selectedSwatch < size() && selectedSwatch > -1 && size() > 2)
		{	
			NColor keyRGB = colorPalette.get(selectedSwatch);
			NColor subRGB = colorPalette.get((selectedSwatch+1) % colorPalette.size());
			NColor tertRGB = colorPalette.get((selectedSwatch+2) % colorPalette.size());
			
			float oldHue;
			if (colorWheelType == false)
				oldHue = keyRGB.hy;
			else
				oldHue = keyRGB.h;
			
			float newHue1 = (float)((oldHue + (float)480) % 360);
			float newHue2 = (float)((oldHue + (float)600) % 360);
			
			subRGB.setByHSV(colorWheelType, newHue1, keyRGB.s, keyRGB.v);
			tertRGB.setByHSV(colorWheelType, newHue2, keyRGB.s, keyRGB.v);
			
			colorPalette.set(1, subRGB);
			colorPalette.set(2, tertRGB);
		}
	}
	
	/**
	 * Sets the color relationship to SplitComplementary
	 */
	private static void setSplitComplementary() 
	{
		// 

		if (selectedSwatch < size() && selectedSwatch > -1 && size() > 2)
		{	
			NColor keyRGB = colorPalette.get(selectedSwatch);
			NColor subRGB = colorPalette.get((selectedSwatch+1) % colorPalette.size());
			NColor tertRGB = colorPalette.get((selectedSwatch+2) % colorPalette.size());
			
			float oldHue;
			if (colorWheelType == false)
				oldHue = keyRGB.hy;
			else
				oldHue = keyRGB.h;
			
			float newHue1 = (float)((oldHue + (float)510) % 360);
			float newHue2 = (float)((oldHue + (float)570) % 360);
			
			subRGB.setByHSV(colorWheelType, newHue1, keyRGB.s, keyRGB.v);
			tertRGB.setByHSV(colorWheelType, newHue2, keyRGB.s, keyRGB.v);
			
			colorPalette.set(1, subRGB);
			colorPalette.set(2, tertRGB);
		}
	}
	
	/**
	 * Sets the color relationship to LeftComplementary
	 */
	private static void setLeftComplementary() 
	{
		// 

		if (selectedSwatch < size() && selectedSwatch > -1 && size() > 2)
		{	
			NColor keyRGB = colorPalette.get(selectedSwatch);
			NColor subRGB = colorPalette.get((selectedSwatch+1) % colorPalette.size());
			NColor tertRGB = colorPalette.get((selectedSwatch+2) % colorPalette.size());
			
			float oldHue;
			if (colorWheelType == false)
				oldHue = keyRGB.hy;
			else
				oldHue = keyRGB.h;
			
			float newHue1 = (float)((oldHue + (float)570) % 360);
			float newHue2 = (float)((oldHue + (float)630) % 360);
			
			subRGB.setByHSV(colorWheelType, newHue1, keyRGB.s, keyRGB.v);
			tertRGB.setByHSV(colorWheelType, newHue2, keyRGB.s, keyRGB.v);
			
			colorPalette.set(1, subRGB);
			colorPalette.set(2, tertRGB);
		}
	}
	
	/**
	 * Sets the color relationship to RightComplementary
	 */
	private static void setRightComplementary() 
	{
		

		if (selectedSwatch < size() && selectedSwatch > -1 && size() > 2)
		{	
			NColor keyRGB = colorPalette.get(selectedSwatch);
			NColor subRGB = colorPalette.get((selectedSwatch+1) % colorPalette.size());
			NColor tertRGB = colorPalette.get((selectedSwatch+2) % colorPalette.size());
			
			float oldHue;
			if (colorWheelType == false)
				oldHue = keyRGB.hy;
			else
				oldHue = keyRGB.h;
			
			float newHue1 = (float)((oldHue + (float)450) % 360);
			float newHue2 = (float)((oldHue + (float)510) % 360);
			
			subRGB.setByHSV(colorWheelType, newHue1, keyRGB.s, keyRGB.v);
			tertRGB.setByHSV(colorWheelType, newHue2, keyRGB.s, keyRGB.v);
			
			colorPalette.set(1, subRGB);
			colorPalette.set(2, tertRGB);
		}
	}
	
	/**
	 * Sets the color relationship to YComplementary
	 */
	private static void setYComplementary() 
	{


		if (selectedSwatch < size() && selectedSwatch > -1 && size() > 3)
		{	
			NColor keyRGB = colorPalette.get(selectedSwatch);
			NColor subRGB = colorPalette.get((selectedSwatch+1) % colorPalette.size());
			NColor tertRGB = colorPalette.get((selectedSwatch+2) % colorPalette.size());
			NColor qurtRGB = colorPalette.get((selectedSwatch+3) % colorPalette.size());
			
			float oldHue;
			if (colorWheelType == false)
				oldHue = keyRGB.hy;
			else
				oldHue = keyRGB.h;
			
			float newHue1 = (float)((oldHue + (float)30) % 360);
			float newHue2 = (float)((oldHue + (float)510) % 360);
			float newHue3 = (float)((oldHue + (float)570) % 360);
			
			subRGB.setByHSV(colorWheelType, newHue1, keyRGB.s, keyRGB.v);
			tertRGB.setByHSV(colorWheelType, newHue2, keyRGB.s, keyRGB.v);
			qurtRGB.setByHSV(colorWheelType, newHue3, keyRGB.s, keyRGB.v);
			
			colorPalette.set(1, subRGB);
			colorPalette.set(2, tertRGB);
			colorPalette.set(3, qurtRGB);
		}
	}
	
	/**
	 * Sets the color relationship to Square
	 */
	private static void setSquare() 
	{
		if (selectedSwatch < size() && selectedSwatch > -1 && size() > 3)
		{	
			NColor keyRGB = colorPalette.get(selectedSwatch);
			NColor subRGB = colorPalette.get((selectedSwatch+1) % colorPalette.size());
			NColor tertRGB = colorPalette.get((selectedSwatch+2) % colorPalette.size());
			NColor qurtRGB = colorPalette.get((selectedSwatch+3) % colorPalette.size());
			
			float oldHue;
			if (colorWheelType == false)
				oldHue = keyRGB.hy;
			else
				oldHue = keyRGB.h;
			
			float newHue1 = (float)((oldHue + (float)450) % 360);
			float newHue2 = (float)((oldHue + (float)540) % 360);
			float newHue3 = (float)((oldHue + (float)630) % 360);
			
			subRGB.setByHSV(colorWheelType, newHue1, keyRGB.s, keyRGB.v);
			tertRGB.setByHSV(colorWheelType, newHue2, keyRGB.s, keyRGB.v);
			qurtRGB.setByHSV(colorWheelType, newHue3, keyRGB.s, keyRGB.v);
			
			colorPalette.set(1, subRGB);
			colorPalette.set(2, tertRGB);
			colorPalette.set(3, qurtRGB);
		}
	}
	
	private static void setTetradic() 
	{
		if (selectedSwatch < size() && selectedSwatch > -1 && size() > 3)
		{	
			NColor keyRGB = colorPalette.get(selectedSwatch);
			NColor subRGB = colorPalette.get((selectedSwatch+1) % colorPalette.size());
			NColor tertRGB = colorPalette.get((selectedSwatch+2) % colorPalette.size());
			NColor qurtRGB = colorPalette.get((selectedSwatch+3) % colorPalette.size());
			
			float oldHue;
			if (colorWheelType == false)
				oldHue = keyRGB.hy;
			else
				oldHue = keyRGB.h;
			
			float newHue1 = (float)((oldHue + (float)420) % 360);
			float newHue2 = (float)((oldHue + (float)540) % 360);
			float newHue3 = (float)((oldHue + (float)600) % 360);
			
			subRGB.setByHSV(colorWheelType, newHue1, keyRGB.s, keyRGB.v);
			tertRGB.setByHSV(colorWheelType, newHue2, keyRGB.s, keyRGB.v);
			qurtRGB.setByHSV(colorWheelType, newHue3, keyRGB.s, keyRGB.v);
			
			colorPalette.set(1, subRGB);
			colorPalette.set(2, tertRGB);
			colorPalette.set(3, qurtRGB);
		}
	}
	
	/**
	 * Sets the color relationship to Custom 1
	 */
	private static void setCustom1()
	{
		if (selectedSwatch < size() && selectedSwatch > -1)
		{
			NColor keyRGB = colorPalette.get(0);
			
			for (int i=1; i<colorPalette.size(); i++)
			{
				float tempHue;
				NColor temp = colorPalette.get(i);
				if (colorWheelType == false)
					tempHue = keyRGB.hy + 360;
				else
					tempHue = keyRGB.h + 360;
				
				float newHue = (tempHue + 120f * ((float)i) / (float)size()) % 360;
				float newVal = (keyRGB.v - keyRGB.v * ((float)i) / (float)size());
				temp.setByHSV(colorWheelType, newHue, keyRGB.s, newVal);
				
				colorPalette.set(i, temp);
			}
		}		
	}

	/**
	 * Sets the color relationship to Custom 2
	 */
	private static void setCustom2()
	{
		if (selectedSwatch < size() && selectedSwatch > -1)
		{
			NColor keyRGB = colorPalette.get(0);
			
			for (int i=1; i<colorPalette.size(); i++)
			{
				float tempHue;
				NColor temp = colorPalette.get(i);
				if (colorWheelType == false)
					tempHue = keyRGB.hy + 360;
				else
					tempHue = keyRGB.h + 360;
				
				float newHue = (tempHue + 120f * ((float)i) / (float)size()) % 360;
				float newSat = (keyRGB.s - keyRGB.s * ((float)i) / (float)size());
				temp.setByHSV(colorWheelType, newHue, newSat, keyRGB.v);
				
				colorPalette.set(i, temp);
			}
		}		
	}
	
	private static void setCustom3()
	{
		if (selectedSwatch < size() && selectedSwatch > -1)
		{
			NColor keyRGB = colorPalette.get(0);
			
			for (int i=1; i<colorPalette.size(); i++)
			{
				float tempHue;
				NColor temp = colorPalette.get(i);
				if (colorWheelType == false)
					tempHue = keyRGB.hy + 360;
				else
					tempHue = keyRGB.h + 360;
				
				float newHue = (tempHue + 240f * ((float)i) / (float)size()) % 360;
				float newSat = (keyRGB.s - keyRGB.s * ((float)i) / (float)size());
				float newVal = 1f - 1f * ((float)i) / (float)size();
				temp.setByHSV(colorWheelType, newHue, newSat, newVal);
				
				colorPalette.set(i, temp);
			}
		}			
	}
	
	private static void setGreys()
	{
		if (selectedSwatch < size() && selectedSwatch > -1)
		{
			NColor keyRGB = colorPalette.get(0);
			
			for (int i=1; i<colorPalette.size(); i++)
			{
				float tempHue;
				NColor temp = colorPalette.get(i);
				if (colorWheelType == false)
					tempHue = keyRGB.hy + 360;
				else
					tempHue = keyRGB.h + 360;
				
				float newHue = tempHue % 360;
				float newVal = 1f * ((float)i) / (float)size();
				//float newSat = (keyRGB.s - keyRGB.s * ((float)i) / (float)size());
				temp.setByHSV(colorWheelType, newHue, 0.0f, newVal);
				
				colorPalette.set(i, temp);
			}
		}			
	}
}

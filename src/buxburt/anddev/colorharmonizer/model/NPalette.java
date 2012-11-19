package buxburt.anddev.colorharmonizer.model;

/**
 * An intermediate class the holds data between the DB and app.
 * 
 * The NPalette class holds all field data that can be used by inventory activies.
 * 
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class NPalette {

	/** The palette name */
	private String paletteName;
	/** The author name */
	private String authorName;
	/** A temporary description */
	private String description;
	/** Size of the palette, for string purposes*/
	private int paletteSize;
	/** The array of integers retrieved */
	private int[] colorPalette;
	/** TODO: Change this to date created */
	private int dateAdded;
	/** TODO: Last time it was updated. */
	private int dateUpdated;
	
	/** The palette ID for future reference */
	private int paletteID;
	
	public NPalette() {
		setDefault();
	}
	
	/**
	 * 
	 * @param id The database record ID
	 * @param paletteName The name of the palette
	 * @param authorName The author of the palette
	 * @param description The description of the palette
	 * @param colorPalette The color palette, in string form
	 * @param paletteSize The size of the palette
	 * @param dateAdded The date added
	 * @param dateUpdated The date updated
	 */
	public NPalette(int id, String paletteName, String authorName, String description, String colorPalette, int paletteSize, int dateAdded, int dateUpdated)
	{
		setDefault();
		this.paletteID = id;
		this.paletteName = paletteName;
		this.authorName = authorName;
		this.description = description;
		this.paletteSize = paletteSize;
		this.dateAdded = dateAdded;
		this.dateUpdated = dateUpdated;
		
		for (int i=0; i<paletteSize; i++)
		{
			String temp = colorPalette.substring((i*6), (i*6)+6);
			//get the #RRGGBB form of the color
			this.colorPalette[i]=Integer.parseInt(temp, 16);
		}
	}
	
	/**
	 * Sets the defaults
	 */
	public void setDefault() {
		paletteName = null;
		authorName = null;
		description = null;
		paletteSize = 16;
		colorPalette = new int[16];
		dateAdded = -1;
		dateUpdated = -1;
	}
	
	/**
	 * Return a color in the palette with the i index
	 * @param i Index to retrieve
	 * @return The color at i, otherwise return black
	 */
	public int getColorSwatch(int i)
	{
		if (i < paletteSize)
			return colorPalette[i];
		else
			return 0x000000;
	}
	
	/**
	 * Return all of the color swatches in string form
	 * @return Used for DB storage
	 */
	public String getAllColorSwatches()
	{
		StringBuilder temp = new StringBuilder();
		
		for (int i = 0; i < paletteSize; i++)
		{
			StringBuilder n = new StringBuilder();
			n.append(Integer.toHexString(colorPalette[i]));
			int k = n.length();
			for (int j=k; j<6; j++)
				n.insert(0, "0");
			temp.append(n);
			//Log.i("PALETTE SWATCH SIZE",String.valueOf(paletteSize));
			//Log.i("PALETTE SWATCHES",temp.toString());
		}
		
		return temp.toString();
	}
	
	//below are a series of setters and getters
	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPaletteSize() {
		return paletteSize;
	}

	public void setPaletteSize(int paletteSize) {
		this.paletteSize = paletteSize;
	}

	public int[] getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(int[] colorPalette) {
		this.colorPalette = colorPalette;
	}

	public int getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(int dateAdded) {
		this.dateAdded = dateAdded;
	}

	public int getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(int dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getPaletteName() {
		return paletteName;
	}
	
	public String getPaletteID() {
		return String.valueOf(paletteID);
	}
	
	public void setPaletteName(String string) {
		paletteName = string;
	}
	
	public void setPaletteID(String string) {
		paletteID = Integer.valueOf(string);
	}
	
	public void setPaletteID(int b) {
		paletteID = b;
	}
}

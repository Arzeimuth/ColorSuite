package buxburt.anddev.colorharmonizer.model;

public class NSwatch {

	/***/
	private String info;
	/***/
	private int color;
	
	public NSwatch()
	{
		setDefault();
	}
	
	public NSwatch(int color, String info)
	{
		setDefault();
		this.color = color;
		this.info = info;
	}
	
	public void setDefault()
	{
		info = "No info";
		color = 0;
	}

	public String getInfo() {
		return info;
	}

	public int getColor() {
		return color;
	}

	
}

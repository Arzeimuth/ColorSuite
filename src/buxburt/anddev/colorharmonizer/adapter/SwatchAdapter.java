package buxburt.anddev.colorharmonizer.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.model.NSwatch;

/**
 * An item adapter for swatches in a ListView
 * 
 * Acts as an array class that carries a holder class as well
 * @since 1.2
 * @author Brandon Burton
 * Jul 18, 2012
 *
 */
public class SwatchAdapter extends ArrayAdapter<NSwatch> 
{
	/** The context it belongs to */
	Context context;
	/** The layout it belongs to */
	int layoutResourceId;
	/** The list of swatches */
	private ArrayList<NSwatch> swatches;

	public SwatchAdapter(Context context, int textViewResourceId,
			ArrayList<NSwatch> swatches) {
		super(context, textViewResourceId, swatches);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.swatches = swatches;
		this.layoutResourceId = textViewResourceId;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		
		NSwatchHolder holder = null;
		
		if (row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.export_menu_listitem, null);
			
			holder = new NSwatchHolder();
			
			holder.labelColor = (TextView)row.findViewById(R.id.labelSwatchColorExport);
			holder.labelInfo = (TextView)row.findViewById(R.id.labelInfoLine1);
		
			row.setTag(holder);
		}
		
		else
		{
			holder = (NSwatchHolder)row.getTag();
		}
		
		NSwatch o = swatches.get(position);
		
		holder.labelColor.setBackgroundColor(o.getColor());
		holder.labelInfo.setText(o.getInfo());
		
		return row;
	}
	
	/**
     * A static class that holds references to each respective view.
     * 
     * Consists of a title, author, primary color, amount, and the five color labels
     * @since 1.2
     * @author Brandon Burton
     * Jul 18, 2012
     *
     */
	static class NSwatchHolder
	{
		TextView labelColor;
		TextView labelInfo;
	}
}

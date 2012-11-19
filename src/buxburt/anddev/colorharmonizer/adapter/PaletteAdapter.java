package buxburt.anddev.colorharmonizer.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.model.NPalette;

/**
 * An item adapter for the ListView
 * 
 * Acts as an array class that carries a holder class as well
 * @since 1.1
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class PaletteAdapter extends ArrayAdapter<NPalette> 
{
	/** The context it belongs to */
	Context context;
	/** The layout it belongs to */
	int layoutResourceId;
	/** The list of palettes */
	private ArrayList<NPalette> palettes;
	
	/**
	 * 
	 * @param context Context it belongs to
	 * @param textViewResourceId The view it belongs to
	 * @param palettes All the palettes saved up
	 */
	public PaletteAdapter(Context context, int textViewResourceId, ArrayList<NPalette> palettes) {
        super(context, textViewResourceId, palettes);
        this.context = context;
        this.palettes = palettes;
        this.layoutResourceId = textViewResourceId;
	}
	
	/**
	 * Adapter gets view created and assigns it to it's parent
	 * @param position The position it belongs to
	 * @param convertView The view it belongs to, this time, it's the ListView adapter
	 * @param parent The ViewGroup this view belongs to 
	 */
    public View getView(int position, View convertView, ViewGroup parent) 
	{
    	//the physical view
        View row = convertView;
        //used to hold the view data
        NPaletteHolder holder = null;
        
        if (row == null) 
        {
            //LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //v = vi.inflate(R.layout.row, null);
        	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        	row = inflater.inflate(R.layout.row, null);
        	
        	//create a new holder that maintains all view information
        	holder = new NPaletteHolder();
            holder.labelTitle = (TextView)row.findViewById(R.id.labelTitle);
            holder.labelAuthor = (TextView)row.findViewById(R.id.labelAuthor);
            holder.labelPrimaryColor = (TextView)row.findViewById(R.id.labelPrimaryColor);
            holder.labelAmount = (TextView)row.findViewById(R.id.labelAmount);
            
            //these are for the 5 colors that will be displayed
            holder.labelSwatch1 = (TextView) row.findViewById(R.id.labelColorPreview1);
            holder.labelSwatch2 = (TextView) row.findViewById(R.id.labelColorPreview2);
            holder.labelSwatch3 = (TextView) row.findViewById(R.id.labelColorPreview3);
            holder.labelSwatch4 = (TextView) row.findViewById(R.id.labelColorPreview4);
            holder.labelSwatch5 = (TextView) row.findViewById(R.id.labelColorPreview5);
            
            //the built information is now this combined layout
            row.setTag(holder);
        }
        
        else
        {
        	//send the previous layout
        	holder = (NPaletteHolder)row.getTag();
        }
        
        //performance of the actual operations
        //get the Palette Information
        NPalette o = palettes.get(position);
        
        holder.labelTitle.setText(o.getPaletteName());
        holder.labelAuthor.setText(o.getAuthorName());
        holder.labelAmount.setText(String.valueOf(o.getPaletteSize())); 

        //use this to hold reference to each value
        int i;
        
        //retrieve the first five colors, otherwise make it black
        i = o.getColorSwatch(0);
    	holder.labelSwatch1.setBackgroundColor(Color.rgb(Color.red(i), Color.green(i), Color.blue(i)));
    	holder.labelPrimaryColor.setBackgroundColor(Color.rgb(Color.red(i), Color.green(i), Color.blue(i)));
    	
    	i = o.getColorSwatch(1);
    	holder.labelSwatch2.setBackgroundColor(Color.rgb(Color.red(i), Color.green(i), Color.blue(i)));
    	
    	i = o.getColorSwatch(2);
    	holder.labelSwatch3.setBackgroundColor(Color.rgb(Color.red(i), Color.green(i), Color.blue(i)));
    	
    	i = o.getColorSwatch(3);
    	holder.labelSwatch4.setBackgroundColor(Color.rgb(Color.red(i), Color.green(i), Color.blue(i)));
    	
    	i = o.getColorSwatch(4);
    	holder.labelSwatch5.setBackgroundColor(Color.rgb(Color.red(i), Color.green(i), Color.blue(i)));

        return row;
 	}

    /**
     * A static class that holds references to each respective view.
     * 
     * Consists of a title, author, primary color, amount, and the five color labels
     * @since 1.1
     * @author Brandon Burton
     * Jul 2, 2012
     *
     */
	static class NPaletteHolder
	{
		TextView labelTitle;
		TextView labelAuthor;
		TextView labelPrimaryColor;
		TextView labelAmount;
		
		TextView labelSwatch1;
		TextView labelSwatch2;
		TextView labelSwatch3;
		TextView labelSwatch4;
		TextView labelSwatch5;
	}
}
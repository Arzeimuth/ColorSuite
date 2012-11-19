package buxburt.anddev.colorharmonizer.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import buxburt.anddev.colorharmonizer.R;
import buxburt.anddev.colorharmonizer.model.ColorRelationship;

/**
 * TODO: Custom view class for ColorRelationship selector, should adapt for dialog
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class ColorRelationshipAdapter extends ArrayAdapter<ColorRelationship>
{
	Context context;
	int layoutResourceId;
	//private ArrayList<ColorRelationship> color_harmony;
	
	public ColorRelationshipAdapter(Context context, int textViewResourceId,
			ArrayList<ColorRelationship> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.layoutResourceId = textViewResourceId;
		//this.color_harmony = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		ColorRelationshipHolder holder = null;
		
		if (row == null)
		{
			
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.row_harmony, null);			
			holder = new ColorRelationshipHolder();
			
			holder.id = position;
			holder.image = (ImageView)row.findViewById(R.id.labelRelationIcon);
			holder.minimumQuantity = (TextView)row.findViewById(R.id.labelSwatchCount);
			holder.name = (TextView)row.findViewById(R.id.labelRelationText);
		}
		
		else
		{
			holder = (ColorRelationshipHolder)row.getTag();
		}
	
		return row;
	}
	
	
	static class ColorRelationshipHolder
	{
		int id;
		ImageView image;
		TextView name;
		TextView minimumQuantity;
	}
}

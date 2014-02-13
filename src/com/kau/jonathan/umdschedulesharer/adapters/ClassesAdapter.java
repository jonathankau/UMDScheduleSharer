package com.kau.jonathan.umdschedulesharer.adapters;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kau.jonathan.umdschedulesharer.R;
import com.kau.jonathan.umdschedulesharer.R.id;
import com.kau.jonathan.umdschedulesharer.R.layout;
import com.kau.jonathan.umdschedulesharer.models.ClassDataHolder;
import com.kau.jonathan.umdschedulesharer.views.NonScrollableGridView;

public class ClassesAdapter extends BaseAdapter {
	private final LayoutInflater inflater;
	Context context;
	LinkedList<ClassDataHolder> parsed;
	Typeface face;
	Typeface lightface;
	
	public ClassesAdapter(Context context, LinkedList<ClassDataHolder> parsed) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.parsed = parsed;


		// Set typeface of text elements in page
		face=Typeface.createFromAsset(context.getAssets(),
				"fonts/Lato-Reg.ttf");
		lightface=Typeface.createFromAsset(context.getAssets(),
				"fonts/Lato-Lig.ttf");
	}


	@Override
	public boolean isEnabled(int position) {
		return false; // false to disable click
	}
	
	@Override
	public int getCount() {
		return parsed.size();
	}

	@Override
	public ClassDataHolder getItem(int position) {
		return parsed.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.class_list_item, parent, false);
			holder = new ViewHolder();
			holder.class_name = (TextView) view.findViewById(R.id.class_name);
			holder.section_name = (TextView) view.findViewById(R.id.section_name);
			holder.class_name.setTypeface(lightface);
			holder.section_name.setTypeface(lightface);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		ClassDataHolder cdh = getItem(position);
		
		holder.class_name.setText(cdh.getName());
		holder.section_name.setText(cdh.getSection());//"Sec. " + 
		
		
		// Set NonScrollableGridView Adapter
		NonScrollableGridView grid = (NonScrollableGridView) view.findViewById(R.id.class_friends_grid);
		FriendGridAdapter adapt = new FriendGridAdapter(context, cdh.getFriends());
		grid.setAdapter(adapt);
		
		return view;
	}

	static class ViewHolder {
		TextView class_name;
		TextView section_name;
	}

}
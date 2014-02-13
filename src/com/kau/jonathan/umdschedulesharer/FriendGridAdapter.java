package com.kau.jonathan.umdschedulesharer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import com.kau.jonathan.umdschedulesharer.Models.ClassDataHolder.FriendSectionData;
import com.kau.jonathan.umdschedulesharer.Models.FriendDataHolder;
import com.kau.jonathan.umdschedulesharer.PicassoSampleAdapter.ViewHolder;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendGridAdapter extends BaseAdapter {
	private final LayoutInflater inflater;
	LinkedList<FriendSectionData> friends;
	Context context;
	Typeface face;
	Typeface lightface;

	public FriendGridAdapter(Context context, LinkedList<FriendSectionData> friends) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.friends = friends;

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
		// TODO Auto-generated method stub
		return friends.size();
	}

	@Override
	public FriendSectionData getItem(int position) {
		// TODO Auto-generated method stub
		return friends.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.inner_class_friend_item, parent, false);
			holder = new ViewHolder();

			holder.image = (ImageView) view.findViewById(R.id.class_friend_pic);
			holder.name = (TextView) view.findViewById(R.id.friend_class_name);
			holder.section = (TextView) view.findViewById(R.id.friend_class_section);

			holder.name.setTypeface(face);
			holder.section.setTypeface(face);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}		

		FriendSectionData fsd = getItem(position);

		// Build textviews
		holder.name.setText(fsd.getName());
		holder.section.setText(fsd.getSection());//"Sec. " + 

		// Set image
		// Trigger the download of the URL asynchronously into the image view.
		String imageUrl= "http://graph.facebook.com/" + fsd.getFacebookId() + "/picture?type=square";

		Picasso.with(context)
		.load(imageUrl)
		.placeholder(R.drawable.fb_default)
		.into(holder.image);

		return view;
	}

	static class ViewHolder {
		ImageView image;
		TextView name;
		TextView section;
	}

}

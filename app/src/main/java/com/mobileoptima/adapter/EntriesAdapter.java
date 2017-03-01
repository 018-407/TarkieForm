package com.mobileoptima.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.object.EntryObj;
import com.mobileoptima.tarkieform.R;

import java.util.ArrayList;

public class EntriesAdapter extends ArrayAdapter<EntryObj> {

	private ArrayList<EntryObj> items;
	private LayoutInflater inflater;
	private int red, green;

	public EntriesAdapter(Context context, ArrayList<EntryObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		Resources res = context.getResources();
		this.red = res.getColor(R.color.red);
		this.green = res.getColor(R.color.green);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final EntryObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.entries_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvNameEntries = (CodePanLabel) view.findViewById(R.id.tvNameEntries);
				holder.tvStatusEntries = (CodePanLabel) view.findViewById(R.id.tvStatusEntries);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.form.name != null) {
				holder.tvNameEntries.setText(obj.form.name);
			}
			if(obj.isSubmit) {
				holder.tvStatusEntries.setTextColor(green);
				holder.tvStatusEntries.setText(R.string.submitted);
			}
			else {
				holder.tvStatusEntries.setTextColor(red);
				holder.tvStatusEntries.setText(R.string.draft);
			}
		}
		return view;
	}

	private class ViewHolder {
		public CodePanLabel tvNameEntries;
		public CodePanLabel tvStatusEntries;
	}
}

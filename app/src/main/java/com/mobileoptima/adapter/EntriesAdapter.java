package com.mobileoptima.adapter;

import android.content.Context;
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

	public EntriesAdapter(Context context, ArrayList<EntryObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
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
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.form.name != null) {
				holder.tvNameEntries.setText(obj.form.name);
			}
		}
		return view;
	}

	private class ViewHolder {
		public CodePanLabel tvNameEntries;
	}
}

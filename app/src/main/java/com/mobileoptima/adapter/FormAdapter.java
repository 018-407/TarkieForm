package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.object.FormObj;
import com.mobileoptima.tarkieform.R;

import java.util.ArrayList;

public class FormAdapter extends ArrayAdapter<FormObj> {

	private ArrayList<FormObj> items;
	private LayoutInflater inflater;

	public FormAdapter(Context context, ArrayList<FormObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final FormObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.form_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvForm = (CodePanLabel) view.findViewById(R.id.tvForm);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.name != null) {
				holder.tvForm.setText(obj.name);
			}
		}
		return view;
	}

	private class ViewHolder {
		public CodePanLabel tvForm;
	}
}

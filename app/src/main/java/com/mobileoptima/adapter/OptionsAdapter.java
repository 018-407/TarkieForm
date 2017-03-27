package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.object.ChoiceObj;
import com.mobileoptima.tarkieform.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

public class OptionsAdapter extends ArrayAdapter<ChoiceObj> {

	private DisplayImageOptions options;
	private ArrayList<ChoiceObj> items;
	private LayoutInflater inflater;

	public OptionsAdapter(Context context, ArrayList<ChoiceObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final ChoiceObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.options_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvItemOptions = (CodePanLabel) view.findViewById(R.id.tvItemOptions);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.name != null) {
				holder.tvItemOptions.setText(obj.name);
			}
			if(items.indexOf(obj) < items.size() - 1) {
				holder.tvItemOptions.setBackgroundResource(R.drawable.state_rect_trans_dark);
			}
			else {
				holder.tvItemOptions.setBackgroundResource(R.drawable.state_rect_trans_rad_five_bot);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvItemOptions;
	}
}

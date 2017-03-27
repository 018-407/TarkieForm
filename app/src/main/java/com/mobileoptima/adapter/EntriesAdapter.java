package com.mobileoptima.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.object.EntryObj;
import com.mobileoptima.tarkieform.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class EntriesAdapter extends ArrayAdapter<EntryObj> {

	private DisplayImageOptions options;
	private ArrayList<EntryObj> items;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private int red, green;

	public EntriesAdapter(Context context, ArrayList<EntryObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		Resources res = context.getResources();
		this.red = res.getColor(R.color.red);
		this.green = res.getColor(R.color.green);
		this.imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.gray_qua)
				.showImageForEmptyUri(R.color.gray_qua)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
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
				holder.tvDateEntries = (CodePanLabel) view.findViewById(R.id.tvDateEntries);
				holder.tvStatusEntries = (CodePanLabel) view.findViewById(R.id.tvStatusEntries);
				holder.ivLogoEntries = (ImageView) view.findViewById(R.id.ivLogoEntries);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.form.name != null) {
				holder.tvNameEntries.setText(obj.form.name);
			}
			if(obj.form.logoUrl != null) {
				imageLoader.displayImage(obj.form.logoUrl, holder.ivLogoEntries, options);
			}
			if(obj.isSubmit) {
				holder.tvStatusEntries.setTextColor(green);
				holder.tvStatusEntries.setText(R.string.submitted);
			}
			else {
				holder.tvStatusEntries.setTextColor(red);
				holder.tvStatusEntries.setText(R.string.draft);
			}
			if(obj.dDate != null) {
				String date = CodePanUtils.getCalendarDate(obj.dDate, true, false);
				holder.tvDateEntries.setText(date);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvNameEntries;
		private CodePanLabel tvStatusEntries;
		private CodePanLabel tvDateEntries;
		private ImageView ivLogoEntries;
	}
}

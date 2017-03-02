package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.object.FormObj;
import com.mobileoptima.tarkieform.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class FormAdapter extends ArrayAdapter<FormObj> {

	private DisplayImageOptions options;
	private ArrayList<FormObj> items;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public FormAdapter(Context context, ArrayList<FormObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
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
		final FormObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.form_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvNameForm = (CodePanLabel) view.findViewById(R.id.tvNameForm);
				holder.ivLogoForm = (ImageView) view.findViewById(R.id.ivLogoForm);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.name != null) {
				holder.tvNameForm.setText(obj.name);
			}
			if(obj.logoUrl != null) {
				imageLoader.displayImage(obj.logoUrl, holder.ivLogoForm, options);
			}
		}
		return view;
	}

	private class ViewHolder {
		public CodePanLabel tvNameForm;
		public ImageView ivLogoForm;
	}
}

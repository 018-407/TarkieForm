package com.mobileoptima.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.SquareImageView;
import com.mobileoptima.constant.App;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.tarkieform.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class PhotosAdapter extends ArrayAdapter<ImageObj> {

	private DisplayImageOptions options;
	private ArrayList<ImageObj> items;
	private ImageLoader imageLoader;
	private LayoutInflater inflater;
	private Context context;

	public PhotosAdapter(Context context, ArrayList<ImageObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.items = items;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.showImageOnLoading(R.color.gray_den);
		builder.showImageForEmptyUri(R.color.gray_den);
		builder.cacheInMemory(true);
		builder.cacheOnDisk(true);
		this.imageLoader = ImageLoader.getInstance();
		this.imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.options = builder.build();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final ImageObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.photos_grid_item, parent, false);
				holder = new ViewHolder();
				holder.ivPhotosItem = (SquareImageView) view.findViewById(R.id.ivPhotosItem);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.bitmap != null) {
				holder.ivPhotosItem.setImageBitmap(obj.bitmap);
			}
			else {
				holder.ivPhotosItem.setImageBitmap(null);
				if(obj.fileName != null) {
					loadImage(holder.ivPhotosItem, obj.fileName, position);
				}
			}
		}
		return view;
	}

	public void loadImage(final SquareImageView ivPhotosItem, final String fileName, final int position) {
		String uri = "file://" + context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		imageLoader.displayImage(uri, ivPhotosItem, options, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				items.get(position).bitmap = loadedImage;
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}


	private class ViewHolder {
		private SquareImageView ivPhotosItem;
	}
}

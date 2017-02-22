package com.mobileoptima.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnSingleTapCallback;
import com.codepan.widget.TouchImageView;
import com.mobileoptima.constant.App;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.tarkieform.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class ImagePreviewAdapter extends PagerAdapter {

	private OnSingleTapCallback singleTapCallback;
	private ArrayList<ImageObj> imageList;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private LayoutInflater inflater;
	private Context context;

	public ImagePreviewAdapter(Context context, ArrayList<ImageObj> imageList) {
		this.context = context;
		this.imageList = imageList;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return imageList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		View imageLayout = inflater.inflate(R.layout.image_preview_item, view, false);
		assert imageLayout != null;
		final TouchImageView ivImagePreview = (TouchImageView) imageLayout.findViewById(R.id.ivImagePreview);
		ivImagePreview.setOnSingleTapCallback(singleTapCallback);
		String fileName = imageList.get(position).fileName;
		if(fileName != null) {
			String uri = "file://" + context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + fileName;
			imageLoader.displayImage(uri, ivImagePreview, options);
		}
		view.addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	public void setOnSingleTapCallback(OnSingleTapCallback singleTapCallback) {
		this.singleTapCallback = singleTapCallback;
	}
}

package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.ImagePreviewAdapter;
import com.mobileoptima.callback.Interface.OnDeletePhotoCallback;
import com.mobileoptima.core.TarkieFormLib;
import com.mobileoptima.object.ImageObj;

import java.util.ArrayList;

public class ImagePreviewFragment extends Fragment implements OnClickListener {

	private CodePanButton btnBackImagePreview, btnDeleteImagePreview;
	private OnDeletePhotoCallback deletePhotoCallback;
	private OnFragmentCallback fragmentCallback;
	private FrameLayout flDeleteImagePreview;
	private FragmentTransaction transaction;
	private ArrayList<ImageObj> imageList;
	private CodePanLabel tvImagePreview;
	private ImagePreviewAdapter adapter;
	private ViewPager vpImagePreview;
	private boolean isDeletable = true;
	private SQLiteAdapter db;
	private int position;

	@Override
	public void onStart() {
		super.onStart();
		setOnBackStack(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		setOnBackStack(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.image_preview_layout, container, false);
		tvImagePreview = (CodePanLabel) view.findViewById(R.id.tvImagePreview);
		btnBackImagePreview = (CodePanButton) view.findViewById(R.id.btnBackImagePreview);
		btnDeleteImagePreview = (CodePanButton) view.findViewById(R.id.btnDeleteImagePreview);
		flDeleteImagePreview = (FrameLayout) view.findViewById(R.id.flDeleteImagePreview);
		vpImagePreview = (ViewPager) view.findViewById(R.id.vpImagePreview);
		btnDeleteImagePreview.setOnClickListener(this);
		btnBackImagePreview.setOnClickListener(this);
		adapter = new ImagePreviewAdapter(getActivity(), imageList);
		vpImagePreview.setAdapter(adapter);
		vpImagePreview.setCurrentItem(position);
		vpImagePreview.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				ImagePreviewFragment.this.position = position;
				ImageObj obj = imageList.get(position);
				tvImagePreview.setText(obj.fileName);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		ImageObj obj = imageList.get(position);
		tvImagePreview.setText(obj.fileName);
		if(!isDeletable) {
			flDeleteImagePreview.setVisibility(View.INVISIBLE);
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackImagePreview:
				getActivity().getSupportFragmentManager().popBackStack();
				break;
			case R.id.btnDeleteImagePreview:
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle("Delete Photo");
				alert.setDialogMessage("Are you sure you want to delete photo?");
				alert.setPositiveButton("Yes", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						alert.getDialogActivity().getSupportFragmentManager().popBackStack();
						ImageObj obj = imageList.get(position);
						boolean result = TarkieFormLib.deletePhoto(getActivity(), db, obj);
						if(result) {
							if(deletePhotoCallback != null) {
								deletePhotoCallback.onDeletePhoto(position);
							}
							position = position == 0 ? position : position - 1;
							if(imageList.size() > 0) {
								adapter = new ImagePreviewAdapter(getActivity(), imageList);
								vpImagePreview.setAdapter(adapter);
								vpImagePreview.setCurrentItem(position);
								ImageObj current = imageList.get(position);
								tvImagePreview.setText(current.fileName);
							}
							else {
								getActivity().getSupportFragmentManager().popBackStack();
							}
						}
					}
				});
				alert.setNegativeButton("No", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					}
				});
				transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
						R.anim.fade_in, R.anim.fade_out);
				transaction.add(R.id.rlMain, alert);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
		}
	}

	public void setImageList(ArrayList<ImageObj> imageList, int position) {
		this.imageList = imageList;
		this.position = position;
	}

	public void setOnDeletePhotoCallback(OnDeletePhotoCallback deletePhotoCallback) {
		this.deletePhotoCallback = deletePhotoCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setIsDeletable(boolean isDeletable) {
		this.isDeletable = isDeletable;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}

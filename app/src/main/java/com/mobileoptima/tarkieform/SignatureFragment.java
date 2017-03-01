package com.mobileoptima.tarkieform;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.SignatureView;
import com.mobileoptima.callback.Interface.OnClearCallback;
import com.mobileoptima.callback.Interface.OnSignCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.core.TarkieFormLib;
import com.mobileoptima.object.ImageObj;

public class SignatureFragment extends Fragment implements View.OnClickListener {

	private CodePanButton btnCancelSignature, btnSaveSignature;
	private OnFragmentCallback fragmentCallback;
	private CodePanLabel tvTitleSignature;
	private OnClearCallback clearCallback;
	private OnSignCallback signCallback;
	private SignatureView svSignature;
	private ImageView ivSignature;
	private SQLiteAdapter db;
	private String photoID;
	private String title;

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
		View view = inflater.inflate(R.layout.signature_layout, container, false);
		btnCancelSignature = (CodePanButton) view.findViewById(R.id.btnCancelSignature);
		btnSaveSignature = (CodePanButton) view.findViewById(R.id.btnSaveSignature);
		tvTitleSignature = (CodePanLabel) view.findViewById(R.id.tvTitleSignature);
		svSignature = (SignatureView) view.findViewById(R.id.svSignature);
		ivSignature = (ImageView) view.findViewById(R.id.ivSignature);
		btnCancelSignature.setOnClickListener(this);
		btnSaveSignature.setOnClickListener(this);
		tvTitleSignature.setText(title);
		if(photoID != null && !photoID.isEmpty()) {
			String fileName = TarkieFormLib.getFileName(db, photoID);
			Bitmap bitmap = CodePanUtils.getBitmapImage(getActivity(), App.FOLDER, fileName);
			ivSignature.setImageBitmap(bitmap);
			ivSignature.setVisibility(View.VISIBLE);
			svSignature.setVisibility(View.GONE);
			btnSaveSignature.setText(R.string.clear);
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnCancelSignature:
				getActivity().getSupportFragmentManager().popBackStack();
				break;
			case R.id.btnSaveSignature:
				if(photoID != null) {
					btnSaveSignature.setText(R.string.save);
					boolean result = TarkieFormLib.deletePhoto(getActivity(), db, photoID);
					if(result) {
						ivSignature.setVisibility(View.GONE);
						svSignature.setVisibility(View.VISIBLE);
						if(clearCallback != null) {
							clearCallback.onClear();
						}
						photoID = null;
					}
				}
				else {
					String fileName = System.currentTimeMillis() + ".png";
					String path = getActivity().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath();
					int width = svSignature.getWidth();
					int height = svSignature.getHeight();
					boolean result = svSignature.exportFile(path, fileName, width, height);
					if(result && signCallback != null) {
						ImageObj image = new ImageObj();
						image.fileName = fileName;
						image.dDate = CodePanUtils.getDate();
						image.dTime = CodePanUtils.getTime();
						image.isSignature = true;
						image.ID = TarkieFormLib.savePhoto(db, image);
						getActivity().getSupportFragmentManager().popBackStack();
						signCallback.onSign(image);
					}
				}
				break;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPhotoID(String photoID) {
		this.photoID = photoID;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnSignCallback(OnSignCallback signCallback) {
		this.signCallback = signCallback;
	}

	public void setOnClearCallback(OnClearCallback clearCallback) {
		this.clearCallback = clearCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}

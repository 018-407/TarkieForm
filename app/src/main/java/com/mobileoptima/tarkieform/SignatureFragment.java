package com.mobileoptima.tarkieform;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.SignatureView;
import com.mobileoptima.callback.Interface.OnSignCallback;
import com.mobileoptima.constant.App;

public class SignatureFragment extends Fragment implements View.OnClickListener {

	private CodePanButton btnCancelSignature, btnSaveSignature;
	private OnFragmentCallback fragmentCallback;
	private CodePanLabel tvTitleSignature;
	private SignatureView svAddSignature;
	private OnSignCallback signCallback;
	private SQLiteAdapter db;
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
		svAddSignature = (SignatureView) view.findViewById(R.id.svAddSignature);
		tvTitleSignature = (CodePanLabel) view.findViewById(R.id.tvTitleSignature);
		btnCancelSignature.setOnClickListener(this);
		btnSaveSignature.setOnClickListener(this);
		tvTitleSignature.setText(title);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnCancelSignature:
				getActivity().getSupportFragmentManager().popBackStack();
				break;
			case R.id.btnSaveSignature:
				String fileName = System.currentTimeMillis() + ".png";
				String path = getActivity().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath();
				int width = svAddSignature.getWidth();
				int height = svAddSignature.getHeight();
				boolean result = svAddSignature.exportFile(path, fileName, width, height);
				if(result && signCallback != null) {
					getActivity().getSupportFragmentManager().popBackStack();
					signCallback.onSign(fileName);
				}
				break;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnSignCallback(OnSignCallback signCallback) {
		this.signCallback = signCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}

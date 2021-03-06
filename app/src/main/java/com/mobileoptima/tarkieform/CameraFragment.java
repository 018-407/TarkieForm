package com.mobileoptima.tarkieform;

import android.animation.LayoutTransition;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnCameraErrorCallback;
import com.codepan.callback.Interface.OnCaptureCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.camera.CameraSurfaceView;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.FocusIndicatorView;
import com.mobileoptima.callback.Interface.OnCameraDoneCallback;
import com.mobileoptima.callback.Interface.OnDeletePhotoCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.object.ImageObj;

import java.util.ArrayList;

public class CameraFragment extends Fragment implements OnClickListener, OnCaptureCallback,
		OnBackPressedCallback, OnFragmentCallback, OnDeletePhotoCallback,
		OnCameraErrorCallback {

	private final String flashMode = Camera.Parameters.FLASH_MODE_OFF;
	private final long TRANS_DELAY = 300;
	private final long FADE_DELAY = 750;

	private CodePanButton btnBackCamera, btnOptionsCamera, btnCaptureCamera,
			btnDoneCamera, btnSwitchCamera, btnClearCamera;
	private int maxWidth, maxHeight, cameraSelection, position;
	private RelativeLayout rlPhotoGridCamera, rlOptionsCamera;
	private LinearLayout llPhotoGridCamera, llSwitchCamera;
	private HorizontalScrollView hsvPhotoGridCamera;
	private OnBackPressedCallback backPressedCallback;
	private OnCameraDoneCallback cameraDoneCallback;
	private OnOverrideCallback overrideCallback;
	private OnFragmentCallback fragmentCallback;
	private CodePanLabel tvPhotosTakenCamera;
	private FragmentTransaction transaction;
	private ArrayList<ImageObj> imageList;
	private CameraSurfaceView surfaceView;
	private FocusIndicatorView dvCamera;
	private LayoutTransition transition;
	private boolean inOtherFragment;
	private FrameLayout flCamera;
	private SQLiteAdapter db;
	private String fileName;
	private View vCamera;

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
	public void onResume() {
		super.onResume();
		if(surfaceView != null && surfaceView.getCamera() == null) {
			resetCamera(0);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageList = new ArrayList<>();
		maxWidth = CodePanUtils.getMaxWidth(getActivity());
		maxHeight = CodePanUtils.getMaxHeight(getActivity());
		((MainActivity) getActivity()).setOnBackPressedCallback(this);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.camera_layout, container, false);
		rlOptionsCamera = (RelativeLayout) view.findViewById(R.id.rlOptionsCamera);
		rlPhotoGridCamera = (RelativeLayout) view.findViewById(R.id.rlPhotoGridCamera);
		hsvPhotoGridCamera = (HorizontalScrollView) view.findViewById(R.id.hsvPhotoGridCamera);
		llPhotoGridCamera = (LinearLayout) view.findViewById(R.id.llPhotoGridCamera);
		llSwitchCamera = (LinearLayout) view.findViewById(R.id.llSwitchCamera);
		tvPhotosTakenCamera = (CodePanLabel) view.findViewById(R.id.tvPhotosTakenCamera);
		btnDoneCamera = (CodePanButton) view.findViewById(R.id.btnDoneCamera);
		btnSwitchCamera = (CodePanButton) view.findViewById(R.id.btnSwitchCamera);
		btnClearCamera = (CodePanButton) view.findViewById(R.id.btnClearCamera);
		btnBackCamera = (CodePanButton) view.findViewById(R.id.btnBackCamera);
		btnOptionsCamera = (CodePanButton) view.findViewById(R.id.btnOptionsCamera);
		btnCaptureCamera = (CodePanButton) view.findViewById(R.id.btnCaptureCamera);
		dvCamera = (FocusIndicatorView) view.findViewById(R.id.dvCamera);
		flCamera = (FrameLayout) view.findViewById(R.id.flCamera);
		vCamera = view.findViewById(R.id.vCamera);
		btnBackCamera.setOnClickListener(this);
		btnOptionsCamera.setOnClickListener(this);
		btnCaptureCamera.setOnClickListener(this);
		btnDoneCamera.setOnClickListener(this);
		btnSwitchCamera.setOnClickListener(this);
		btnClearCamera.setOnClickListener(this);
		rlOptionsCamera.setOnClickListener(this);
		transition = llPhotoGridCamera.getLayoutTransition();
		transition.addTransitionListener(new LayoutTransition.TransitionListener() {
			@Override
			public void startTransition(LayoutTransition transition, ViewGroup container, View view,
										int transitionType) {
			}

			@Override
			public void endTransition(LayoutTransition transition, ViewGroup container, View view,
									  int transitionType) {
				hsvPhotoGridCamera.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		});
		resetCamera(TRANS_DELAY);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackCamera:
				onBackPressed();
				break;
			case R.id.btnCaptureCamera:
				if(surfaceView != null && !surfaceView.isCaptured()) {
					surfaceView.takePicture();
				}
				break;
			case R.id.btnOptionsCamera:
				if(rlOptionsCamera.getVisibility() == View.GONE) {
					CodePanUtils.fadeIn(rlOptionsCamera);
				}
				else {
					CodePanUtils.fadeOut(rlOptionsCamera);
				}
				break;
			case R.id.btnDoneCamera:
				if(rlOptionsCamera.getVisibility() == View.VISIBLE) {
					CodePanUtils.fadeOut(rlOptionsCamera);
				}
				if(cameraDoneCallback != null) {
					cameraDoneCallback.onCameraDone(imageList);
				}
				getActivity().getSupportFragmentManager().popBackStack();
				break;
			case R.id.btnSwitchCamera:
				if(cameraSelection == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					cameraSelection = Camera.CameraInfo.CAMERA_FACING_BACK;
				}
				else {
					cameraSelection = Camera.CameraInfo.CAMERA_FACING_FRONT;
				}
				resetCamera(0);
				break;
			case R.id.btnClearCamera:
				rlOptionsCamera.performClick();
				if(!imageList.isEmpty()) {
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setOnFragmentCallback(this);
					alert.setDialogTitle("Delete Photos");
					alert.setDialogMessage("Are you sure you want to clear all taken photos?");
					alert.setPositiveButton("Yes", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							alert.getDialogActivity().getSupportFragmentManager().popBackStack();
							clearPhotos(db, imageList);
							if(overrideCallback != null) {
								overrideCallback.onOverride(false);
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
				}
				else {
					CodePanUtils.alertToast(getActivity(), "No photos to be cleared.");
				}
				break;
			case R.id.rlOptionsCamera:
				if(rlOptionsCamera.getVisibility() == View.VISIBLE) {
					CodePanUtils.fadeOut(rlOptionsCamera);
				}
				break;
		}
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
		if(!isOnBackStack) {
			((MainActivity) getActivity()).setOnBackPressedCallback(backPressedCallback);
		}
	}

	@Override
	public void onCapture(String fileName) {
		this.fileName = fileName;
		ImageObj obj = new ImageObj();
		obj.dDate = CodePanUtils.getDate();
		obj.dTime = CodePanUtils.getTime();
		obj.fileName = fileName;
		obj.ID = TarkieLib.savePhoto(db, obj);
		imageList.add(obj);
		if(surfaceView != null && surfaceView.isCaptured()) {
			surfaceView.reset();
		}
		if(overrideCallback != null) {
			overrideCallback.onOverride(true);
		}
		updatePhotoGrid(position);
		position++;
	}

	public void updatePhotoGrid(final int position) {
		View view = getView();
		if(view != null) {
			ViewGroup container = (ViewGroup) view.getParent();
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View child = inflater.inflate(R.layout.photo_grid_item, container, false);
			CodePanButton btnPhotoGrid = (CodePanButton) child.findViewById(R.id.btnPhotoGrid);
			ImageView ivPhotoGrid = (ImageView) child.findViewById(R.id.ivPhotoGrid);
			int size = CodePanUtils.getWidth(child);
			Bitmap bitmap = CodePanUtils.getBitmapThumbnails(getActivity(), App.FOLDER, fileName, size);
			ivPhotoGrid.setImageBitmap(bitmap);
			imageList.get(position).bitmap = bitmap;
			btnPhotoGrid.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(position);
				}
			});
			if(position == 0) {
				llPhotoGridCamera.removeAllViews();
				llPhotoGridCamera.setLayoutTransition(null);
			}
			else {
				llPhotoGridCamera.setLayoutTransition(transition);
			}
			llPhotoGridCamera.addView(child);
			if(rlPhotoGridCamera.getVisibility() == View.GONE) {
				CodePanUtils.expandView(rlPhotoGridCamera, true);
			}
			String taken = String.valueOf(imageList.size());
			tvPhotosTakenCamera.setText(taken);
		}
	}

	@Override
	public void onBackPressed() {
		if(!imageList.isEmpty() && !inOtherFragment) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setOnFragmentCallback(this);
			alert.setDialogTitle("Save Photos");
			alert.setDialogMessage("Do you want to save photos?");
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					getActivity().getSupportFragmentManager().popBackStack();
					if(cameraDoneCallback != null) {
						cameraDoneCallback.onCameraDone(imageList);
					}
					if(overrideCallback != null) {
						overrideCallback.onOverride(false);
					}
				}
			});
			alert.setNegativeButton("No", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!imageList.isEmpty()) {
						clearPhotos(db, imageList);
					}
					if(overrideCallback != null) {
						overrideCallback.onOverride(false);
					}
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					getActivity().getSupportFragmentManager().popBackStack();
				}
			});
			transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, alert);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			getActivity().getSupportFragmentManager().popBackStack();
		}
	}

	@Override
	public void onDeletePhoto(int position) {
		imageList.remove(position);
		llPhotoGridCamera.removeViewAt(position);
		this.position = imageList.size();
		if(imageList.size() == 0) {
			CodePanUtils.collapseView(rlPhotoGridCamera, true);
		}
		else {
			invalidateViews();
		}
		String taken = String.valueOf(imageList.size());
		tvPhotosTakenCamera.setText(taken);
	}

	public void invalidateViews() {
		int count = llPhotoGridCamera.getChildCount();
		for(int i = 0; i < count; i++) {
			final int position = i;
			View view = llPhotoGridCamera.getChildAt(i);
			CodePanButton btnPhotoGrid = (CodePanButton) view.findViewById(R.id.btnPhotoGrid);
			btnPhotoGrid.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(position);
				}
			});
		}
	}

	public void clearPhotos(final SQLiteAdapter db, final ArrayList<ImageObj> deleteList) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					boolean result = TarkieLib.deletePhotos(getActivity(), db, deleteList);
					if(result) {
						imageList.clear();
						clearPhotosHandler.sendMessage(clearPhotosHandler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler clearPhotosHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			CodePanUtils.alertToast(getActivity(), "Photos cleared.");
			llPhotoGridCamera.removeAllViews();
			CodePanUtils.collapseView(rlPhotoGridCamera, true);
			tvPhotosTakenCamera.setText("0");
			position = 0;
			return true;
		}
	});

	public void onPhotoGridItemClick(int position) {
		ImagePreviewFragment imagePreview = new ImagePreviewFragment();
		imagePreview.setImageList(imageList, position);
		imagePreview.setOnDeletePhotoCallback(this);
		imagePreview.setOnFragmentCallback(this);
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, imagePreview);
		transaction.hide(this);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onFragment(boolean status) {
		this.inOtherFragment = status;
		if(!status) {
			((MainActivity) getActivity()).setOnBackPressedCallback(this);
			if(!imageList.isEmpty()) {
				if(overrideCallback != null) {
					overrideCallback.onOverride(true);
				}
			}
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnCameraDoneCallback(OnCameraDoneCallback cameraDoneCallback) {
		this.cameraDoneCallback = cameraDoneCallback;
	}

	public void setOnBackPressedCallback(OnBackPressedCallback backPressedCallback) {
		this.backPressedCallback = backPressedCallback;
	}

	public void resetCamera(long delay) {
		if(vCamera != null) vCamera.setVisibility(View.VISIBLE);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(surfaceView != null) surfaceView.stopCamera();
				surfaceView = new CameraSurfaceView(getActivity(), CameraFragment.this,
						cameraSelection, flashMode, App.FOLDER, maxWidth, maxHeight);
				surfaceView.setOnCaptureCallback(CameraFragment.this);
				surfaceView.setFocusIndicatorView(dvCamera);
				surfaceView.fullScreenToContainer(flCamera);
				if(flCamera.getChildCount() > 1) {
					flCamera.removeViewAt(0);
				}
				flCamera.addView(surfaceView, 0);
				CodePanUtils.fadeOut(vCamera, FADE_DELAY);
				if(surfaceView.getNoOfCamera() == 1) {
					llSwitchCamera.setVisibility(View.GONE);
				}
			}
		}, delay);
	}

	@Override
	public void onCameraError() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle("Camera Failed");
		alert.setDialogMessage("Unable to load camera, please try to restart " +
				"your device and try again.");
		alert.setPositiveButton("Ok", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alert.getDialogActivity().getSupportFragmentManager().popBackStack();
				alert.getDialogActivity().getSupportFragmentManager().popBackStack();
			}
		});
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}

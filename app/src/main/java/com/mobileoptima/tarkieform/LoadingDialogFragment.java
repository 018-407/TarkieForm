package com.mobileoptima.tarkieform;

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

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.ProgressWheel;
import com.mobileoptima.callback.Interface.OnErrorCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Module;
import com.mobileoptima.constant.Module.Action;
import com.mobileoptima.constant.Process;
import com.mobileoptima.core.Rx;
import com.mobileoptima.core.TarkieFormLib;
import com.mobileoptima.core.Tx;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.schema.Tables;

import static com.codepan.callback.Interface.OnRefreshCallback;

public class LoadingDialogFragment extends Fragment implements OnErrorCallback,
		OnBackPressedCallback, OnFragmentCallback {

	private CodePanLabel tvTitleLoadingDialog, tvCountLoadingDialog;
	private boolean result, isDone, isPause, inOtherFragment;
	private String successMsg, failedMsg, error, message;
	private OnFragmentCallback fragmentCallback;
	private OnOverrideCallback overrideCallback;
	private ProgressWheel progressLoadingDialog;
	private OnRefreshCallback refreshCallback;
	private FragmentTransaction transaction;
	private int progress, max;
	private SQLiteAdapter db;
	private Action action;
	private float percent;
	private Bundle bundle;
	private Thread bg;

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
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isPause && isDone) {
			showResult(message);
		}
		isPause = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MainActivity) getActivity()).setOnBackPressedCallback(this);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
		bundle = this.getArguments();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.loading_dialog_layout, container, false);
		progressLoadingDialog = (ProgressWheel) view.findViewById(R.id.progressLoadingDialog);
		tvTitleLoadingDialog = (CodePanLabel) view.findViewById(R.id.tvTitleLoadingDialog);
		tvCountLoadingDialog = (CodePanLabel) view.findViewById(R.id.tvCountLoadingDialog);
		switch(action) {
			case AUTHORIZE_DEVICE:
				setMax(4);
				successMsg = "Authorization successful.";
				failedMsg = "Failed to authorize the device.";
				String title = "Authorizing Device...";
				tvTitleLoadingDialog.setText(title);
				String authorizationCode = bundle.getString(Key.AUTH_CODE);
				String deviceID = CodePanUtils.getDeviceID(db.getContext());
				authorizeDevice(db, authorizationCode, deviceID);
				break;
			case LOGIN:
				setMax(4);
				successMsg = "Login successful.";
				failedMsg = "Failed to login.";
				title = "Validating account...";
				tvTitleLoadingDialog.setText(title);
				String username = bundle.getString(Key.USERNAME);
				String password = bundle.getString(Key.PASSWORD);
				login(db, username, password);
				break;
			case UPDATE_MASTERLIST:
				setMax(4);
				successMsg = "Update master list successful.";
				failedMsg = "Failed to update master list.";
				title = "Updating master list...";
				tvTitleLoadingDialog.setText(title);
				updateMasterlist(db);
				break;
			case SYNC_DATA:
				setMax(4);
				successMsg = "Sync Data successful.";
				failedMsg = "Failed to sync data.";
				title = "Syncing data...";
				tvTitleLoadingDialog.setText(title);
				syncData(db);
				break;
		}
		return view;
	}

	public void authorizeDevice(final SQLiteAdapter db, final String authorizationCode, final String deviceID) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.authorizeDevice(db, authorizationCode, deviceID, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getSyncBatchID(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getCompany(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getEmployee(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.AUTHORIZATION);
		bg.start();
	}

	public void login(final SQLiteAdapter db, final String username, final String password) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getEmployee(db, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.login(db, username, password, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getForms(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getFields(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.LOGIN);
		bg.start();
	}

	public void updateMasterlist(final SQLiteAdapter db) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getCompany(db, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getEmployee(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getForms(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getFields(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.UPDATE_MASTERFILE);
		bg.start();
	}

	public void syncData(final SQLiteAdapter db) {

		bg = new Thread(new Runnable(){

			@Override
			public void run(){

				Looper.prepare();

				try{
					result = Rx.getSyncBatchID(db, getErrorCallback());
					if(!result){
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}

					for(ImageObj imageObj : TarkieFormLib.getIDsUpload(db, Tables.TB.PHOTO)){
						if(result){
							result = Tx.uploadPhoto(db, imageObj, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}

					for(String entryID : TarkieFormLib.getIDsSync(db, Tables.TB.ENTRIES)){
						if(result){
							result = Tx.syncEntry(db, entryID, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		bg.setName(Process.SYNC_DATA);
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(result) {
				updateProgress();
				if(progress >= max) {
					message = successMsg;
					isDone = true;
					if(!isPause) {
						showResult(message);
					}
				}
			}
			else {
				isDone = true;
				message = error != null ? error : failedMsg;
				if(!isPause) {
					showResult(message);
				}
			}
			return true;
		}
	});

	public void updateProgress() {
		progress++;
		percent = ((float) progress / (float) max) * 100f;
		String percentage = (int) percent + "%";
		progressLoadingDialog.incrementProgress();
		progressLoadingDialog.setText(percentage);
		String count = progress + "/" + max;
		tvCountLoadingDialog.setText(count);
	}

	public void setMax(int max) {
		this.max = max;
		progress = 0;
		progressLoadingDialog.setmax(max);
		String count = "0/" + max;
		tvCountLoadingDialog.setText(count);
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}

	public void showResult(String message) {
		if(!inOtherFragment) {
			getActivity().getSupportFragmentManager().popBackStack();
			String title = Module.getTitle(action);
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(title);
			alert.setDialogMessage(message);
			alert.setPositiveButton("Okay", new OnClickListener() {
				@Override
				public void onClick(View v) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
				}
			});
			transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.rlMain, alert);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onError(String error, String params, String response, boolean showError) {
		CodePanUtils.setErrorMsg(getActivity(), error, params, response);
		if(showError) {
			this.error = error;
		}
	}

	public OnErrorCallback getErrorCallback() {
		return this;
	}

	@Override
	public void onBackPressed() {
		if(!inOtherFragment) {
			String title = Module.getTitle(action);
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(title);
			alert.setDialogMessage("Are you sure you want to cancel " + title + "?");
			alert.setOnFragmentCallback(this);
			alert.setPositiveButton("Yes", new OnClickListener() {
				@Override
				public void onClick(View v) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
				}
			});
			alert.setNegativeButton("No", new OnClickListener() {
				@Override
				public void onClick(View v) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					if(isDone) {
						alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					}
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
	public void onFragment(boolean status) {
		this.inOtherFragment = status;
	}

	public void stopProcess() {
		if(bg != null && bg.isAlive()) {
			bg.interrupt();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopProcess();
	}
}


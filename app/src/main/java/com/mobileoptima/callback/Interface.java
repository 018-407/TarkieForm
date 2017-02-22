package com.mobileoptima.callback;

import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.object.ChoiceObj;
import com.mobileoptima.object.GpsObj;
import com.mobileoptima.object.ImageObj;

import java.util.ArrayList;

public class Interface {

	public interface OnInitializeCallback {
		void onInitialize(SQLiteAdapter db);
	}

	public interface OnOverrideCallback {
		void onOverride(boolean isOverridden);
	}

	public interface OnOptionSelectedCallback {
		void onOptionSelected(ChoiceObj obj);
	}

	public interface OnErrorCallback{
		void onError(String error, String params, String response, boolean showError);
	}

	public interface OnGpsFixedCallback {
		void onGpsFixed(GpsObj gps);
	}

	public interface OnSignCallback {
		void onSign(String fileName);
	}

	public interface OnCameraDoneCallback{
		void onCameraDone(ArrayList<ImageObj> imageList);
	}

	public interface OnDeletePhotoCallback{
		void onDeletePhoto(int position);
	}
}

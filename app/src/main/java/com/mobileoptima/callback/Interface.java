package com.mobileoptima.callback;

import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.object.ChoiceObj;

public class Interface {

	public interface OnInitializeCallback {
		void onInitialize(SQLiteAdapter db);
	}

	public interface OnOverrideCallback {
		void onOverride(boolean isOverridden);
	}

	public interface OnLoginCallback {
		void onLogin(String empID);
	}

	public interface OnOptionSelectedCallback {
		void onOptionSelected(ChoiceObj obj);
	}

	public interface OnErrorCallback{
		void onError(String error, String params, String response, boolean showError);
	}

	public interface OnGpsFixedCallback {
		void onGpsFixed(double latitude, double longitude);
	}

	public interface OnSignCallback {
		void onSign(String fileName);
	}
}

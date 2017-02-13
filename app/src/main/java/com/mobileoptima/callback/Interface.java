package com.mobileoptima.callback;

import com.codepan.database.SQLiteAdapter;

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
}

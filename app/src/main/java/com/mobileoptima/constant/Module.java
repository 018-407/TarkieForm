package com.mobileoptima.constant;

public class Module {
	public enum Action {
		AUTHORIZE_DEVICE,
		LOGIN,
		UPDATE_MASTERLIST,
		SYNC_DATA
	}

	public static String getTitle(Action action) {
		String title = null;
		switch(action) {
			case AUTHORIZE_DEVICE:
				title = "Authorization";
				break;

			case LOGIN:
				title = "Login";
				break;

			case UPDATE_MASTERLIST:
				title = "Update Masterlist";
				break;

			case SYNC_DATA:
				title = "Sync Data";
				break;
		}
		return title;
	}
}
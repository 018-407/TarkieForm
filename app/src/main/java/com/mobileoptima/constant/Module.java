package com.mobileoptima.constant;

public class Module {
	public enum Action {
		AUTHORIZE_DEVICE,
		LOGIN,
		UPDATE_MASTERLIST
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
		}
		return title;
	}
}
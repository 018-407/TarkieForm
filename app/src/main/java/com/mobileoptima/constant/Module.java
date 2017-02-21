package com.mobileoptima.constant;

public class Module {
	public enum Action {
		AUTHORIZE_DEVICE
	}

	public static String getTitle(Action action) {
		String title = null;
		switch(action) {
			case AUTHORIZE_DEVICE:
				title = "Authorization";
				break;
		}
		return title;
	}
}
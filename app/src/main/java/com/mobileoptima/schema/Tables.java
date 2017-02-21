package com.mobileoptima.schema;

public class Tables {
	public enum TB {
		API_KEY
	}

	public static String create(TB tb) {
		String table = getName(tb);
		String statement = null;
		switch(tb) {
			case API_KEY:
				statement = "CREATE TABLE IF NOT EXISTS " + table + "(" +
						"ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
						", apiKey TEXT" +
						", authorizationCode TEXT" +
						", deviceID TEXT" +
						")";
				break;
		}
		return statement;
	}

	public static String getName(TB tb) {
		String name = null;
		switch(tb) {
			case API_KEY:
				name = "api_key_tb";
				break;
		}
		return name;
	}
}

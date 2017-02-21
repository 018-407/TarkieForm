package com.mobileoptima.schema;

public class Tables {
	public enum TB {
		API_KEY,
		SYNC_BATCH,
		CREDENTIALS,
		PHOTO
	}

	public static String create(TB tb) {
		String table = getName(tb);
		String statement = null;
		switch(tb) {
			case API_KEY:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, apiKey TEXT, " +
						"authorizationCode TEXT, deviceID TEXT)";
				break;
			case SYNC_BATCH:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"syncBatchID TEXT)";
				break;
			case CREDENTIALS:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, dDate TEXT, dTime TEXT, " +
						"empID INTEGER, isLogOut INTEGER DEFAULT 0, isNewUser INTEGER DEFAULT 0 )";
				break;
			case PHOTO:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, dDesc TEXT, " +
						"fileName TEXT, dDate TEXT, dTime TEXT, empID INTEGER, " +
						"isDelete INTEGER DEFAULT 0, isUpload INTEGER DEFAULT 0, " +
						"isActive INTEGER DEFAULT 0, webPhotoID INTEGER, syncBatchID TEXT)";
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
			case SYNC_BATCH:
				name = "sync_batch_tb";
				break;
			case CREDENTIALS:
				name = "credentials_tb";
				break;
			case PHOTO:
				name = "photo_tb";
				break;
		}
		return name;
	}
}

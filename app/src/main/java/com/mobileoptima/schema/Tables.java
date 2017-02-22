package com.mobileoptima.schema;

public class Tables {

	public enum TB {
		API_KEY,
		SYNC_BATCH,
		CREDENTIALS,
		COMPANY,
		EMPLOYEE,
		PHOTO,
		FORMS,
		FIELDS,
		CHOICES,
		ENTRIES
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
			case COMPANY:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT, " +
						"address TEXT, email TEXT, contactNo TEXT, imageUrl TEXT)";
				break;
			case EMPLOYEE:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, employeeNo TEXT, " +
						"firstName TEXT, lastName TEXT, email TEXT, groupID INTEGER, " +
						"isActive INTEGER, imageUrl TEXT)";
				break;
			case PHOTO:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"fileName TEXT, dDate TEXT, dTime TEXT, empID INTEGER, " +
						"isDelete INTEGER DEFAULT 0, isUpload INTEGER DEFAULT 0, " +
						"isActive INTEGER DEFAULT 0, webPhotoID INTEGER, syncBatchID TEXT)";
				break;
			case FORMS:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"name TEXT, description TEXT, dateCreated TEXT, timeCreated TEXT, " +
						"groupID INTEGER)";
				break;
			case FIELDS:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, formID INTEGER, " +
						"name TEXT, description TEXT, type TEXT, orderNo INTEGER, " +
						"isRequired INTEGER)";
				break;
			case CHOICES:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"fieldID INTEGER, code TEXT, name TEXT)";
				break;
			case ENTRIES:
				statement = "CREATE TABLE IF NOT EXISTS " + table +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"formID INTEGER, dDate TEXt, dTime TEXT, isSync INTEGER DEFAULT 0, " +
						"syncBatchID TEXT, webEntryID INTEGER)";
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
			case COMPANY:
				name = "company_tb";
				break;
			case EMPLOYEE:
				name = "employee_tb";
				break;
			case PHOTO:
				name = "photo_tb";
				break;
			case FORMS:
				name = "forms_tb";
				break;
			case FIELDS:
				name = "fields_tb";
				break;
			case CHOICES:
				name = "choices_tb";
				break;
			case ENTRIES:
				name = "entries_tb";
				break;
		}
		return name;
	}
}

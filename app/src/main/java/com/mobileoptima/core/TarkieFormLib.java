package com.mobileoptima.core;

import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.mobileoptima.schema.Tables;

import java.util.ArrayList;

import static com.mobileoptima.schema.Tables.TB;

public class TarkieFormLib {

	public static void createTables(SQLiteAdapter db) {
		db.execQuery(Tables.create(TB.API_KEY));
		db.execQuery(Tables.create(TB.SYNC_BATCH));
		db.execQuery(Tables.create(TB.CREDENTIALS));
	}

	public static void alterTables(SQLiteAdapter db, int oldVersion, int newVersion) {
	}

	public static boolean isAuthorized(SQLiteAdapter db) {
		String table = Tables.getName(TB.API_KEY);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		return db.isRecordExists(query);
	}

	public static boolean isLoggedIn(SQLiteAdapter db) {
		String table = Tables.getName(TB.CREDENTIALS);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1 AND employeeID != 0 AND isLogOut = 0";
		return db.isRecordExists(query);
	}

	public static String getAPIKey(SQLiteAdapter db) {
		String table = Tables.getName(TB.API_KEY);
		String query = "SELECT apiKey FROM " + table + " WHERE ID = 1 AND apiKey != ''";
		return db.getString(query);
	}

	public static boolean updateSyncBatchID(SQLiteAdapter db, String syncBatchID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(TB.SYNC_BATCH);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("syncBatchID", syncBatchID));
		if(db.isRecordExists(query)) {
			binder.update(table, fieldValueList, 1);
		}
		else {
			binder.insert(table, fieldValueList);
		}
		return binder.finish();
	}
}

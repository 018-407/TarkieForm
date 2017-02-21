package com.mobileoptima.core;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.session.Session;

import java.util.ArrayList;

import static com.mobileoptima.schema.Tables.TB;

public class TarkieFormLib {

	public static void createTables(SQLiteAdapter db) {
		db.execQuery(Tables.create(TB.API_KEY));
	}

	public static void alterTables(SQLiteAdapter db, int oldVersion, int newVersion) {
	}

	public static boolean isAuthorized(SQLiteAdapter db) {
		return false;
	}

	public static boolean isLoggedIn(SQLiteAdapter db) {
		return false;
	}

	public static String getDeviceID(Context context) {
		String deviceID;
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
			deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		else {
			deviceID = manager.getDeviceId();
		}
		if(deviceID == null || deviceID.isEmpty()) {
			deviceID = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
		}
		return deviceID;
	}

	public static boolean saveAPIKey(SQLiteAdapter db, String apiKey, String authorizationCode, String deviceID) {
		String table = Tables.getName(TB.API_KEY);
		ArrayList<FieldValue> list = new ArrayList<>();
		list.add(new FieldValue("apiKey", apiKey));
		list.add(new FieldValue("authorizationCode", authorizationCode));
		list.add(new FieldValue("deviceID", deviceID));
		SQLiteBinder binder = new SQLiteBinder(db);
		binder.dropTable(table);
		binder.execute(Tables.create(TB.API_KEY));
		binder.insert(table, list);
		if(binder.finish()) {
			Session.API_KEY = getAPIKey(db);
			return true;
		}
		return false;
	}

	public static String getAPIKey(SQLiteAdapter db) {
		if(!Session.API_KEY.isEmpty()) {
			return Session.API_KEY;
		}
		return db.getString("SELECT apiKey FROM " + Tables.getName(TB.API_KEY) + " WHERE ID = 1 AND apiKey != ''");
	}
}

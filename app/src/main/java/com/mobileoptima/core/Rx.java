package com.mobileoptima.core;

import android.util.Log;

import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnErrorCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.schema.Tables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Rx {
	public static boolean authorizeDevice(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(Tables.TB.API_KEY);
		TarkieFormLib.updateSyncBatchID(db, "1");
		ArrayList<FieldValue> list = new ArrayList<>();
		list.add(new FieldValue("apiKey", "R9J621vJg11W4WeDf3N7J2yK0Zgfjm"));
		list.add(new FieldValue("authorizationCode", "10711251"));
		list.add(new FieldValue("deviceID", "121"));
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		if(!db.isRecordExists(query)) {
			binder.insert(table, list);
		}
		else {
			binder.update(table, list, 1);
		}
		return binder.finish();
	}

	public static boolean authorizeDevice(SQLiteAdapter db, String authorizationCode, String deviceID,
										  OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		String action = "authorize-device";
		String url = App.WEB_URL + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("tablet_id", deviceID);
			paramsObj.put("authorization_code", authorizationCode);
			paramsObj.put("api_key", App.API_KEY);
			paramsObj.put("os_type", App.OS_TYPE);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.getHttpResponse(url, params, 5000);
			Log.e("authorization PARAMS", params);
			Log.e("authorization RESPONSE", response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					int recNo = initObj.getInt("recno");
					if(status.equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
			if(hasData) {
				SQLiteBinder binder = new SQLiteBinder(db);
				String table = Tables.getName(Tables.TB.API_KEY);
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int d = 0; d < dataArray.length(); d++) {
					JSONObject dataObj = dataArray.getJSONObject(d);
					String apiKey = dataObj.getString("request_code");
					String syncBatchID = dataObj.getString("sync_batch_id");
					TarkieFormLib.updateSyncBatchID(db, syncBatchID);
					ArrayList<FieldValue> list = new ArrayList<>();
					list.add(new FieldValue("apiKey", apiKey));
					list.add(new FieldValue("authorizationCode", authorizationCode));
					list.add(new FieldValue("deviceID", deviceID));
					String query = "SELECT ID FROM " + table + " WHERE ID = 1";
					if(!db.isRecordExists(query)) {
						binder.insert(table, list);
					}
					else {
						binder.update(table, list, 1);
					}
				}
				result = binder.finish();
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
			return false;
		}
		return result;
	}

	public static boolean login(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(Tables.TB.CREDENTIALS);
		TarkieFormLib.updateSyncBatchID(db, "2");
		ArrayList<FieldValue> list = new ArrayList<>();
		list.add(new FieldValue("companyID", "R9J621vJg11W4WeDf3N7J2yK0Zgfjm"));
		list.add(new FieldValue("employeeID", "10711251"));
		list.add(new FieldValue("dDate", "2017-02-22"));
		list.add(new FieldValue("dTime", "09:00:00"));
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		if(!db.isRecordExists(query)) {
			binder.insert(table, list);
		}
		else {
			binder.update(table, list, 1);
		}
		return binder.finish();
	}

	public static boolean login(SQLiteAdapter db, String username, String password,
										  OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		String action = "get-employee";
		String url = App.WEB_URL + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("tablet_id", password);
			paramsObj.put("authorization_code", username);
			paramsObj.put("api_key", App.API_KEY);
			paramsObj.put("os_type", App.OS_TYPE);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.getHttpResponse(url, params, 5000);
			Log.e("authorization PARAMS", params);
			Log.e("authorization RESPONSE", response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					int recNo = initObj.getInt("recno");
					if(status.equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
			if(hasData) {
				SQLiteBinder binder = new SQLiteBinder(db);
				String table = Tables.getName(Tables.TB.API_KEY);
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int d = 0; d < dataArray.length(); d++) {
					JSONObject dataObj = dataArray.getJSONObject(d);
					String apiKey = dataObj.getString("request_code");
					String syncBatchID = dataObj.getString("sync_batch_id");
					TarkieFormLib.updateSyncBatchID(db, syncBatchID);
					ArrayList<FieldValue> list = new ArrayList<>();
					list.add(new FieldValue("apiKey", apiKey));
					list.add(new FieldValue("authorizationCode", username));
					list.add(new FieldValue("deviceID", password));
					String query = "SELECT ID FROM " + table + " WHERE ID = 1";
					if(!db.isRecordExists(query)) {
						binder.insert(table, list);
					}
					else {
						binder.update(table, list, 1);
					}
				}
				result = binder.finish();
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
			return false;
		}
		return result;
	}
}
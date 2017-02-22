package com.mobileoptima.core;

import android.util.Log;

import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnErrorCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.session.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Rx {

	public static boolean authorizeDevice(SQLiteAdapter db, String authorizationCode, String deviceID,
										  OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
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
			response = CodePanUtils.getHttpResponse(url, params, TIMEOUT);
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
					String apiKey = dataObj.getString("api_key");
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

	public static boolean getCompany(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-company";
		String url = App.WEB_URL + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieFormLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.getHttpResponse(url, params, TIMEOUT);
			Log.e("getCompany PARAMS", params);
			Log.e("getCompany RESPONSE", response);
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
				String table = Tables.getName(Tables.TB.COMPANY);
				binder.truncate(table);
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int d = 0; d < dataArray.length(); d++) {
					JSONObject dataObj = dataArray.getJSONObject(d);
					String company = CodePanUtils.handleUniCode(dataObj.getString("name"));
					String address = CodePanUtils.handleUniCode(dataObj.getString("address"));
					ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
					fieldValueList.add(new FieldValue("ID", dataObj.getString("company_id")));
					fieldValueList.add(new FieldValue("name", company));
					fieldValueList.add(new FieldValue("address", address));
					fieldValueList.add(new FieldValue("email", dataObj.getString("email")));
					fieldValueList.add(new FieldValue("contactNo", dataObj.getString("contact_number")));
					fieldValueList.add(new FieldValue("imageUrl", dataObj.getString("splash_screen_image")));
					binder.insert(table, fieldValueList);
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

	public static boolean login(SQLiteAdapter db, String username, String password,
										  OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "login";
		String url = App.WEB_URL + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("api_key", Session.API_KEY);
			paramsObj.put("username", password);
			paramsObj.put("password", username);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.getHttpResponse(url, params, TIMEOUT);
			Log.e("login PARAMS", params);
			Log.e("login RESPONSE", response);
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
				String table = Tables.getName(Tables.TB.CREDENTIALS);
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int d = 0; d < dataArray.length(); d++) {
					JSONObject dataObj = dataArray.getJSONObject(d);
					int empID = dataObj.getInt("user_id");
					ArrayList<FieldValue> list = new ArrayList<>();
					list.add(new FieldValue("dDate", CodePanUtils.getDate()));
					list.add(new FieldValue("dTime", CodePanUtils.getTime()));
					list.add(new FieldValue("empID", empID));
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
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

	public static boolean authorizeDevice(SQLiteAdapter db, String authorizationCode, String deviceID,
										  OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "authorize-device";
		String url = App.API_V10 + action;
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
				try {
					JSONArray dataArray = responseObj.getJSONArray("data");
					ArrayList<FieldValue> fieldValueList = new ArrayList<>();
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String apiKey = dataObj.getString("api_key");
						fieldValueList.clear();
						fieldValueList.add(new FieldValue("apiKey", apiKey));
						fieldValueList.add(new FieldValue("authorizationCode", authorizationCode));
						fieldValueList.add(new FieldValue("deviceID", deviceID));
						String query = "SELECT ID FROM " + table + " WHERE ID = 1";
						if(!db.isRecordExists(query)) {
							binder.insert(table, fieldValueList);
						}
						else {
							binder.update(table, fieldValueList, 1);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean getSyncBatchID(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-sync-batch-id";
		String url = App.API_V10 + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieFormLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.getHttpResponse(url, params, TIMEOUT);
			Log.e("getSyncBatchID PARAMS", params);
			Log.e("getSyncBatchID RESPONSE", response);
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
				String table = Tables.getName(Tables.TB.SYNC_BATCH);
				try {
					JSONArray dataArray = responseObj.getJSONArray("data");
					ArrayList<FieldValue> fieldValueList = new ArrayList<>();
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String query = "SELECT ID FROM " + table + " WHERE ID = 1";
						fieldValueList.clear();
						fieldValueList.add(new FieldValue("syncBatchID", dataObj.getString("sync_batch_id")));
						if(!db.isRecordExists(query)) {
							binder.insert(table, fieldValueList);
						}
						else {
							binder.update(table, fieldValueList, 1);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean getCompany(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-company";
		String url = App.API_V10 + action;
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
				try {
					JSONArray dataArray = responseObj.getJSONArray("data");
					ArrayList<FieldValue> fieldValueList = new ArrayList<>();
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String coID = dataObj.getString("company_id");
						String company = CodePanUtils.handleUniCode(dataObj.getString("name"));
						String address = CodePanUtils.handleUniCode(dataObj.getString("address"));
						String logoUrl = dataObj.getString("company_logo");
						CodePanUtils.clearImageUrl(db.getContext(), logoUrl);
						fieldValueList.clear();
						fieldValueList.add(new FieldValue("ID", coID));
						fieldValueList.add(new FieldValue("name", company));
						fieldValueList.add(new FieldValue("address", address));
						fieldValueList.add(new FieldValue("email", dataObj.getString("email")));
						fieldValueList.add(new FieldValue("contactNo", dataObj.getString("contact_number")));
						fieldValueList.add(new FieldValue("imageUrl", dataObj.getString("splash_screen_image")));
						fieldValueList.add(new FieldValue("logoUrl", logoUrl));
						String query = "SELECT ID FROM " + table + " WHERE ID = '" + coID + "'";
						if(!db.isRecordExists(query)) {
							binder.insert(table, fieldValueList);
						}
						else {
							binder.update(table, fieldValueList, coID);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
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
		String url = App.API_V10 + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieFormLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("username", username);
			paramsObj.put("password", password);
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
				try {
					JSONArray dataArray = responseObj.getJSONArray("data");
					ArrayList<FieldValue> fieldValueList = new ArrayList<>();
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String empID = dataObj.getString("user_id");
						fieldValueList.clear();
						fieldValueList.add(new FieldValue("dDate", CodePanUtils.getDate()));
						fieldValueList.add(new FieldValue("dTime", CodePanUtils.getTime()));
						fieldValueList.add(new FieldValue("isLogOut", false));
						fieldValueList.add(new FieldValue("empID", empID));
						String query = "SELECT ID FROM " + table + " WHERE ID = 1";
						if(!db.isRecordExists(query)) {
							binder.insert(table, fieldValueList);
						}
						else {
							binder.update(table, fieldValueList, 1);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean getEmployee(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-employee";
		String url = App.API_V10 + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieFormLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.getHttpResponse(url, params, TIMEOUT);
			Log.e("getEmployee PARAMS", params);
			Log.e("getEmployee RESPONSE", response);
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
				String table = Tables.getName(Tables.TB.EMPLOYEE);
				try {
					JSONArray dataArray = responseObj.getJSONArray("data");
					ArrayList<FieldValue> fieldValueList = new ArrayList<>();
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String empID = dataObj.getString("user_id");
						String firstName = CodePanUtils.handleUniCode(dataObj.getString("fname"));
						String lastName = CodePanUtils.handleUniCode(dataObj.getString("lname"));
						String employeeNo = CodePanUtils.handleUniCode(dataObj.getString("employee_code"));
						String imageUrl = CodePanUtils.handleUniCode(dataObj.getString("profile_picture"));
						CodePanUtils.clearImageUrl(db.getContext(), imageUrl);
						fieldValueList.clear();
						fieldValueList.add(new FieldValue("ID", empID));
						fieldValueList.add(new FieldValue("firstName", firstName));
						fieldValueList.add(new FieldValue("lastName", lastName));
						fieldValueList.add(new FieldValue("employeeNo", employeeNo));
						fieldValueList.add(new FieldValue("groupID", dataObj.getString("team_id")));
						fieldValueList.add(new FieldValue("email", dataObj.getString("email")));
						fieldValueList.add(new FieldValue("isActive", dataObj.getInt("is_active")));
						fieldValueList.add(new FieldValue("imageUrl", imageUrl));
						String query = "SELECT ID FROM " + table + " WHERE ID = '" + empID + "'";
						if(!db.isRecordExists(query)) {
							binder.insert(table, fieldValueList);
						}
						else {
							binder.update(table, fieldValueList, empID);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean getForms(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-forms";
		String url = App.API_V10 + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieFormLib.getAPIKey(db);
			String groupID = TarkieFormLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("team_id", groupID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.getHttpResponse(url, params, TIMEOUT);
			Log.e("getForms PARAMS", params);
			Log.e("getForms RESPONSE", response);
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
				String table = Tables.getName(Tables.TB.FORMS);
				try {
					JSONArray dataArray = responseObj.getJSONArray("data");
					ArrayList<FieldValue> fieldValueList = new ArrayList<>();
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String formID = dataObj.getString("form_id");
						String name = CodePanUtils.handleUniCode(dataObj.getString("form_name"));
						String description = CodePanUtils.handleUniCode(dataObj.getString("form_description"));
						String logoUrl = dataObj.getString("form_logo");
						CodePanUtils.clearImageUrl(db.getContext(), logoUrl);
						fieldValueList.clear();
						fieldValueList.add(new FieldValue("ID", formID));
						fieldValueList.add(new FieldValue("name", name));
						fieldValueList.add(new FieldValue("groupID", groupID));
						fieldValueList.add(new FieldValue("description", description));
						fieldValueList.add(new FieldValue("dateCreated", dataObj.getString("form_date_created")));
						fieldValueList.add(new FieldValue("timeCreated", dataObj.getString("form_time_created")));
						fieldValueList.add(new FieldValue("isActive", dataObj.getString("form_is_active")));
						fieldValueList.add(new FieldValue("logoUrl", logoUrl));
						String query = "SELECT ID FROM " + table + " WHERE ID = '" + formID + "'";
						if(!db.isRecordExists(query)) {
							binder.insert(table, fieldValueList);
						}
						else {
							binder.update(table, fieldValueList, formID);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean getFields(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-forms-fields";
		String url = App.API_V10 + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieFormLib.getAPIKey(db);
			String groupID = TarkieFormLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("team_id", groupID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.getHttpResponse(url, params, TIMEOUT);
			Log.e("getForms PARAMS", params);
			Log.e("getForms RESPONSE", response);
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
				try {
					JSONArray dataArray = responseObj.getJSONArray("data");
					ArrayList<FieldValue> fieldValueList = new ArrayList<>();
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String fieldID = dataObj.getString("field_id");
						String type = dataObj.getString("field_type");
						String name = CodePanUtils.handleUniCode(dataObj.getString("field_name"));
						String uniCode = CodePanUtils.handleUniCode(dataObj.getString("field_description"));
						String description = CodePanUtils.handleNextLine(uniCode, false);
						fieldValueList.clear();
						fieldValueList.add(new FieldValue("ID", fieldID));
						fieldValueList.add(new FieldValue("name", name));
						fieldValueList.add(new FieldValue("type", type));
						fieldValueList.add(new FieldValue("description", description));
						fieldValueList.add(new FieldValue("formID", dataObj.getString("field_form_id")));
						fieldValueList.add(new FieldValue("orderNo", dataObj.getString("field_order_number")));
						fieldValueList.add(new FieldValue("isRequired", dataObj.getInt("field_is_required")));
						fieldValueList.add(new FieldValue("isActive", dataObj.getInt("field_is_active")));
						String table = Tables.getName(Tables.TB.FIELDS);
						String query = "SELECT ID FROM " + table + " WHERE ID = '" + fieldID + "'";
						if(!db.isRecordExists(query)) {
							binder.insert(table, fieldValueList);
						}
						else {
							binder.update(table, fieldValueList, fieldID);
						}
						if(!dataObj.isNull("field_choices")) {
							JSONArray choicesArray = dataObj.getJSONArray("field_choices");
							for(int c = 0; c < choicesArray.length(); c++) {
								JSONObject choicesObj = choicesArray.getJSONObject(c);
								String code = choicesObj.getString("field_choice_id");
								String choice = CodePanUtils.handleUniCode(choicesObj.getString("field_choice_name"));
								fieldValueList.clear();
								fieldValueList.add(new FieldValue("code", code));
								fieldValueList.add(new FieldValue("name", choice));
								fieldValueList.add(new FieldValue("fieldID", fieldID));
								table = Tables.getName(Tables.TB.CHOICES);
								query = "SELECT ID FROM " + table + " WHERE code = '" + code + "'";
								if(!db.isRecordExists(query)) {
									binder.insert(table, fieldValueList);
								}
								else {
									String choiceID = db.getString(query);
									binder.update(table, fieldValueList, choiceID);
								}
							}
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}
}
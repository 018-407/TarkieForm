package com.mobileoptima.core;

import android.content.Context;
import android.util.Log;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.object.EntryObj;
import com.mobileoptima.object.FieldObj;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Tx {

	public static boolean uploadPhoto(SQLiteAdapter db, ImageObj image, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		String action = "add-photos";
		String url = App.API_V10 + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String empID = TarkieLib.getEmployeeID(db);
			String groupID = TarkieLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("employee_id", empID);
			paramsObj.put("team_id", groupID);
			paramsObj.put("local_record_id", image.ID);
			paramsObj.put("sync_batch_id", image.syncBatchID);
			paramsObj.put("is_signature", image.isSignature ? 1 : 0);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + image.fileName;
			String table = Tables.getName(TB.PHOTO);
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusUpload(db, image.ID, table);
			}
			response = CodePanUtils.uploadFile(url, params, "image", file);
			Log.e("uploadPhoto PARAMS", params);
			Log.e("uploadPhoto RESPONSE", response);
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
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int i = 0; i < dataArray.length(); i++) {
					try {
						SQLiteQuery query = new SQLiteQuery();
						JSONObject dataObj = dataArray.getJSONObject(i);
						query.add(new FieldValue("webPhotoID", dataObj.getString("photo_id")));
						query.add(new FieldValue("isUpload", true));
						binder.update(table, query, image.ID);
					}
					catch(Exception e) {
						e.printStackTrace();
						if(errorCallback != null) {
							errorCallback.onError(e.getMessage(), params, response, false);
						}
						return false;
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
		}
		return result;
	}

	public static boolean syncEntry(SQLiteAdapter db, EntryObj entry, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "add-form-answers";
		String url = App.API_V10 + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String empID = TarkieLib.getEmployeeID(db);
			String groupID = TarkieLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("employee_id", empID);
			paramsObj.put("team_id", groupID);
			paramsObj.put("form_id", entry.form.ID);
			paramsObj.put("reference_number", entry.referenceNo);
			paramsObj.put("date_created", entry.dDate);
			paramsObj.put("time_created", entry.dTime);
			paramsObj.put("date_submitted", entry.dateSubmitted);
			paramsObj.put("time_submitted", entry.timeSubmitted);
			paramsObj.put("local_record_id", entry.ID);
			paramsObj.put("sync_batch_id", entry.syncBatchID);
			JSONArray detailsArray = new JSONArray();
			for(FieldObj field : entry.fieldList) {
				JSONObject detailsObj = new JSONObject();
				detailsObj.put("field_id", field.ID);
				detailsObj.put("field_type", field.type);
				detailsObj.put("answer", field.answer.value);
				detailsObj.put("local_record_id", field.answer.ID);
				detailsObj.put("sync_batch_id", field.answer.syncBatchID);
				detailsArray.put(detailsObj);
			}
			paramsObj.put("question_answers", detailsArray);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(url, paramsObj, TIMEOUT);
			Log.e("syncEntry PARAMS", params);
			Log.e("syncEntry RESPONSE", response);
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
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int i = 0; i < dataArray.length(); i++) {
					try {
						SQLiteQuery query = new SQLiteQuery();
						JSONObject dataObj = dataArray.getJSONObject(i);
						query.add(new FieldValue("webEntryID", dataObj.getString("form_record_id")));
//						query.add(new FieldValue("referenceNo", dataObj.getString("reference_number")));//PAUL api adrian
						query.add(new FieldValue("isSync", true));
						binder.update(Tables.getName(TB.ENTRIES), query, entry.ID);
					}
					catch(Exception e) {
						e.printStackTrace();
						if(errorCallback != null) {
							errorCallback.onError(e.getMessage(), params, response, false);
						}
						return false;
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
		}
		return result;
	}
}
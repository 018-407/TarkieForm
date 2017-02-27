package com.mobileoptima.core;

import android.content.Context;
import android.util.Log;

import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnErrorCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.schema.Tables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Tx {
	public static boolean uploadPhoto(SQLiteAdapter db, ImageObj obj, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		String action = "sync-photos";
		String url = App.WEB_URL + action;
		String fileName = null;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieFormLib.getAPIKey(db);
			String empID = TarkieFormLib.getEmployeeID(db);
			String groupID = TarkieFormLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("employee_id", empID);
			paramsObj.put("team_id", groupID);
			paramsObj.put("local_record_id", obj.ID);
			paramsObj.put("sync_batch_id", obj.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + fileName;
			String table = Tables.getName(Tables.TB.PHOTO);
			File file = new File(path);
			if(!file.exists() || file.isDirectory()){
				return TarkieFormLib.updateStatusUpload(db, obj.ID, table);
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

			if(hasData){
				SQLiteBinder binder = new SQLiteBinder(db);
				JSONArray dataArray = responseObj.getJSONArray("data");

				for(int i = 0; i < dataArray.length(); i++){

					try{
						JSONObject dataObj = dataArray.getJSONObject(i);

						ArrayList<FieldValue> fieldValueList = new ArrayList<>();
						fieldValueList.add(new FieldValue("webPhotoID", dataObj.getString("photo_id")));
						fieldValueList.add(new FieldValue("isUpload", true));

						binder.update(table, fieldValueList, obj.ID);
					}
					catch(Exception e){
						e.printStackTrace();
						if(errorCallback != null){
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

	public static boolean syncEntry(SQLiteAdapter db, String entryID, OnErrorCallback errorCallback) {
		boolean result = false;
//		final int INDENT = 4;
//		final int TIMEOUT = 5000;
//		String action = "sync-photos";
//		String url = App.WEB_URL + action;
//		String response = null;
//		String params = null;
//		try {
//			JSONObject paramsObj = new JSONObject();
//			String apiKey = TarkieFormLib.getAPIKey(db);
//			String empID = TarkieFormLib.getEmployeeID(db);
//			String groupID = TarkieFormLib.getGroupID(db);
//			String syncBatchID = TarkieFormLib.getSyncBatchID(db);
//			paramsObj.put("api_key", apiKey);
//			paramsObj.put("employee_id", empID);
//			paramsObj.put("team_id", groupID);
//			paramsObj.put("local_record_id", entryID);
//			paramsObj.put("sync_batch_id", syncBatchID);
//			params = paramsObj.toString(INDENT);
//			response = CodePanUtils.getHttpResponse(url, params, TIMEOUT);
//			Log.e("syncEntry PARAMS", params);
//			Log.e("syncEntry RESPONSE", response);
//			JSONObject responseObj = new JSONObject(response);
//			if(responseObj.isNull("error")) {
//				JSONArray initArray = responseObj.getJSONArray("init");
//				for(int i = 0; i < initArray.length(); i++) {
//					JSONObject initObj = initArray.getJSONObject(i);
//					String status = initObj.getString("status");
//					String message = initObj.getString("message");
//					if(status.equals("ok")) {
//						result = TarkieFormLib.updateStatusSync(db, entryID, Tables.getName(Tables.TB.ENTRIES));
//					}
//					else {
//						if(errorCallback != null) {
//							errorCallback.onError(message, params, response, true);
//						}
//						return false;
//					}
//				}
//			}
//			else {
//				JSONObject errorObj = responseObj.getJSONObject("error");
//				String message = errorObj.getString("message");
//				if(errorCallback != null) {
//					errorCallback.onError(message, params, response, true);
//				}
//			}
//		}
//		catch(JSONException je) {
//			je.printStackTrace();
//			if(errorCallback != null) {
//				errorCallback.onError(je.getMessage(), params, response, false);
//			}
//		}
		return result;
	}
}
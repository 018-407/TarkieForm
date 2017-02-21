package com.mobileoptima.core;

import android.util.Log;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnErrorCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.session.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class Rx {
	public static boolean authorizeDevice(SQLiteAdapter db, String authorizationCode, OnErrorCallback errorCallback) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("api_key", App.API_KEY);
		map.put("authorization_code", authorizationCode);
		map.put("tablet_id", Session.DEVICE_ID);
		map.put("os_type", "ANDROID");
		String params = new JSONObject(map).toString();
		String response = CodePanUtils.getHttpResponse(App.WEB_URL + "authorize-device", params, 5000);
		Log.e("authorization PARAMS", params);
		Log.e("authorization RESPONSE", response);
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray initArray = jsonObject.getJSONArray("init");
			for(int x = 0; x < initArray.length(); x++) {
				JSONObject initObj = initArray.getJSONObject(x);
				String status = initObj.getString("status");
				int recNo = initObj.getInt("recno");
				String message = initObj.getString("message");
				if(status.equals("ok")) {
					JSONArray dataArray = jsonObject.getJSONArray("data");
					for(int y = 0; y < dataArray.length(); y++) {
						JSONObject dataObj = dataArray.getJSONObject(y);
						if(TarkieFormLib.saveAPIKey(db, dataObj.getString("request_code"), authorizationCode, Session.DEVICE_ID)) {
							return true;
						}
						if(errorCallback != null) {
							errorCallback.onError("Failed to save authorization code.", params, response, true);
						}
					}
				}
				if(status.equals("error") && message != null && !message.isEmpty()) {
					if(errorCallback != null) {
						errorCallback.onError(message, params, response, true);
					}
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null){
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return false;
	}
}
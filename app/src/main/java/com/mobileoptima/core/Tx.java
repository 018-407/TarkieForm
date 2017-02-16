package com.mobileoptima.core;

import android.util.Log;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class Tx {

	public static boolean sendAuthorization(SQLiteAdapter db, String code) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("tablet_id", "");
		map.put("authorization_code", code);
		map.put("device_type", "ANDROID");
		String params = new JSONObject(map).toString();
		String response = CodePanUtils.getHttpResponse("http://www.timsie.com/API/1.0/authorization-request.php", params, 5000);
		Log.e("paul", response);
		return false;
	}


}

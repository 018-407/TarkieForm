package com.mobileoptima.core;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;

import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.object.GpsObj;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.schema.Tables;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.mobileoptima.schema.Tables.TB;
import static com.mobileoptima.schema.Tables.TB.CREDENTIALS;
import static com.mobileoptima.schema.Tables.TB.PHOTO;
import static com.mobileoptima.schema.Tables.TB.SYNC_BATCH;

public class TarkieFormLib {

	public static void createTables(SQLiteAdapter db) {
		db.execQuery(Tables.create(TB.API_KEY));
		db.execQuery(Tables.create(TB.SYNC_BATCH));
		db.execQuery(Tables.create(TB.CREDENTIALS));
		db.execQuery(Tables.create(TB.PHOTO));
	}

	public static void alterTables(SQLiteAdapter db, int oldVersion, int newVersion) {
	}

	public static boolean isAuthorized(SQLiteAdapter db) {
		String table = Tables.getName(TB.API_KEY);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		//return db.isRecordExists(query);
		return true;
	}

	public static boolean isLoggedIn(SQLiteAdapter db) {
		return true;
	}

	public static String getAPIKey(SQLiteAdapter db) {
		String table = Tables.getName(TB.API_KEY);
		String query = "SELECT apiKey FROM " + table + " WHERE ID = 1 AND apiKey != ''";
		return db.getString(query);
	}

	public static String getSyncBatchID(SQLiteAdapter db) {
		String table = Tables.getName(SYNC_BATCH);
		String query = "SELECT syncBatchID FROM " + table + " WHERE ID = 1";
		return db.getString(query);
	}

	public static String getEmployeeID(SQLiteAdapter db) {
		String table = Tables.getName(CREDENTIALS);
		String query = "SELECT empID FROM " + table + " WHERE ID = 1";
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

	public static GpsObj getGPS(Context context, Location location,
								long lastLocationUpdate, long interval, float requiredAccuracy) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
		long currentMillis = 0;
		double latitude = 0d;
		double longitude = 0d;
		float accuracy = 0f;
		float speed = 0f;
		boolean isEnabled = false;
		boolean withHistory = false;
		boolean isValid = false;
		String gpsTime = "00:00:00";
		String gpsDate = "0000-00-00";
		if(CodePanUtils.isGpsEnabled(context)) {
			if(location != null) {
				currentMillis = location.getTime();
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				accuracy = location.getAccuracy();
				speed = location.getSpeed();
				long timeElapsed = SystemClock.elapsedRealtime() - lastLocationUpdate;
				long allowance = speed >= 20 ? 0 : 15000;
				if(timeElapsed <= interval + allowance && accuracy <= requiredAccuracy) {
					if(longitude != 0 && latitude != 0) {
						isValid = true;
					}
				}
				Date gps = new Date(currentMillis);
				gpsTime = timeFormat.format(gps);
				gpsDate = dateFormat.format(gps);
				withHistory = true;
			}
			isEnabled = true;
		}
		GpsObj gps = new GpsObj();
		gps.longitude = longitude;
		gps.latitude = latitude;
		gps.accuracy = accuracy;
		gps.currentMillis = currentMillis;
		gps.gpsTime = gpsTime;
		gps.gpsDate = gpsDate;
		gps.isEnabled = isEnabled;
		gps.withHistory = withHistory;
		gps.speed = speed;
		gps.isValid = isValid;
		return gps;
	}

	public static String savePhoto(SQLiteAdapter db, ImageObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String syncBatchID = getSyncBatchID(db);
		String empID = getEmployeeID(db);
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("fileName", obj.fileName));
		fieldValueList.add(new FieldValue("dDate", obj.dDate));
		fieldValueList.add(new FieldValue("dTime", obj.dTime));
		fieldValueList.add(new FieldValue("syncBatchID", syncBatchID));
		fieldValueList.add(new FieldValue("empID", empID));
		String photoID = binder.insert(Tables.getName(PHOTO), fieldValueList);
		binder.finish();
		return photoID;
	}

	public static boolean deletePhoto(Context context, SQLiteAdapter db, ImageObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(PHOTO), fieldValueList, obj.ID);
		String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + obj.fileName;
		CodePanUtils.deleteFile(path);
		return binder.finish();
	}

	public static boolean deletePhotos(Context context, SQLiteAdapter db, ArrayList<ImageObj> deleteList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		for(ImageObj obj : deleteList) {
			fieldValueList.clear();
			fieldValueList.add(new FieldValue("isDelete", true));
			binder.update(Tables.getName(PHOTO), fieldValueList, obj.ID);
			String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + obj.fileName;
			CodePanUtils.deleteFile(path);
		}
		return binder.finish();
	}
}

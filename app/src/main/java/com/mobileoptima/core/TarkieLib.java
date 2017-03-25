package com.mobileoptima.core;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.AnswerType;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.object.AnswerObj;
import com.mobileoptima.object.ChoiceObj;
import com.mobileoptima.object.EntryObj;
import com.mobileoptima.object.FieldObj;
import com.mobileoptima.object.GpsObj;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.tarkieform.AlertDialogFragment;
import com.mobileoptima.tarkieform.R;

import net.sqlcipher.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.mobileoptima.schema.Tables.TB;
import static com.mobileoptima.schema.Tables.TB.ANSWERS;
import static com.mobileoptima.schema.Tables.TB.COMPANY;
import static com.mobileoptima.schema.Tables.TB.CREDENTIALS;
import static com.mobileoptima.schema.Tables.TB.EMPLOYEE;
import static com.mobileoptima.schema.Tables.TB.ENTRIES;
import static com.mobileoptima.schema.Tables.TB.FIELDS;
import static com.mobileoptima.schema.Tables.TB.PHOTO;
import static com.mobileoptima.schema.Tables.TB.SYNC_BATCH;

public class TarkieLib {
	public static void createTables(SQLiteAdapter db) {
		db.execQuery(Tables.create(TB.API_KEY));
		db.execQuery(Tables.create(TB.SYNC_BATCH));
		db.execQuery(Tables.create(TB.CREDENTIALS));
		db.execQuery(Tables.create(TB.COMPANY));
		db.execQuery(Tables.create(TB.EMPLOYEE));
		db.execQuery(Tables.create(TB.PHOTO));
		db.execQuery(Tables.create(TB.FORMS));
		db.execQuery(Tables.create(TB.FIELDS));
		db.execQuery(Tables.create(TB.CHOICES));
		db.execQuery(Tables.create(TB.ENTRIES));
		db.execQuery(Tables.create(TB.ANSWERS));
	}

	public static void updateTables(SQLiteAdapter db, int o, int n) {
		//TODO for updating tables
	}

	public static boolean isAuthorized(SQLiteAdapter db) {
		String table = Tables.getName(TB.API_KEY);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		return db.isRecordExists(query);
	}

	public static boolean isLoggedIn(SQLiteAdapter db) {
		String table = Tables.getName(TB.CREDENTIALS);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1 AND empID != 0 AND isLogOut = 0";
		return db.isRecordExists(query);
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

	public static String getCompanyLogo(SQLiteAdapter db) {
		String table = Tables.getName(COMPANY);
		String query = "SELECT logoUrl FROM " + table + " LIMIT 1";
		return db.getString(query);
	}

	public static String getEmployeeUrl(SQLiteAdapter db, String empID) {
		String table = Tables.getName(EMPLOYEE);
		String query = "SELECT imageUrl FROM " + table + " WHERE ID = '" + empID + "'";
		return db.getString(query);
	}

	public static String getEmployeeName(SQLiteAdapter db, String empID) {
		String name = null;
		String table = Tables.getName(EMPLOYEE);
		String query = "SELECT firstName, lastName FROM " + table + " WHERE ID = '" + empID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			String firstName = cursor.getString(0);
			String lastName = cursor.getString(1);
			name = firstName + " " + lastName;
		}
		cursor.close();
		return name;
	}

	public static String getGroupID(SQLiteAdapter db) {
		String query = "SELECT e.groupID FROM " + Tables.getName(TB.EMPLOYEE) + " e, " +
				Tables.getName(TB.CREDENTIALS) + " c WHERE c.ID = 1 AND e.ID = c.empID";
		return db.getString(query);
	}

	public static int getLastOrderNo(SQLiteAdapter db, String formID) {
		String table = Tables.getName(FIELDS);
		String query = "SELECT orderNo FROM " + table + " WHERE formID = '" + formID + "' " +
				"ORDER BY orderNo DESC LIMIT 1";
		return db.getInt(query);
	}

	public static int getFirstOrderNo(SQLiteAdapter db, String formID) {
		String table = Tables.getName(FIELDS);
		String query = "SELECT orderNo FROM " + table + " WHERE formID = '" + formID + "' " +
				"ORDER BY orderNo LIMIT 1";
		return db.getInt(query);
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
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("fileName", obj.fileName));
		query.add(new FieldValue("dDate", obj.dDate));
		query.add(new FieldValue("dTime", obj.dTime));
		query.add(new FieldValue("isSignature", obj.isSignature));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		String photoID = binder.insert(Tables.getName(PHOTO), query);
		binder.finish();
		return photoID;
	}

	public static boolean deletePhoto(Context context, SQLiteAdapter db, String photoID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(PHOTO), query, photoID);
		String fileName = TarkieLib.getFileName(db, photoID);
		String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		CodePanUtils.deleteFile(path);
		return binder.finish();
	}

	public static boolean deletePhoto(Context context, SQLiteAdapter db, ImageObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(PHOTO), query, obj.ID);
		String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + obj.fileName;
		CodePanUtils.deleteFile(path);
		return binder.finish();
	}

	public static boolean deletePhotos(Context context, SQLiteAdapter db, ArrayList<ImageObj> deleteList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		for(ImageObj obj : deleteList) {
			query.clearAll();
			query.add(new FieldValue("isDelete", true));
			binder.update(Tables.getName(PHOTO), query, obj.ID);
			String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + obj.fileName;
			CodePanUtils.deleteFile(path);
		}
		return binder.finish();
	}

	public static String getFileName(SQLiteAdapter db, String photoID) {
		String table = Tables.getName(TB.PHOTO);
		String query = "SELECT fileName FROM " + table + " WHERE ID = '" + photoID + "' AND isDelete = 0";
		return db.getString(query);
	}

	public static boolean saveEntry(SQLiteAdapter db, String formID, ArrayList<FieldObj> fieldList, boolean isSubmit) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String empID = getEmployeeID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("formID", formID));
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("isSubmit", isSubmit));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		if(isSubmit) {
			query.add(new FieldValue("dateSubmitted", dDate));
			query.add(new FieldValue("timeSubmitted", dTime));
		}
		String entryID = binder.insert(Tables.getName(ENTRIES), query);
		for(FieldObj field : fieldList) {
			if(field.isQuestion) {
				AnswerObj answer = field.answer;
				String value = answer.value;
				switch(field.type) {
					case FieldType.MS:
						if(answer.choiceList != null && !answer.choiceList.isEmpty()) {
							value = "";
							for(ChoiceObj choiceObj : answer.choiceList) {
								if(choiceObj.isCheck) {
									value += choiceObj.code + ",";
								}
							}
							int length = value.length();
							if(length != 0) {
								value = value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.PHOTO:
						if(answer.imageList != null && !answer.imageList.isEmpty()) {
							value = "";
							for(ImageObj image : answer.imageList) {
								value += image.ID + ",";
							}
							int length = value.length();
							if(length != 0) {
								value = value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.YON:
						value = answer.isCheck ? AnswerType.YES : AnswerType.NO;
						break;
					case FieldType.CB:
						value = answer.isCheck ? AnswerType.CHECK : AnswerType.UNCHECK;
						break;
				}
				query.clearAll();
				query.add(new FieldValue("entryID", entryID));
				query.add(new FieldValue("fieldID", field.ID));
				query.add(new FieldValue("value", value));
				binder.insert(Tables.getName(ANSWERS), query);
			}
		}
		return binder.finish();
	}

	public static boolean updateEntry(SQLiteAdapter db, String entryID, ArrayList<FieldObj> fieldList, boolean isSubmit) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isSubmit", isSubmit));
		if(isSubmit) {
			String date = CodePanUtils.getDate();
			String time = CodePanUtils.getTime();
			query.add(new FieldValue("dateSubmitted", date));
			query.add(new FieldValue("timeSubmitted", time));
		}
		binder.update(Tables.getName(ENTRIES), query, entryID);
		for(FieldObj field : fieldList) {
			if(field.isQuestion) {
				AnswerObj answer = field.answer;
				String value = answer.value;
				switch(field.type) {
					case FieldType.MS:
						if(answer.choiceList != null && !answer.choiceList.isEmpty()) {
							value = "";
							for(ChoiceObj choiceObj : answer.choiceList) {
								if(choiceObj.isCheck) {
									value += choiceObj.code + ",";
								}
							}
							int length = value.length();
							if(length != 0) {
								value = value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.PHOTO:
						if(answer.imageList != null && !answer.imageList.isEmpty()) {
							value = "";
							for(ImageObj image : answer.imageList) {
								value += image.ID + ",";
							}
							int length = value.length();
							if(length != 0) {
								value = value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.YON:
						value = answer.isCheck ? AnswerType.YES : AnswerType.NO;
						break;
					case FieldType.CB:
						value = answer.isCheck ? AnswerType.CHECK : AnswerType.UNCHECK;
						break;
				}
				query.clearAll();
				query.add(new FieldValue("value", value));
				String table = Tables.getName(TB.ANSWERS);
				String sql = "SELECT ID FROM " + table + " WHERE fieldID = '" + field.ID + "' " +
						"AND entryID = '" + entryID + "'";
				if(db.isRecordExists(sql)) {
					String answerID = db.getString(sql);
					binder.update(table, query, answerID);
				}
				else {
					query.add(new FieldValue("entryID", entryID));
					query.add(new FieldValue("fieldID", field.ID));
					binder.insert(table, query);
				}
			}
		}
		return binder.finish();
	}

	public static boolean deleteEntry(SQLiteAdapter db, String entryID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(ENTRIES), query, entryID);
		return binder.finish();
	}

	public static AnswerObj getAnswer(SQLiteAdapter db, EntryObj entry, FieldObj field) {
		AnswerObj answer = new AnswerObj();
		if(entry != null) {
			String table = Tables.getName(TB.ANSWERS);
			String query = "SELECT ID, value FROM " + table + " WHERE entryID = '" +
					entry.ID + "' AND fieldID = '" + field.ID + "'";
			Cursor cursor = db.read(query);
			while(cursor.moveToNext()) {
				answer.ID = cursor.getString(0);
				answer.value = cursor.getString(1);
			}
			cursor.close();
		}
		return answer;
	}

	public static String getWebPhotoIDs(SQLiteAdapter db, String value) {
		String webPhotoIDs = "";
		String table = Tables.getName(TB.PHOTO);
		if(value != null && !value.isEmpty()) {
			String condition = "ID = " + value.replace(",", " OR ID = ");
			String query = "SELECT webPhotoID FROM " + table + " WHERE (" + condition + ") " +
					"AND isDelete = 0 AND isUpload = 1";
			Cursor cursor = db.read(query);
			while(cursor.moveToNext()) {
				if(cursor.getPosition() != cursor.getCount() - 1) {
					webPhotoIDs += cursor.getString(0) + ",";
				}
				else {
					webPhotoIDs += cursor.getString(0);
				}
			}
			cursor.close();
		}
		return webPhotoIDs;
	}

	public static boolean logout(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(CREDENTIALS);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isLogOut", true));
		binder.update(table, query, 1);
		return binder.finish();
	}

	public static void showAlertDialog(FragmentActivity activity, String title, String message) {
		final FragmentManager manager = activity.getSupportFragmentManager();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setPositiveButton("Ok", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
			}
		});
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public static int getCountUpload(SQLiteAdapter db) {
		String table = Tables.getName(PHOTO);
		String query = "SELECT COUNT(ID) FROM " + table + " WHERE isUpload = 0 AND isDelete = 0";
		return db.getInt(query);
	}

	public static int getCountSync(SQLiteAdapter db) {
		String table = Tables.getName(TB.ENTRIES);
		String query = "SELECT COUNT(ID) FROM " + table + " WHERE isSync = 0 AND isSubmit = 1 AND isDelete = 0";
		return db.getInt(query);
	}

	public static int getCountSyncTotal(SQLiteAdapter db) {
		return getCountUpload(db) + getCountSync(db);
	}

	public static boolean updateStatusUpload(SQLiteAdapter db, String ID, String table) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isUpload", true));
		binder.update(table, query, ID);
		return binder.finish();
	}

	public static boolean updateStatusSync(SQLiteAdapter db, String ID, String table) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isSync", true));
		binder.update(table, query, ID);
		return binder.finish();
	}
}
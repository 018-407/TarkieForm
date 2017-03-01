package com.mobileoptima.core;

import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.object.AnswerObj;
import com.mobileoptima.object.ChoiceObj;
import com.mobileoptima.object.EntryObj;
import com.mobileoptima.object.FieldObj;
import com.mobileoptima.object.FormObj;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.object.PageObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;

import net.sqlcipher.Cursor;

import java.util.ArrayList;

public class Data {
	public static ArrayList<FormObj> loadForms(SQLiteAdapter db, String search) {
		ArrayList<FormObj> formList = new ArrayList<>();
		String groupID = TarkieFormLib.getGroupID(db);
		String table = Tables.getName(TB.FORMS);
		String condition = search != null ? "name LIKE '%" + search + "%' AND " : "";
		String query = "SELECT ID, name FROM " + table + " WHERE " + condition + "groupID = '" + groupID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			FormObj obj = new FormObj();
			obj.ID = cursor.getString(0);
			obj.name = cursor.getString(1);
			formList.add(obj);
		}
		cursor.close();
		return formList;
	}

	public static ArrayList<PageObj> loadPages(SQLiteAdapter db, String formID) {
		ArrayList<PageObj> pageList = new ArrayList<>();
		String table = Tables.getName(TB.FIELDS);
		String query = "SELECT ID, orderNo FROM " + table + " WHERE type = '" +
				FieldType.PB + "' AND formID = '" + formID + "' ORDER BY orderNo";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			PageObj obj = new PageObj();
			obj.tag = cursor.getString(0);
			int orderNo = cursor.getInt(1);
			int index = cursor.getPosition();
			if(pageList.size() > 0) {
				PageObj previous = pageList.get(index - 1);
				obj.start = previous.end + 2;
			}
			else {
				obj.start = 1;
			}
			obj.end = orderNo - 1;
			obj.orderNo = orderNo;
			pageList.add(obj);
		}
		if(!pageList.isEmpty()) {
			int orderNo = TarkieFormLib.getLastOrderNo(db, formID);
			int index = pageList.size() - 1;
			PageObj last = pageList.get(index);
			if(orderNo > last.orderNo) {
				PageObj obj = new PageObj();
				obj.tag = String.valueOf(orderNo + 1);
				obj.start = last.end + 2;
				obj.orderNo = orderNo;
				obj.end = orderNo;
				pageList.add(obj);
			}
		}
		cursor.close();
		return pageList;
	}

	public static ArrayList<FieldObj> loadFields(SQLiteAdapter db, FormObj form,
												 EntryObj entry, PageObj page) {
		ArrayList<FieldObj> fieldList = new ArrayList<>();
		String table = Tables.getName(TB.FIELDS);
		String query = "SELECT ID, name, description, type, isRequired FROM " + table + " WHERE " +
				"formID = '" + form.ID + "' AND orderNo BETWEEN '" + page.start + "' AND '" +
				page.end + "' ORDER BY orderNo";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			FieldObj field = new FieldObj();
			field.ID = cursor.getString(0);
			field.name = cursor.getString(1);
			field.description = cursor.getString(2);
			field.type = cursor.getString(3);
			field.isRequired = cursor.getInt(4) == 1;
			switch(field.type) {
				case FieldType.SEC:
				case FieldType.LAB:
				case FieldType.PB:
				case FieldType.LINK:
					field.isQuestion = false;
					break;
				default:
					field.isQuestion = true;
					field.answer = TarkieFormLib.getAnswer(db, entry, field);
					break;
			}
			fieldList.add(field);
		}
		cursor.close();
		return fieldList;
	}

	public static ArrayList<ChoiceObj> loadChoices(SQLiteAdapter db, String fieldID) {
		ArrayList<ChoiceObj> choiceList = new ArrayList<>();
		String table = Tables.getName(TB.CHOICES);
		String query = "SELECT ID, code, name FROM " + table + " WHERE fieldID = '" + fieldID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ChoiceObj obj = new ChoiceObj();
			obj.ID = cursor.getString(0);
			obj.code = cursor.getString(1);
			obj.name = cursor.getString(2);
			choiceList.add(obj);
		}
		cursor.close();
		return choiceList;
	}

	public static ArrayList<ImageObj> loadPhotos(SQLiteAdapter db) {
		ArrayList<ImageObj> imageList = new ArrayList<>();
		String empID = TarkieFormLib.getEmployeeID(db);
		String table = Tables.getName(TB.PHOTO);
		String query = "SELECT ID, fileName FROM " + table + " WHERE empID = '" + empID + "' AND " +
				"isDelete = 0 AND isSignature = 0 ORDER BY ID DESC";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ImageObj obj = new ImageObj();
			obj.ID = cursor.getString(0);
			obj.fileName = cursor.getString(1);
			imageList.add(obj);
		}
		cursor.close();
		return imageList;
	}

	public static ArrayList<EntryObj> loadEntries(SQLiteAdapter db) {
		ArrayList<EntryObj> entryList = new ArrayList<>();
		String empID = TarkieFormLib.getEmployeeID(db);
		String query = "SELECT e.ID, e.dDate, e.dTime, e.isSubmit, f.ID, f.name FROM " +
				Tables.getName(TB.ENTRIES) + " e , " + Tables.getName(TB.FORMS) + " f " +
				"WHERE e.empID = '" + empID + "' AND e.isDelete = 0 AND f.ID = e.formID ORDER BY e.ID DESC";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			EntryObj entry = new EntryObj();
			entry.ID = cursor.getString(0);
			entry.dDate = cursor.getString(1);
			entry.dTime = cursor.getString(2);
			entry.isSubmit = cursor.getInt(3) == 1;
			FormObj form = new FormObj();
			form.ID = cursor.getString(4);
			form.name = cursor.getString(5);
			entry.form = form;
			entryList.add(entry);
		}
		cursor.close();
		return entryList;
	}

	public static ArrayList<ImageObj> loadPhotosUpload(SQLiteAdapter db) {
		ArrayList<ImageObj> imageList = new ArrayList<>();
		String table = Tables.getName(TB.PHOTO);
		String query = "SELECT ID, fileName, syncBatchID, isSignature FROM " + table + " WHERE " +
				"isUpload = 0 AND isDelete = 0";
		net.sqlcipher.Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ImageObj image = new ImageObj();
			image.ID = cursor.getString(0);
			image.fileName = cursor.getString(1);
			image.syncBatchID = cursor.getString(2);
			image.isSignature = cursor.getInt(3) == 1;
			imageList.add(image);
		}
		cursor.close();
		return imageList;
	}

	public static ArrayList<EntryObj> loadEntrySync(SQLiteAdapter db) {
		ArrayList<EntryObj> entryList = new ArrayList<>();
		String query = "SELECT ID, dDate, dTime, dateSubmitted, timeSubmitted, syncBatchID, formID " +
				"FROM " + Tables.getName(TB.ENTRIES) + " WHERE isSync = 0 AND isSubmit = 1 AND " +
				"isDelete = 0";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			EntryObj entry = new EntryObj();
			entry.ID = cursor.getString(0);
			entry.dDate = cursor.getString(1);
			entry.dTime = cursor.getString(2);
			entry.dateSubmitted = cursor.getString(3);
			entry.timeSubmitted = cursor.getString(4);
			entry.syncBatchID = cursor.getString(5);
			FormObj form = new FormObj();
			form.ID = cursor.getString(6);
			entry.form = form;
			ArrayList<FieldObj> fieldList = new ArrayList<>();
			query = "SELECT f.ID, f.type, a.ID, a.value FROM " + Tables.getName(TB.ANSWERS) + " a, " +
					Tables.getName(TB.FIELDS) + " f WHERE f.formID = " + form.ID + " " +
					"AND a.entryID = " + entry.ID + " AND a.fieldID = f.ID " +
					"AND a.value NOT NULL";
			Cursor c = db.read(query);
			while(c.moveToNext()) {
				FieldObj field = new FieldObj();
				field.ID = c.getString(0);
				field.type = c.getString(1);
				AnswerObj answer = new AnswerObj();
				answer.ID = c.getString(2);
				String value = c.getString(3);
				switch(field.type) {
					case FieldType.PHOTO:
					case FieldType.SIG:
						answer.value = TarkieFormLib.getWebPhotoIDs(db, value);
						break;
					default:
						answer.value = value;
						break;
				}
				answer.syncBatchID = entry.syncBatchID;
				field.answer = answer;
				fieldList.add(field);
			}
			c.close();
			entry.fieldList = fieldList;
			entryList.add(entry);
		}
		cursor.close();
		return entryList;
	}
}

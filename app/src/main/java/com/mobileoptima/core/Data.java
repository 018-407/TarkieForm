package com.mobileoptima.core;

import android.database.Cursor;

import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.object.ChoiceObj;
import com.mobileoptima.object.FieldObj;
import com.mobileoptima.object.FormObj;
import com.mobileoptima.object.PageObj;
import com.mobileoptima.schema.Tables;

import java.util.ArrayList;

public class Data {

	public static ArrayList<FormObj> loadForms(SQLiteAdapter db) {
		ArrayList<FormObj> formList = new ArrayList<>();
		String groupID = TarkieFormLib.getGroupID(db);
		String table = Tables.getName(Tables.TB.FORMS);
		String query = "SELECT ID, name FROM " + table + " WHERE groupID = '" + groupID + "'";
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
		String table = Tables.getName(Tables.TB.FIELDS);
		String query = "SELECT ID, orderNo FROM " + table + " WHERE type = '" +
				FieldType.PB + "' ORDER BY orderNo";
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

	public static ArrayList<FieldObj> loadFields(SQLiteAdapter db, String formID, PageObj page) {
		ArrayList<FieldObj> fieldList = new ArrayList<>();
		String table = Tables.getName(Tables.TB.FIELDS);
		String query = "SELECT ID, name, description, type, isRequired FROM " + table + " WHERE " +
				"formID = '" + formID + "' AND orderNo BETWEEN '" + page.start + "' AND '" +
				page.end + "' ORDER BY orderNo";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			FieldObj obj = new FieldObj();
			obj.ID = cursor.getString(0);
			obj.name = cursor.getString(1);
			obj.description = cursor.getString(2);
			obj.type = cursor.getString(3);
			obj.isRequired = cursor.getInt(4) == 1;
			fieldList.add(obj);
		}
		cursor.close();
		return fieldList;
	}

	public static ArrayList<ChoiceObj> loadChoices(SQLiteAdapter db, String fieldID) {
		ArrayList<ChoiceObj> choiceList = new ArrayList<>();
		String table = Tables.getName(Tables.TB.CHOICES);
		String query = "SELECT ID, code, name FROM " + table + " WHERE fieldID = '" + fieldID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			ChoiceObj obj = new ChoiceObj();
			obj.ID = cursor.getString(0);
			obj.code = cursor.getString(1);
			obj.name = cursor.getString(2);
			choiceList.add(obj);
		}
		return choiceList;
	}
}

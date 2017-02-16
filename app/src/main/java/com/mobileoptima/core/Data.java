package com.mobileoptima.core;

import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.object.FormObj;

import java.util.ArrayList;

public class Data {

	public static ArrayList<FormObj> loadForms(SQLiteAdapter db) {
		ArrayList<FormObj> formList = new ArrayList<>();
		FormObj form1 = new FormObj();
		form1.ID = "1";
		form1.dDesc = "Service Order Form";
		formList.add(form1);
		FormObj form2 = new FormObj();
		form2.ID = "2";
		form2.dDesc = "Medical Form";
		formList.add(form2);
		return formList;
	}
}

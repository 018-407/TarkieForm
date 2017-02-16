package com.mobileoptima.core;

import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.object.FieldObj;
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

	public static ArrayList<FieldObj> loadFields(SQLiteAdapter db, int page) {
		ArrayList<FieldObj> fieldList = new ArrayList<>();
		FieldObj section = new FieldObj();
		section.ID = "1";
		section.name = "STEP 1";
		section.dDesc = "You may now proceed with the installation proper. For reference in standard " +
				"procedures, you may refer to the standards tab of the iKnow App.";
		section.type = FieldType.SEC;
		fieldList.add(section);
		FieldObj text = new FieldObj();
		text.ID = "2";
		text.name = "Name of field personnel";
		text.type = FieldType.TEXT;
		text.isRequired = true;
		fieldList.add(text);
		FieldObj numeric = new FieldObj();
		numeric.ID = "3";
		numeric.name = "Number of P-CLAMP/S-CLAMP?";
		numeric.type = FieldType.NUM;
		numeric.isRequired = true;
		fieldList.add(numeric);
		FieldObj lText = new FieldObj();
		lText.ID = "4";
		lText.name = "Reason for not letting go.";
		lText.type = FieldType.LTEXT;
		lText.isRequired = true;
		fieldList.add(lText);
		FieldObj date = new FieldObj();
		date.ID = "5";
		date.name = "Date of break-up.";
		date.type = FieldType.DATE;
		date.isRequired = true;
		fieldList.add(date);
		return fieldList;
	}
}

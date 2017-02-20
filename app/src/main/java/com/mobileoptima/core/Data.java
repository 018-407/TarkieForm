package com.mobileoptima.core;

import com.codepan.database.SQLiteAdapter;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.object.FieldObj;
import com.mobileoptima.object.FormObj;
import com.mobileoptima.object.ChoiceObj;

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
		FieldObj section1 = new FieldObj();
		section1.ID = "1";
		section1.field = "STEP 1";
		section1.description = "You may now proceed with the installation proper. For reference in standard " +
				"procedures, you may refer to the standards tab of the iKnow App.";
		section1.type = FieldType.SEC;
		fieldList.add(section1);
		FieldObj text = new FieldObj();
		text.ID = "2";
		text.field = "Name of field personnel";
		text.type = FieldType.TEXT;
		text.isRequired = true;
		fieldList.add(text);
		FieldObj numeric = new FieldObj();
		numeric.ID = "3";
		numeric.field = "Number of P-CLAMP/S-CLAMP?";
		numeric.type = FieldType.NUM;
		numeric.isRequired = true;
		fieldList.add(numeric);
		FieldObj lText = new FieldObj();
		lText.ID = "4";
		lText.field = "Reason for not letting go.";
		lText.type = FieldType.LTEXT;
		lText.isRequired = true;
		fieldList.add(lText);
		FieldObj date = new FieldObj();
		date.ID = "5";
		date.field = "Date of break-up.";
		date.type = FieldType.DATE;
		date.isRequired = true;
		fieldList.add(date);
		FieldObj dropdown = new FieldObj();
		dropdown.ID = "6";
		dropdown.field = "Type of Request.";
		dropdown.type = FieldType.DD;
		dropdown.isRequired = true;
		fieldList.add(dropdown);
		FieldObj yon1 = new FieldObj();
		yon1.ID = "7";
		yon1.field = "With Voice Service?";
		yon1.type = FieldType.YON;
		yon1.isRequired = true;
		fieldList.add(yon1);
		FieldObj yon2 = new FieldObj();
		yon2.ID = "8";
		yon2.field = "Was there a dial tone?";
		yon2.type = FieldType.YON;
		yon2.isRequired = false;
		fieldList.add(yon2);
		FieldObj ms = new FieldObj();
		ms.ID = "8";
		ms.field = "This is for multi-selection question.";
		ms.type = FieldType.MS;
		ms.isRequired = true;
		fieldList.add(ms);
		FieldObj label = new FieldObj();
		label.ID = "9";
		label.field = "This is a sample label, you can add additional information here.";
		label.type = FieldType.LAB;
		fieldList.add(label);
		FieldObj section2 = new FieldObj();
		section2.ID = "9";
		section2.field = "SECTION HEADING";
		section2.description = "This is a section heading, you can add description here.";
		section2.type = FieldType.SEC;
		fieldList.add(section2);
		return fieldList;
	}

	public static ArrayList<ChoiceObj> loadChoices(SQLiteAdapter db) {
		ArrayList<ChoiceObj> choiceList = new ArrayList<>();
		for(int x = 1; x <= 5; x++) {
			ChoiceObj option = new ChoiceObj();
			option.ID = "" + x;
			option.dDesc = "Option " + x;
			choiceList.add(option);
		}
		return choiceList;
	}
}

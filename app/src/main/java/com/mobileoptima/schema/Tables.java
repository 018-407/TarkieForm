package com.mobileoptima.schema;

import com.codepan.database.Field;
import com.codepan.database.SQLiteQuery;
import com.codepan.database.SQLiteQuery.DataType;
public class Tables {

	public enum TB {
		API_KEY,
		SYNC_BATCH,
		CREDENTIALS,
		COMPANY,
		EMPLOYEE,
		PHOTO,
		FORMS,
		FIELDS,
		CHOICES,
		ENTRIES,
		ANSWERS
	}

	public static String create(TB tb) {
		SQLiteQuery query = new SQLiteQuery();
		switch(tb) {
			case API_KEY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("apiKey", DataType.TEXT));
				query.add(new Field("authorizationCode", DataType.TEXT));
				query.add(new Field("deviceID", DataType.TEXT));
				break;
			case SYNC_BATCH:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				break;
			case CREDENTIALS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("isLogOut", 0));
				query.add(new Field("isNewUser", 0));
				break;
			case COMPANY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("code", DataType.TEXT));
				query.add(new Field("address", DataType.TEXT));
				query.add(new Field("email", DataType.TEXT));
				query.add(new Field("mobile", DataType.TEXT));
				query.add(new Field("landline", DataType.TEXT));
				query.add(new Field("logoUrl", DataType.TEXT));
				query.add(new Field("expirationDate", DataType.TEXT));
				break;
			case EMPLOYEE:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("employeeNo", DataType.TEXT));
				query.add(new Field("firstName", DataType.TEXT));
				query.add(new Field("lastName", DataType.TEXT));
				query.add(new Field("email", DataType.TEXT));
				query.add(new Field("mobile", DataType.TEXT));
				query.add(new Field("groupID", DataType.INTEGER));
				query.add(new Field("isActive", DataType.INTEGER));
				query.add(new Field("imageUrl", DataType.TEXT));
				break;
			case PHOTO:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("fileName", DataType.TEXT));
				query.add(new Field("webPhotoID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSignature", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isUpload", 0));
				query.add(new Field("isActive", 0));
				break;
			case FORMS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("description", DataType.TEXT));
				query.add(new Field("dateCreated", DataType.TEXT));
				query.add(new Field("timeCreated", DataType.TEXT));
				query.add(new Field("groupID", DataType.INTEGER));
				query.add(new Field("logoUrl", DataType.TEXT));
				query.add(new Field("isActive", 1));
				break;
			case FIELDS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("description", DataType.TEXT));
				query.add(new Field("type", DataType.TEXT));
				query.add(new Field("formID", DataType.INTEGER));
				query.add(new Field("orderNo", DataType.INTEGER));
				query.add(new Field("isRequired", DataType.INTEGER));
				query.add(new Field("isActive", 1));
				break;
			case CHOICES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("code", DataType.TEXT));
				query.add(new Field("fieldID", DataType.INTEGER));
				break;
			case ENTRIES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("formID", DataType.INTEGER));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("webEntryID", DataType.INTEGER));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isSync", 0));
				break;
			case ANSWERS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("value", DataType.TEXT));
				query.add(new Field("entryID", DataType.INTEGER));
				query.add(new Field("fieldID", DataType.INTEGER));
				query.add(new Field("isUpdate", 0));
				break;
		}
		return query.createTable(getName(tb));
	}

	public static String getName(TB tb) {
		String name = null;
		switch(tb) {
			case API_KEY:
				name = "api_key_tb";
				break;
			case SYNC_BATCH:
				name = "sync_batch_tb";
				break;
			case CREDENTIALS:
				name = "credentials_tb";
				break;
			case COMPANY:
				name = "company_tb";
				break;
			case EMPLOYEE:
				name = "employee_tb";
				break;
			case PHOTO:
				name = "photo_tb";
				break;
			case FORMS:
				name = "forms_tb";
				break;
			case FIELDS:
				name = "fields_tb";
				break;
			case CHOICES:
				name = "choices_tb";
				break;
			case ENTRIES:
				name = "entries_tb";
				break;
			case ANSWERS:
				name = "answers_tb";
				break;
		}
		return name;
	}
}

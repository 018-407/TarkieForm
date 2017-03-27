package com.codepan.database;

import java.util.ArrayList;

public class SQLiteQuery {

	private ArrayList<FieldValue> fieldValueList;
	private ArrayList<Condition> conditionList;
	private ArrayList<Field> fieldList;

	public enum DataType {
		INTEGER,
		TEXT
	}

	public void setFieldList(ArrayList<Field> fieldList) {
		this.fieldList = fieldList;
	}

	public void setFieldValueList(ArrayList<FieldValue> fieldValueList) {
		this.fieldValueList = fieldValueList;
	}

	public void setConditionList(ArrayList<Condition> conditionList) {
		this.conditionList = conditionList;
	}

	public void add(Field field) {
		if(fieldList == null) {
			fieldList = new ArrayList<>();
		}
		fieldList.add(field);
	}

	public void add(FieldValue fieldValue) {
		if(fieldValueList == null) {
			fieldValueList = new ArrayList<>();
		}
		fieldValueList.add(fieldValue);
	}

	public void add(Condition condition) {
		if(conditionList == null) {
			conditionList = new ArrayList<>();
		}
		conditionList.add(condition);
	}

	public void clearConditionList() {
		if(conditionList != null) {
			conditionList.clear();
		}
	}

	public void clearFieldValueList() {
		if(fieldValueList != null) {
			fieldValueList.clear();
		}
	}

	public void clearFieldList() {
		if(fieldList != null) {
			fieldList.clear();
		}
	}

	public void clearAll() {
		clearFieldList();
		clearFieldValueList();
		clearConditionList();
	}

	public boolean hasConditions() {
		if(conditionList != null) {
			return !conditionList.isEmpty();
		}
		return false;
	}

	public boolean hasFieldsValues() {
		if(fieldValueList != null) {
			return !fieldValueList.isEmpty();
		}
		return false;
	}

	public boolean hasFields() {
		if(fieldList != null) {
			return !fieldList.isEmpty();
		}
		return false;
	}

	public String getFields() {
		String fields = "";
		if(fieldList != null) {
			for(Field obj : fieldList) {
				if(obj.withDataType) {
					String dataType = obj.getDataType();
					fields += obj.field + " " + dataType;
					if(obj.isDefault) {
						String defValue = obj.getDefaultValue();
						fields += " DEFAULT " + defValue;
					}
					if(obj.isPrimaryKey) {
						fields += " PRIMARY KEY AUTOINCREMENT NOT NULL";
					}
				}
				else {
					fields += obj.field;
				}
				if(fieldList.indexOf(obj) < fieldList.size() - 1) {
					fields += ", ";
				}
			}
		}
		return fields;
	}

	public String getFieldsValues() {
		String fieldsValues = "";
		if(fieldValueList != null) {
			for(FieldValue obj : fieldValueList) {
				if(obj.value != null) {
					fieldsValues += obj.field + " = '" + obj.value + "'";
				}
				else {
					fieldsValues += obj.field + " = NULL";
				}
				if(fieldValueList.indexOf(obj) < fieldValueList.size() - 1) {
					fieldsValues += ", ";
				}
			}
		}
		return fieldsValues;
	}

	public String getConditions() {
		String condition = "";
		if(conditionList != null) {
			for(Condition obj : conditionList) {
				switch(obj.operator) {
					case EQUALS:
						condition += obj.field + " = '" + obj.value + "'";
						break;
					case NOT_EQUALS:
						condition += obj.field + " != '" + obj.value + "'";
						break;
					case GREATER_THAN:
						condition += obj.field + " > '" + obj.value + "'";
						break;
					case LESS_THAN:
						condition += obj.field + " < '" + obj.value + "'";
						break;
					case GREATER_THAN_OR_EQUALS:
						condition += obj.field + " >= '" + obj.value + "'";
						break;
					case LESS_THAN_OR_EQUALS:
						condition += obj.field + " <= '" + obj.value + "'";
						break;
					case BETWEEN:
						condition += obj.field + " BETWEEN '" + obj.start + "' AND '" + obj.end + "'";
						break;
					case IS_NULL:
						condition += obj.field + " IS NULL";
						break;
					case LIKE:
						condition += obj.field + " LIKE '%" + obj.value + "%'";
						break;
				}
				if(conditionList.indexOf(obj) < conditionList.size() - 1) {
					condition += " AND ";
				}
			}
		}
		return condition;
	}

	public String insert(String table) {
		String fields = "";
		String values = "";
		if(fieldValueList != null) {
			for(FieldValue obj : fieldValueList) {
				fields += obj.field;
				if(obj.value != null) {
					values += "'" + obj.value + "'";
				}
				else {
					values += "NULL";
				}
				if(fieldValueList.indexOf(obj) < fieldValueList.size() - 1) {
					fields += ", ";
					values += ", ";
				}
			}
		}
		return "INSERT INTO " + table + " (" + fields + ") VALUES (" + values + ")";
	}

	public String update(String table, String recID) {
		return "UPDATE " + table + " SET " + getFieldsValues() + " WHERE ID = '" + recID + "'";
	}

	public String update(String table, int recID) {
		return "UPDATE " + table + " SET " + getFieldsValues() + " WHERE ID = '" + recID + "'";
	}

	public String update(String table) {
		String condition = "";
		if(conditionList != null && !conditionList.isEmpty()) {
			condition = " WHERE " + getConditions();
		}
		return "UPDATE " + table + " SET " + getFieldsValues() + condition;
	}

	public String delete(String table) {
		String condition = "";
		if(conditionList != null && !conditionList.isEmpty()) {
			condition = " WHERE " + getConditions();
		}
		return "DELETE FROM " + table + condition;
	}

	public String createTable(String table) {
		return "CREATE TABLE IF NOT EXISTS " + table + " (" + getFields() + ")";
	}

	public String addColumn(String table, String column, String defText) {
		String value = defText != null ? defText : "NULL";
		return "ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT DEFAULT " + value + "";
	}

	public String addColumn(String table, String column, int defInt) {
		return "ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER DEFAULT " + defInt + "";
	}

	public String addColumn(String table, DataType type, String column) {
		String query = null;
		switch(type) {
			case INTEGER:
				query = "ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER";
				break;
			case TEXT:
				query = "ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT";
				break;
		}
		return query;
	}

	public String dropTable(String table) {
		return "DROP TABLE IF EXISTS " + table;
	}
}

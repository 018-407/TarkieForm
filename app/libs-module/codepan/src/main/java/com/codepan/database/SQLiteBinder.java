package com.codepan.database;

import net.sqlcipher.database.SQLiteStatement;

import java.util.ArrayList;

public class SQLiteBinder {

	public enum DataType {
		INTEGER, TEXT
	}

	private SQLiteAdapter db;

	public SQLiteBinder(SQLiteAdapter db) {
		this.db = db;
		this.db.beginTransaction();
	}

	public String insert(String table, ArrayList<FieldValue> list) {
		String fields = "";
		String values = "";
		for(FieldValue obj : list) {
			if(list.indexOf(obj) == list.size() - 1) {
				fields += obj.field;
				if(obj.value != null) {
					values += "'" + obj.value + "'";
				}
				else {
					values += "NULL";
				}
			}
			else {
				fields += obj.field + ",";
				if(obj.value != null) {
					values += "'" + obj.value + "',";
				}
				else {
					values += "NULL,";
				}
			}
		}
		String sql = "INSERT INTO " + table + " (" + fields + ") VALUES (" + values + ")";
		SQLiteStatement insert = db.compileStatement(sql);
		long id = insert.executeInsert();
		insert.close();
		if(id == -1) {
			return null;
		}
		else {
			return String.valueOf(id);
		}
	}

	public void update(String table, ArrayList<FieldValue> list, String locRecID) {
		String fieldsValues = "";
		for(FieldValue obj : list) {
			if(list.indexOf(obj) == list.size() - 1) {
				if(obj.value != null) {
					fieldsValues += obj.field + " = '" + obj.value + "'";
				}
				else {
					fieldsValues += obj.field + " = NULL";
				}
			}
			else {
				if(obj.value != null) {
					fieldsValues += obj.field + " = '" + obj.value + "',";
				}
				else {
					fieldsValues += obj.field + " = NULL,";
				}
			}
		}
		String sql = "UPDATE " + table + " SET " + fieldsValues + " WHERE ID = '" + locRecID + "'";
		SQLiteStatement update = db.compileStatement(sql);
		update.execute();
		update.close();
	}

	public void update(String table, ArrayList<FieldValue> list, int locRecID) {
		String fieldsValues = "";
		for(FieldValue obj : list) {
			if(list.indexOf(obj) == list.size() - 1) {
				if(obj.value != null) {
					fieldsValues += obj.field + " = '" + obj.value + "'";
				}
				else {
					fieldsValues += obj.field + " = NULL";
				}
			}
			else {
				if(obj.value != null) {
					fieldsValues += obj.field + " = '" + obj.value + "',";
				}
				else {
					fieldsValues += obj.field + " = NULL,";
				}
			}
		}
		String sql = "UPDATE " + table + " SET " + fieldsValues + " WHERE ID = '" + locRecID + "'";
		SQLiteStatement update = db.compileStatement(sql);
		update.execute();
		update.close();
	}

	public void update(String table, ArrayList<FieldValue> list, ArrayList<Condition> conditionList) {
		String fieldsValues = "";
		String condition = "";
		for(FieldValue obj : list) {
			if(list.indexOf(obj) == list.size() - 1) {
				if(obj.value != null) {
					fieldsValues += obj.field + " = '" + obj.value + "'";
				}
				else {
					fieldsValues += obj.field + " = NULL";
				}
			}
			else {
				if(obj.value != null) {
					fieldsValues += obj.field + " = '" + obj.value + "',";
				}
				else {
					fieldsValues += obj.field + " = NULL,";
				}
			}
		}
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
			}
			if(conditionList.indexOf(obj) < conditionList.size() - 1) {
				condition += " AND ";
			}
		}
		String sql = "UPDATE " + table + " SET " + fieldsValues + " WHERE " + condition;
		SQLiteStatement update = db.compileStatement(sql);
		update.execute();
		update.close();
	}

	public void delete(String table, ArrayList<Condition> conditionList) {
		String condition = "";
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
			}
			if(conditionList.indexOf(obj) < conditionList.size() - 1) {
				condition += " AND ";
			}
		}
		String sql = "DELETE FROM " + table + " WHERE " + condition;
		SQLiteStatement delete = db.compileStatement(sql);
		delete.execute();
		delete.close();
	}

	public void addColumn(String table, DataType type, String column, int defaultValue, boolean isDefault) {
		String sql = null;
		switch(type) {
			case INTEGER:
				if(!isDefault) {
					sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER";
				}
				else {
					sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER DEFAULT " + defaultValue + "";
				}
				break;
			case TEXT:
				if(!isDefault) {
					sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT";
				}
				else {
					sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT DEFAULT " + defaultValue + "";
				}
				break;
		}
		SQLiteStatement alter = db.compileStatement(sql);
		alter.execute();
		alter.close();
	}

	public void addColumn(String table, DataType type, String column, String defaultValue, boolean isDefault) {
		String sql = null;
		switch(type) {
			case INTEGER:
				if(!isDefault) {
					sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER";
				}
				else {
					sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER DEFAULT " + defaultValue + "";
				}
				break;
			case TEXT:
				if(!isDefault) {
					sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT";
				}
				else {
					sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT DEFAULT " + defaultValue + "";
				}
				break;
		}
		SQLiteStatement alter = db.compileStatement(sql);
		alter.execute();
		alter.close();
	}

	public void dropTable(String table) {
		String sql = "DROP TABLE IF EXISTS " + table;
		SQLiteStatement drop = db.compileStatement(sql);
		drop.execute();
		drop.close();
	}

	public void execute(String sql) {
		SQLiteStatement statement = db.compileStatement(sql);
		statement.execute();
		statement.close();
	}

	public void truncate(String table) {
		String sql = "DELETE FROM " + table;
		SQLiteStatement statement = db.compileStatement(sql);
		statement.execute();
		statement.close();
	}

	public boolean finish() {
		try {
			db.setTransactionSuccessful();
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			db.endTransaction();
		}
		return true;
	}
}

package com.codepan.database;

public class FieldValue {

	private final int TRUE = 1;
	private final int FALSE = 0;

	public String field;
	public String value;

	public FieldValue(String field, String value) {
		this.field = field;
		setString(value);
	}

	public FieldValue(String field, long value) {
		this.field = field;
		setLong(value);
	}

	public FieldValue(String field, int value) {
		this.field = field;
		setInt(value);
	}

	public FieldValue(String field, double value) {
		this.field = field;
		setDouble(value);
	}

	public FieldValue(String field, float value) {
		this.field = field;
		setFloat(value);
	}

	private void setString(String value) {
		this.value = value;
	}

	private void setLong(long value) {
		this.value = String.valueOf(value);
	}

	private void setInt(int value) {
		this.value = String.valueOf(value);
	}

	private void setDouble(double value) {
		this.value = String.valueOf(value);
	}

	private void setFloat(float value) {
		this.value = String.valueOf(value);
	}

	public FieldValue(String field, boolean value) {
		this.field = field;
		if(value) {
			setInt(TRUE);
		}
		else {
			setInt(FALSE);
		}
	}
}

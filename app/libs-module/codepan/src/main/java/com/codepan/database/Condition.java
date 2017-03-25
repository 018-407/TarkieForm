package com.codepan.database;

public class Condition {

	private final int TRUE = 1;
	private final int FALSE = 0;

	public enum Operator {
		EQUALS,
		NOT_EQUALS,
		GREATER_THAN,
		LESS_THAN,
		GREATER_THAN_OR_EQUALS,
		LESS_THAN_OR_EQUALS,
		BETWEEN,
		IS_NULL,
		LIKE
	}

	public Operator operator;
	public String field;
	public String value;
	public String start;
	public String end;

	public Condition(String field, String value, Operator operator) {
		this.operator = operator;
		this.field = field;
		setString(value);
	}

	public Condition(String field, long value, Operator operator) {
		this.operator = operator;
		this.field = field;
		setLong(value);
	}

	public Condition(String field, int value, Operator operator) {
		this.operator = operator;
		this.field = field;
		setInt(value);
	}

	public Condition(String field, double value, Operator operator) {
		this.operator = operator;
		this.field = field;
		setDouble(value);
	}

	public Condition(String field, float value, Operator operator) {
		this.operator = operator;
		this.field = field;
		setFloat(value);
	}

	public Condition(String field, boolean value, Operator operator) {
		this.operator = operator;
		this.field = field;
		if(value) {
			setInt(TRUE);
		}
		else {
			setInt(FALSE);
		}
	}

	public Condition(String field, Operator operator) {
		this.field = field;
		this.operator = operator;
	}

	public Condition(String field, String start, String end, Operator operator) {
		this.operator = operator;
		this.field = field;
		this.start = start;
		this.end = end;
	}

	public Condition(String field, long start, long end, Operator operator) {
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, int start, int end, Operator operator) {
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, double start, double end, Operator operator) {
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
		this.field = field;
	}

	public Condition(String field, float start, float end, Operator operator) {
		this.start = String.valueOf(start);
		this.end = String.valueOf(end);
		this.operator = operator;
		this.field = field;
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
}

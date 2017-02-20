package com.codepan.utils;

import android.graphics.Typeface;

public class SpannableMap {

	public static final int COLOR = 1;
	public static final int FONT = 2;

	public Typeface typeface;
	public int type = COLOR;
	public int color;
	public int start;
	public int end;

	public SpannableMap(int start, int end, int color) {
		this.color = color;
		this.start = start;
		this.end = end;
		this.type = COLOR;
	}

	public SpannableMap(int start, int end, Typeface typeface) {
		this.typeface = typeface;
		this.start = start;
		this.end = end;
		this.type = FONT;
	}
}

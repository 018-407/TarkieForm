package com.codepan.cache;

import android.content.Context;

import com.codepan.database.SQLiteAdapter;

import java.util.Hashtable;

public class SQLiteCache {

	private static final Hashtable<String, SQLiteAdapter> CACHE = new Hashtable<String, SQLiteAdapter>();

	public static SQLiteAdapter getDatabase(Context context, String name, String password, String old, int version) {
		synchronized(CACHE) {
			if(!CACHE.containsKey(name)) {
				SQLiteAdapter db = new SQLiteAdapter(context, name, password, old, version);
				CACHE.put(name, db);
			}
		}
		return CACHE.get(name);
	}
}

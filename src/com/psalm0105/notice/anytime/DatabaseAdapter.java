package com.psalm0105.notice.anytime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter {
	private static final String DATABASE_NAME = "noticeanytime.db";
	public static final String DATABASE_TABLE = "noticeanytime";
	private static final int DATABASE_VERSION = 1;
	
	public static final String KEY_ID="_id";
	public static final String TITLE = "title";
	public static final String TYPE = "type";
	public static final String FILTER = "filter";
	public static final String ACTION = "action";
	public static final String ENABLE = "enable";
	
	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;
	
	private String[] field = new String[]{
			KEY_ID
			,TITLE
			,TYPE
			,FILTER
			,ACTION
			,ENABLE
	};
	
	public DatabaseAdapter(Context context) {
		dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DatabaseAdapter open() {
		db = dbHelper.getWritableDatabase();
		dbHelper.onUpgrade(db, 1, 2);
		return this;
	}
	
	public void close() {
		db.close();
	}
	
	public int insert(RuleDomain ruleDomain) {
		ContentValues values = new ContentValues();
		values.put(TITLE, ruleDomain.getTitle());
		values.put(TYPE, ruleDomain.getType().name());
		values.put(FILTER, ruleDomain.getFilter());
		values.put(ACTION, ruleDomain.getAction());	
		values.put(ENABLE, ruleDomain.getEnable());		
				
		return (int) db.insert(DATABASE_TABLE, TITLE, values);
	}
		
	public void deleteData(String id) {					
		db.delete(DATABASE_TABLE, KEY_ID+"="+id, null);
	}
	
	public Cursor getAll() {	
		return db.query(DATABASE_TABLE, field,null,null,null,null,KEY_ID+" desc");
	}

	public RuleDomain getData(String keyId) {
		Cursor cursor = db.query(DATABASE_TABLE, field,KEY_ID+"="+keyId,null,null,null,null);
		RuleDomain ruleDomain = new RuleDomain();
		ruleDomain.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
		ruleDomain.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
		ruleDomain.setType(RuleType.valueOf(cursor.getString(cursor.getColumnIndex(TYPE))));
		ruleDomain.setFilter(cursor.getString(cursor.getColumnIndex(FILTER)));
		ruleDomain.setAction(cursor.getString(cursor.getColumnIndex(ACTION)));
		ruleDomain.setEnable(cursor.getString(cursor.getColumnIndex(ENABLE)));
		return ruleDomain;
	}
}
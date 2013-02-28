package com.psalm0105.noticeAnyTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter {
	private static final String DATABASE_NAME = "noticeanytime.db";
	public static final String DATABASE_TABLE = "noticeanytime";
	private static final int DATABASE_VERSION = 1;
	
	public static final String KEY_ID="_id";
	public static final String ENABLE = "enable";
	
	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;
	
	private String[] field = new String[]{
			KEY_ID
			,ENABLE
	};
	
	public DatabaseAdapter(Context context) {
		dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DatabaseAdapter open() {
		db = dbHelper.getWritableDatabase();
//		dbHelper.onUpgrade(db, 1, 2);
		return this;
	}
	
	public void close() {
		db.close();
	}
	
	public int insert(RuleDomain ruleDomain) {
		ContentValues values = new ContentValues();
		values.put(ENABLE, ruleDomain.getEnable());		
				
		return (int) db.insert(DATABASE_TABLE, ENABLE, values);
	}
	
	public void update(RuleDomain ruleDomain) {
		ContentValues values = new ContentValues();
		values.put(ENABLE, ruleDomain.getEnable());	
		db.update(DATABASE_TABLE, values, KEY_ID+"="+ruleDomain.getId(), null);
	}
		
	public void deleteData(String id) {					
		db.delete(DATABASE_TABLE, KEY_ID+"="+id, null);
	}
	
	public Cursor getAll() {	
		return db.query(DATABASE_TABLE, field,null,null,null,null,KEY_ID+" desc");
	}
	
	public Cursor getEnable() {	
		return db.query(DATABASE_TABLE, field,ENABLE+"='true'",null,null,null,null);
	}

	public RuleDomain getData(String keyId) {
		Cursor cursor = db.query(DATABASE_TABLE, field,KEY_ID+"="+keyId,null,null,null,null);
		RuleDomain ruleDomain = new RuleDomain();
		ruleDomain.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
		ruleDomain.setEnable(cursor.getString(cursor.getColumnIndex(ENABLE)));
		return ruleDomain;
	}
}
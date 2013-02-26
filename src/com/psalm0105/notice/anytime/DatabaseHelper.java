/* Copyright (c) 2008-2011 -- CommonsWare, LLC

	 Licensed under the Apache License, Version 2.0 (the "License");
	 you may not use this file except in compliance with the License.
	 You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

	 Unless required by applicable law or agreed to in writing, software
	 distributed under the License is distributed on an "AS IS" BASIS,
	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 See the License for the specific language governing permissions and
	 limitations under the License.
*/
	 
package com.psalm0105.notice.anytime;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_CREATE = "CREATE TABLE "+DatabaseAdapter.DATABASE_TABLE+" ( "+
		DatabaseAdapter.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
		DatabaseAdapter.TITLE+" TEXT, " +
		DatabaseAdapter.TYPE+" TEXT, " +
		DatabaseAdapter.FILTER+" TEXT, " +
		DatabaseAdapter.ACTION+" TEXT, " +
		DatabaseAdapter.ENABLE+" TEXT)";

		
	public DatabaseHelper(Context context, String databaseName, Object object, int databaseVersion) {
		super(context, databaseName, null, databaseVersion);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+DatabaseAdapter.DATABASE_TABLE+"");
		onCreate(db);
	}
}


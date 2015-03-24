package com.yt.worlddatetime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Mycitys extends SQLiteOpenHelper {

	public Mycitys(Context context) {
		super(context, "mycity", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE IF NOT EXISTS cities (_id INTEGER PRIMARY KEY, display_name VARCHAR(200),alternate_names STRING(5000),latitude DOUBLE,longitude DOUBLE,country_code VARCHAR(3),admin1_code VARCHAR(3),timezone VARCHAR(128),utc_offset INTEGER)");
	}

	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}

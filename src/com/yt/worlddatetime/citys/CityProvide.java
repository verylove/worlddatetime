package com.yt.worlddatetime.citys;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;



public class CityProvide extends ContentProvider {
	DBHelper dbHelper;
	  
	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder localSQLiteQueryBuilder = new SQLiteQueryBuilder();
		localSQLiteQueryBuilder.setTables("cities");
		 //if (TextUtils.isEmpty(sortOrder)) {}
		  String str = "display_name asc"; str = sortOrder;
		 //   {
		      Cursor localCursor = localSQLiteQueryBuilder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, str);
		      localCursor.setNotificationUri(getContext().getContentResolver(), uri);
		      return localCursor;
		 //   }

	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		 long rowId = -1;
        // rowId =dbHelper.getWritableDatabase().insert(Config.TABLE_NAME, null, values);
         Uri cururi = Uri.withAppendedPath(URI.uri, rowId+"");
         this.getContext().getContentResolver().notifyChange(uri,null);
	        
		return cururi;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final class URI
	{
	  public static final Uri uri = Uri.parse("content://com.yt.worlddatetime.citys.CityProvide/cities?notify=false");
	  private static ContentValues b = new ContentValues();
	}
	
	public static final  class DBHelper extends SQLiteOpenHelper{
	    
		private Context context;
		
	    public DBHelper(Context context){
	        super(context,"cities.sql", null, 3);
	        this.context = context;
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) {

	    	db.execSQL("CREATE TABLE IF NOT EXISTS cities (_id INTEGER PRIMARY KEY, display_name VARCHAR(200),alternate_names STRING(5000),latitude DOUBLE,longitude DOUBLE,country_code VARCHAR(3),admin1_code VARCHAR(3),timezone VARCHAR(128),utc_offset INTEGER)");
	    	db.execSQL("CREATE INDEX IF NOT EXISTS IDX_display_name ON cities (display_name COLLATE LOCALIZED)");
	        db.execSQL("CREATE INDEX IF NOT EXISTS IDX_utc_offset ON cities (utc_offset)");
	         
	        
	        try {
				GZIPInputStream localGZIPInputStream = new GZIPInputStream(this.context.getAssets().open("cities15000.sql.gz.jpg"));
				BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localGZIPInputStream, "UTF-8"), 32768);
				for (;;)
				{
				  String str = localBufferedReader.readLine();
				  if (TextUtils.isEmpty(str))
				  {
				    localBufferedReader.close();
				    localGZIPInputStream.close();
				    return;
				  }
				  db.execSQL(str);
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
	        
	        
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // TODO Auto-generated method stub
	        
	    }
	    
	}
	
	
	
}



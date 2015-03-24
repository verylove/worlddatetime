package com.yt.worlddatetime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;

import com.yt.worlddatetime.citys.ListCountriesActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {


	private Button button;
	private ListView listview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button = (Button) findViewById(R.id.button1);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
								
				//Toast.makeText(getApplicationContext(), "test", 1).show();

				Intent list = new Intent();
			
				list.setClass(getApplicationContext(), ListCountriesActivity.class);
				startActivity(list);

			}
		});

		Mycitys citys = new Mycitys(this);
		SQLiteDatabase db = null;
		db = citys.getReadableDatabase();
		Cursor c =   db.query("cities", null, null, null, null, null, null);
		List<Map<String, ?>> data = new ArrayList<Map<String,?>>();
		int i=0;
		if(c.moveToFirst()){
			do{
	
					Log.d("YT","--------"+i+"="+c.getCount());
	
				  
					Map<String,String> m = new HashMap<String, String>();
					String name = c.getString(c.getColumnIndex("display_name"));
					Log.d("YT","----"+name);
					m.put("name", name);
					data.add(m);
					i++;
	
			}while(c.moveToNext());
		}
		
		
		listview = (ListView)findViewById(R.id.mycitylist);
	
		
		listview.setAdapter(new SimpleAdapter(getApplicationContext(), data,android.R.layout.simple_list_item_1, new String[]{"name"}, new int[]{android.R.id.text1}));
		

	}

}

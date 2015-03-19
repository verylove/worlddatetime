package com.yt.worlddatetime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView listView = null;
	private ListView ListView2 = null;
	List<Countries> list = new ArrayList<Countries>();
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String Result = "";
		try {

			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open("datetime"));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";

			while ((line = bufReader.readLine()) != null) {
				Result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Result = "";
		JSONArray jsonArray = null;

		try {
			jsonArray = new JSONArray(Result);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		
		
		JSONArray item = null;

		for (int i = 0; i < jsonArray.length(); i++) {
			item = null;
			
			Countries map = new Countries();
			
			try {
				item = jsonArray.getJSONArray(i);
			} catch (JSONException e) {
				e.printStackTrace();
			} 

			
			try {
				
				map.setId(item.getInt(0));
				map.setTextualId(item.getString(1));
				map.setName(item.getString(2));
				map.setDesc(item.getString(3));
				map.setCode(item.getString(4));
				map.setCountries(item.getString(5));
				map.setLongitude(item.getDouble(6));
				map.setLatitude(item.getDouble(7));
				
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			
			list.add(map);
			
			map = null;
		}


		button = (Button) findViewById(R.id.button1);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				@SuppressWarnings("unused")
				String[] arrayOfString = TimeZone.getAvailableIDs();
				List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();  
				
				for (int i = 0; i < arrayOfString.length; i++) {
					Map<String,Object> item;  
				    item = new HashMap<String,Object>();  
			        item.put("name",arrayOfString[i].toString());			       
			        data.add(item);
			     }

				
				ListView2 = (ListView) findViewById(R.id.listView2);

				ListAdapter adapter = new SimpleAdapter(getApplicationContext(),  data, android.R.layout.simple_list_item_1, new String[]{"name"}, new int[]{android.R.id.text1});

				ListView2.setAdapter(adapter);
				 
				
				Toast.makeText(getApplicationContext(), "test", 1).show();

			}
		});

		
		listView = (ListView) findViewById(R.id.listView1);

		ListAdapter adapter = new CountriesAdapter(getApplicationContext(),
				list);

		 listView.setAdapter(adapter);

	}

}

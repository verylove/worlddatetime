package com.yt.worlddatetime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView listView = null;
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
				Toast.makeText(getApplicationContext(), "test", 1).show();

			}
		});

		
		listView = (ListView) findViewById(R.id.listView1);

		ListAdapter adapter = new CountriesAdapter(getApplicationContext(),
				list);

		 listView.setAdapter(adapter);

	}

}

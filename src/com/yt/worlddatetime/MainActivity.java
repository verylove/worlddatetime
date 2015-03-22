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

import com.yt.worlddatetime.citys.ListCountriesActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {


	private Button button;

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

		

	}

}

package com.yt.worlddatetime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.Inflater;

import org.json.JSONArray;
import org.json.JSONException;

import com.yt.worlddatetime.citys.Countries;
import com.yt.worlddatetime.citys.ListCountriesActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView listview;

	Intent list = new Intent();
	final List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	

	}

	// 菜单处理部分
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {// 建立菜单
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	    public boolean onOptionsItemSelected(MenuItem item) { //菜单响应函数
	        switch (item.getItemId()) {
	        case R.id.scan:
	        	list.setClass(getApplicationContext(),
						ListCountriesActivity.class);
				startActivity(list);
	            return true;
	        case R.id.quit:
	            finish();
	            return true;
	        case R.id.clear:
	           
	            return true;
	        case R.id.save:

	            return true;
	        }
	        return false;
	    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
	}

	public void initData() {
		Mycitys citys = new Mycitys(this);
		SQLiteDatabase db = null;
		db = citys.getReadableDatabase();
		Cursor c = db.query("cities", null, null, null, null, null, null);

		data.clear();
		int i = 0;
		if (c.moveToFirst()) {
			do {

				Map<String, String> m = new HashMap<String, String>();
				String id = c.getString(c.getColumnIndex("_id"));
				String name = c.getString(c.getColumnIndex("display_name"));
				String timezone = c.getString(c.getColumnIndex("timezone"));
				String country_code = c.getString(c
						.getColumnIndex("country_code"));

				m.put("id", id);
				m.put("name", name);
				m.put("timezone", timezone);
				m.put("country_code", country_code);
				data.add(m);
				i++;

			} while (c.moveToNext());
		}

		listview = (ListView) findViewById(R.id.mycitylist);

		listview.setOnItemClickListener(new onLongClick());

		listview.setAdapter(new myCityAdapter());

	}

	final class ViewMyCity {
		public TextView name;
		public TextView time;
	}

	class onLongClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Map<String, String> m = (Map<String, String>) listview.getAdapter()
					.getItem(position);
			// Toast.makeText(getApplicationContext(),
			// m.get("name")+m.get("id"), 1).show();

			View diaView = View.inflate(getApplicationContext(),
					R.layout.tip_dialog, null);

			int Pos[] = { -1, -1 };

			view.getLocationInWindow(Pos);

			SelectDialog selectDialog = new SelectDialog(MainActivity.this,
					R.style.tip_dialog);// 创建Dialog并设置样式主题
			Window win = selectDialog.getWindow();
			selectDialog.id = m.get("id");

			LayoutParams params = new LayoutParams();
			// params.x = Pos[0]+160;//设置x坐标
			params.y = Pos[1] - 615;// 设置y坐标
			Log.d("YT", Pos[0] + "-" + Pos[1]);
			win.setAttributes(params);
			win.setGravity(Gravity.RIGHT);
			selectDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
			win.setWindowAnimations(R.style.dialogAnimationStyle); // 添加动画
			selectDialog.show();

		}

	}

	public class SelectDialog extends AlertDialog {
		private Button btnDel, btnNo;
		public String id;

		public SelectDialog(Context context, int theme) {
			super(context, theme);
		}

		public SelectDialog(Context context) {
			super(context);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.tip_dialog);

			btnDel = (Button) findViewById(R.id.btnDel);
			btnNo = (Button) findViewById(R.id.btnNodel);
			btnDel.setOnClickListener(new DelData(this));
			btnNo.setOnClickListener(new noDelData(this));
		}

		class DelData implements View.OnClickListener {
			private SelectDialog sv;

			DelData(SelectDialog sv) {
				this.sv = sv;
			}

			@Override
			public void onClick(View v) {
				Mycitys citys = new Mycitys(getApplicationContext());
				SQLiteDatabase db1 = null;
				db1 = citys.getWritableDatabase();

				db1.delete("cities", "_id=?", new String[] { id });
				this.sv.cancel();
				initData();
				/*
				 * db = citys.getReadableDatabase(); Cursor c =
				 * db.query("cities", null, null, null, null, null, null);
				 * data.clear(); int i=0; if(c.moveToFirst()){ do{
				 * 
				 * 
				 * Map<String,String> m = new HashMap<String, String>(); String
				 * id = c.getString(c.getColumnIndex("_id")); String name =
				 * c.getString(c.getColumnIndex("display_name")); String
				 * timezone = c.getString(c.getColumnIndex("timezone")); String
				 * country_code = c.getString(c.getColumnIndex("country_code"));
				 * 
				 * m.put("id", id); m.put("name", name);
				 * m.put("timezone",timezone);
				 * m.put("country_code",country_code); data.add(m); i++;
				 * 
				 * }while(c.moveToNext()); } listview.setAdapter(new
				 * myCityAdapter());
				 */
			}

		}

		class noDelData implements View.OnClickListener {

			private SelectDialog sv;

			noDelData(SelectDialog sv) {
				this.sv = sv;
			}

			@Override
			public void onClick(View v) {
				this.sv.cancel();

			}

		}

	}

	class myCityAdapter implements ListAdapter {

		LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewMyCity mycity = null;

			if (convertView == null) {
				mycity = new ViewMyCity();

				convertView = mInflater.inflate(R.layout.mycityitem, null);

				mycity.name = (TextView) convertView
						.findViewById(R.id.CityName);
				mycity.time = (TextView) convertView
						.findViewById(R.id.CityTime);

				convertView.setTag(mycity);
			} else {
				mycity = (ViewMyCity) convertView.getTag();
			}
			Map<String, String> n = (Map<String, String>) data.get(position);

			TimeZone localTimeZone2 = TimeZone.getTimeZone(n.get("timezone"));
			GregorianCalendar localGregorianCalendar2 = new GregorianCalendar(
					localTimeZone2);
			String display_name = localTimeZone2.getDisplayName();
			localGregorianCalendar2.setTimeInMillis(System.currentTimeMillis());

			String language = Locale.getDefault().getLanguage();

			DateFormat localDateFormat = DateFormat.getDateInstance(
					DateFormat.YEAR_FIELD,
					new Locale(language, n.get("country_code")));
			DateFormat localTimeFormat = DateFormat.getTimeInstance(
					DateFormat.DEFAULT,
					new Locale(language, n.get("country_code")));
			localDateFormat.setTimeZone(localTimeZone2);
			localTimeFormat.setTimeZone(localTimeZone2);
			String nowdate = localDateFormat.format(localGregorianCalendar2
					.getTime());
			String nowtime = localTimeFormat.format(localGregorianCalendar2
					.getTime());

			mycity.name.setText(n.get("name") + "\n" + display_name);
			mycity.time.setText(nowdate + "  \n " + nowtime);

			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return false;
		}
	}

}

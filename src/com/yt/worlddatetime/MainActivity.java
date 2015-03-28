package com.yt.worlddatetime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
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

import com.yt.worlddatetime.citys.CityProvide.DBHelper;
import com.yt.worlddatetime.citys.Countries;
import com.yt.worlddatetime.citys.ListCountriesActivity;
import com.yt.worlddatetime.tools.CheckUpdate;
import com.yt.worlddatetime.tools.ScreenTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView listview;
	public static  ArrayList<Countries> mListLocalCity = null;
	AsyncTask asyncTask;

	final List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	
		asyncTask = new AsyncInitCitys();
		asyncTask.execute("");

		
		Log.d("YT","---------------test");
	}

	
	class AsyncInitCitys extends AsyncTask{

		@Override
		protected Object doInBackground(Object... arg0) {

			ArrayList<Countries> tmp = new ArrayList<Countries>();
			
			DBHelper citys = new DBHelper(getApplicationContext());
			
			SQLiteDatabase db = null;
			db = citys.getReadableDatabase();
			Cursor cursor = db.query("cities", null, null, null, null, null, null);

			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {
				Countries cv = new Countries();
				cursor.moveToPosition(i);

				cv.setId(cursor.getInt(0));
				cv.setName(cursor.getString(1));
				cv.setSortKey(cursor.getString(2));
				cv.setTextualId(cursor.getString(7));
				cv.setCode(cursor.getString(5));
				cv.setDesc(cursor.getString(8));
				
				tmp.add(cv);
			}
			
			mListLocalCity = tmp;
			
			Log.d("YT","Success loading !!");
			
			return null;
		}
		
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
	public boolean onOptionsItemSelected(MenuItem item) { // 菜单响应函数
		switch (item.getItemId()) {
		case R.id.add:
			//asyncTask.cancel(true);
			Intent list = new Intent();
			list.setClass(getApplicationContext(), ListCountriesActivity.class);
			startActivity(list);
			return true;
		case R.id.quit:
			finish();
			return true;
		case R.id.update:
			CheckUpdate cu = new CheckUpdate(this);
			return true;
		case R.id.about:
			AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(   
		            "关于我们").setPositiveButton("(*^__^*) 感谢您的使用！(*^__^*) ", null).create();   
		    Window window = alertDialog.getWindow();   
		    WindowManager.LayoutParams lp = window.getAttributes();   
		    // 设置透明度为0.3   
		    lp.alpha = 0.6f;   
		    window.setAttributes(lp);   
		    alertDialog.show(); 
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
		public com.yt.worlddatetime.tools.AnalogClock analogClock;
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

			
			
			
			SelectDialog addPopWindow = new SelectDialog(MainActivity.this);
			addPopWindow.id = m.get("id");
            addPopWindow.showPopupWindow(view);
			

		}

	}

	
	
	
	public class SelectDialog extends PopupWindow {
		private Button btnDel, btnNo;
		public String id;
	    private View conentView;  
	    

		public  SelectDialog(final Activity context) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			conentView = inflater.inflate(R.layout.tip_dialog, null);
			int h = context.getWindowManager().getDefaultDisplay().getHeight();
			int w = context.getWindowManager().getDefaultDisplay().getWidth();
			// 设置SelectPicPopupWindow的View
			this.setContentView(conentView);
			// 设置SelectPicPopupWindow弹出窗体的宽
			this.setWidth(w/2+50);
			this.setHeight(h/2-500);
		
			
			// 计算x轴方向的偏移量，使得PopupWindow在Title的正下方显示，此处的单位是pixels  
		    int xoffInPixels = ScreenTools.getInstance(MainActivity.this).getScreenWidth() / 2  ;  
		    // 将pixels转为dip  
		    int xoffInDip = ScreenTools.getInstance(MainActivity.this).px2dip(xoffInPixels);  
		   // this.showAsDropDown(this, -xoffInDip, 0);  
		    
		    
			// 设置SelectPicPopupWindow弹出窗体的高
			//this.setHeight(LayoutParams.WRAP_CONTENT);
			// 设置SelectPicPopupWindow弹出窗体可点击
			this.setFocusable(true);
			this.setOutsideTouchable(true);
			// 刷新状态
			this.update();
			// 实例化一个ColorDrawable颜色为半透明
			ColorDrawable dw = new ColorDrawable(0000000000);
			// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
			this.setBackgroundDrawable(dw);
			// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
			// 设置SelectPicPopupWindow弹出窗体动画效果
			this.setAnimationStyle(R.style.AnimationPreview);
			
			btnDel = (Button) conentView.findViewById(R.id.btnDel);
			btnNo = (Button) conentView.findViewById(R.id.btnNodel);
			btnDel.setOnClickListener(new DelData(this));
			btnNo.setOnClickListener(new noDelData(this));
			
		}
		

		class noDelData implements OnClickListener {

			private SelectDialog sv;

			noDelData(SelectDialog sv) {
				this.sv = sv;
			}

			@Override
			public void onClick(View v) {
				this.sv.dismiss();

			}

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
				this.sv.dismiss();
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
		/**
		 * 显示popupWindow
		 * 
		 * @param parent
		 */
		public void showPopupWindow(View parent) {
			if (!this.isShowing()) {
				// 以下拉方式显示popupwindow
				this.showAsDropDown(parent, parent.getLayoutParams().width / 2, -240);
			} else {
				this.dismiss();
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
				mycity.analogClock = (com.yt.worlddatetime.tools.AnalogClock) convertView
						.findViewById(R.id.analogClock);
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
					DateFormat.SHORT,
					new Locale(language, n.get("country_code")));
			localDateFormat.setTimeZone(localTimeZone2);
			localTimeFormat.setTimeZone(localTimeZone2);
			String nowdate = localDateFormat.format(localGregorianCalendar2
					.getTime());
			String nowtime = localTimeFormat.format(localGregorianCalendar2
					.getTime());

			mycity.name.setText(n.get("name") + "\n" + display_name);
			mycity.time.setText(nowdate + "\n" + nowtime);
			mycity.analogClock.setmTimezone(n.get("timezone"));
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

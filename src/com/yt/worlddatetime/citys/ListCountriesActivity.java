package com.yt.worlddatetime.citys;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yt.worlddatetime.Mycitys;
import com.yt.worlddatetime.R;
import com.yt.worlddatetime.citys.MyLetterListView.OnTouchingLetterChangedListener;

public class ListCountriesActivity extends Activity {

	private static final String TAG = "MainActivity";

	private Button queryBtn;// 查询按钮
	private EditText queryContent;// 查询条件
	private ListView listView;// listView显示查询结果
	private AsyncQueryHandler asyncQuery;
	private HashMap<String, Integer> alphaIndexer;
	private Handler handler;
	private OverlayThread overlayThread;
	private TextView overlay;// 显示索引字母
	private MyLetterListView letterListView;
	private ArrayList<Countries> mListLocalCity;
	private TextWatcher textWatcher = new MyFindContactTextWatcher();
	private OnItemClickListener itemClickListener = new MyListItemClickListener();
	private OnItemLongClickListener itemLongClickListener = new MyOnItemLongClickListener();
	private OnClickListener clickListener = new MyOnClickListener();
	private ProgressDialog progressbar;

	
	private SQLiteDatabase db = null;
	
	
	public String[] sections;

	private CountriesAdapter adapter;

	private View MyLetterListView01;
	
	public static long startTime = 0;
	
	private boolean queryFlag = true;
	private List<Countries> queryList = new ArrayList<Countries>();

	public boolean overlayFlag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置窗口特征：启用显示进度的进度条
		requestWindowFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.cityslist);

		initwidget();
		listView.setOnItemClickListener(itemClickListener);
		listView.setOnItemLongClickListener(itemLongClickListener);
		queryBtn.setOnClickListener(clickListener);

		letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		queryContent.addTextChangedListener(textWatcher);
	}

	/**
	 * 初始化部件
	 */
	private void initwidget() {
		queryBtn = (Button) findViewById(R.id.queryBtn);
		queryContent = (EditText) findViewById(R.id.queryContent);
		listView = (ListView) findViewById(R.id.listview);	
		MyLetterListView01 = (View)findViewById(R.id.MyLetterListView01);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		startTime = System.nanoTime(); 
		
		Uri uri = Uri
				.parse("content://com.yt.worlddatetime.citys.CityProvide/cities?notify=false");
		String[] arrayOfString = { "_id", "display_name", "alternate_names",
				"timezone", "country_code","utc_offset" };
		asyncQuery.startQuery(0, null, uri, arrayOfString, null, null,
				"display_name asc");

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		progressbar = ProgressDialog.show(this, "Loading Citys", "Loading...");

	}

	public void initData() {

		String Result = "";
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

				map.setSortKey(item.getString(2));

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

			// tmpList.add(map);

			map = null;
		}

		// Collections.sort(tmpList, new SortByName());

		// setAdapter(tmpList);

	}

	class SortByName implements Comparator {

		@Override
		public int compare(Object lhs, Object rhs) {
			Countries c1 = (Countries) lhs;
			Countries c2 = (Countries) rhs;

			return c1.getName().compareToIgnoreCase(c2.getName());

		}

	}

	private void setAdapter(List<Countries> list) {
		adapter = new CountriesAdapter(this, list);
		listView.setAdapter(adapter);		
	}

	public class setIndex extends AsyncTask<List<Countries>, Integer, String>{
		

		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			MyLetterListView01.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(List<Countries>... params) {

			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[params[0].size()];
			

			for (int i=0;i<params[0].size();i++) {
				// 当前汉语拼音首字母
				String currentStr = getAlpha(params[0].get(i).getName());
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? getAlpha(params[0].get(i - 1)
						.getName()) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(params[0].get(i).getName());
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
			long consumingTime = System.nanoTime() - startTime;
			Log.d("YT",consumingTime/1000+"微秒--setAdapter");
			
			publishProgress(100);
			
			return null;
		}

		


		
		
	}
	
	public class CountriesAdapter extends BaseAdapter implements Filterable {

		private final MyContactFilter mFilter;
		private List<Countries> list;
		private LayoutInflater mInflater;

		public CountriesAdapter(Context context, List<Countries> list) {
			this.list = list;
			this.mInflater = LayoutInflater.from(context);
			mFilter = new MyContactFilter();
			
			setIndex task = new setIndex();  
	        task.execute(this.list);
			
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem, null);

				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.desc = (TextView) convertView.findViewById(R.id.desc);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(list.get(position).getName()+list.get(position).getCode());

			if(list.get(position).getDesc().contains(".")){
				holder.desc.setText("GMT "+list.get(position).getDesc().replace(".5", "")+":30");
			}else{
				holder.desc.setText("GMT "+list.get(position).getDesc()+":00");
			}

			return convertView;
		}

		public final class ViewHolder {
			public TextView name;
			public TextView desc;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		/**
		 * A Filter which select Contact to display by searching in ther Jid.
		 */
		private class MyContactFilter extends Filter {

			/**
			 * Create a ContactFilter.
			 */
			public MyContactFilter() {
			}

			@Override
			protected Filter.FilterResults performFiltering(
					CharSequence constraint) {
				Log.d(TAG, "performFiltering");
				List<Countries> result = new LinkedList<Countries>();
				String condition = (String) constraint;
				condition = condition.trim().toLowerCase();
				// List<Contact> target = mContactOnGroup.get(mSelectedGroup);
				// L.d("target="+target);
				if (queryFlag) {
					queryList = list;
					queryFlag = false;
				}
				try {

					for (Countries obj : queryList) {
						// L.d(lc.displayName + lc.phone + lc.id);
						if ("".equals(condition)) {
							result.add(obj);
						} else if (obj.getSortKey() != null
								&& obj.getSortKey().toLowerCase()
										.contains(condition)
								|| (obj.getName() != null && obj.getName()
										.toLowerCase().contains(condition))) {
							result.add(obj);
						}
					}
				} catch (Exception e) {
					Log.d(TAG, e.toString());
				}
				Filter.FilterResults fr = new Filter.FilterResults();
				fr.values = result;
				fr.count = result.size();
				return fr;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					Filter.FilterResults results) {
				Log.d(TAG, "publishResults");
				List<Countries> contacts = (List<Countries>) results.values;
				setAdapter(contacts);
				setProgressBarIndeterminateVisibility(false);
			}
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			return mFilter;
		}

	}

	/**
	 * 实现通讯录字母索引
	 */
	// 查询联系人

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		@SuppressLint("HandlerLeak")
		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);

		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			setProgressBarIndeterminateVisibility(false);
			if (progressbar.isShowing()) {
				progressbar.dismiss();
			}
			
			long consumingTime = System.nanoTime() - startTime;
			Log.d("YT",consumingTime/1000+"微秒");
			
			if (cursor != null && cursor.getCount() > 0) {
				mListLocalCity = new ArrayList<Countries>();
				cursor.moveToFirst();

				for (int i = 0; i < cursor.getCount(); i++) {
					Countries cv = new Countries();
					cursor.moveToPosition(i);

					cv.setId(cursor.getInt(0));
					cv.setName(cursor.getString(1));
					cv.setSortKey(cursor.getString(2));
					cv.setTextualId(cursor.getString(3));
					cv.setCode(cursor.getString(4));
					cv.setDesc(cursor.getString(5));
					

					// TimeZone localTimeZone2 =
					// TimeZone.getTimeZone(cursor.getString(2));
					// GregorianCalendar localGregorianCalendar2 = new
					// GregorianCalendar(localTimeZone2);
					// localGregorianCalendar2.setTimeInMillis(System.currentTimeMillis());

					mListLocalCity.add(cv);
				}
				if (mListLocalCity.size() > 0) {
					setAdapter(mListLocalCity);
				}
			}
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (overlayFlag) {
				initOverlay();
				overlayFlag = false;
			}
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				listView.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	// 获得汉语拼音首字母
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	class MyFindContactTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			Log.e(TAG, "beforeTextChanged...");
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			Log.e(TAG, "onTextChanged...");

		}

		@Override
		public void afterTextChanged(Editable s) {
			Log.e(TAG, "onTextChanged...");
			// adapter.getFilter().filter(queryContent.getText());
		}

	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			setProgressBarIndeterminateVisibility(true);
			// 查询过滤
			adapter.getFilter().filter(queryContent.getText());

		}
	}

	// listView item点击事件
	class MyListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Countries city = (Countries)listView.getAdapter().getItem(position);
			
			TimeZone localTimeZone2 = TimeZone.getTimeZone(city.getTextualId());
            GregorianCalendar localGregorianCalendar2 = new GregorianCalendar(localTimeZone2);
            String display_name = localTimeZone2.getDisplayName();
            localGregorianCalendar2.setTimeInMillis(System.currentTimeMillis());
            
            
            String language = Locale.getDefault().getLanguage();
            Log.d("YT",language+""+city.getCode()+""+city.getTextualId());
            DateFormat localDateFormat = DateFormat.getDateTimeInstance(DateFormat.YEAR_FIELD,DateFormat.ERA_FIELD,new Locale(language,city.getCode()));
            localDateFormat.setTimeZone(localTimeZone2);
            String noewtime = localDateFormat.format(localGregorianCalendar2.getTime());
           

    		Mycitys citys = new Mycitys(getApplicationContext());
    		db = citys.getWritableDatabase();
    		ContentValues cv = new ContentValues();
    		cv.put("display_name", city.getName());
    		cv.put("alternate_names", city.getSortKey());
    		cv.put("latitude", 0);
    		cv.put("longitude", 0);
    		cv.put("country_code", city.getCode());
    		cv.put("admin1_code", 0);
    		cv.put("timezone", city.getTextualId());
    		cv.put("utc_offset", city.getDesc());    		
    		long result = db.insert("cities", null, cv);
    		//db.execSQL("INSERT INTO cities"
    		//		+ " (display_name, alternate_names, latitude, longitude, country_code, admin1_code, timezone, utc_offset) VALUES"
    		//		+ " ('"+display_name+"','"+city.getSortKey()+"',0,0,'"+city.getCode()+"','',"+city.getTextualId()+", '"+city.getDesc()+"')");
    		
    		
    		ListCountriesActivity.this.finish();
			//Toast.makeText(ListCountriesActivity.this,
			//		String.valueOf(position) + " id=" + city.getId()+" "+display_name+" "+noewtime, 1).show();

		}
	}

	// listView item长按事件
	class MyOnItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Toast.makeText(ListCountriesActivity.this, id + " 长按 " + position,
					0).show();
			return false;
		}
	}
}
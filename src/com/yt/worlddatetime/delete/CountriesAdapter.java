package com.yt.worlddatetime.delete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.yt.worlddatetime.R;
import com.yt.worlddatetime.R.id;
import com.yt.worlddatetime.R.layout;
import com.yt.worlddatetime.citys.Countries;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CountriesAdapter extends BaseAdapter implements Filterable {

	private final String TAG = "CountriesAdapter";
	private final MyContactFilter mFilter;
	private List<Countries> list;
	private LayoutInflater mInflater;
	
	private boolean queryFlag = true;
	public String[] sections;
	private HashMap<String, Integer> alphaIndexer;
	
	private List<Countries> queryList = new ArrayList<Countries>();
	
	public CountriesAdapter(Context context,List<Countries> list){
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
		mFilter = new MyContactFilter();
		alphaIndexer = new HashMap<String, Integer>();
		sections = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			// 当前汉语拼音首字母
			String currentStr = getAlpha(list.get(i).getSortKey());
			// 上一个汉语拼音首字母，如果不存在为“ ”
			String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
					.getSortKey()) : " ";
			if (!previewStr.equals(currentStr)) {
				String name = getAlpha(list.get(i).getSortKey());
				alphaIndexer.put(name, i);
				sections[i] = name;
			}
		}
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
		
		int type = getItemViewType(position);  
        Log.i("YT","getView " + position + " " + convertView + " type = " + type); 
        
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView  = mInflater.inflate(R.layout.listitem, null);
			
			holder.id = (TextView)convertView.findViewById(R.id.name);
			holder.name = (TextView)convertView.findViewById(R.id.desc);
		
			convertView.setTag(holder);
		}else{		
			holder = (ViewHolder)convertView.getTag();		
		}
		
		String id,name;
		
		Countries city = list.get(position);
		
		id = city.getId()+"";
		holder.id.setText(id);
		name = city.getName();
		holder.name.setText(name);
		
		
		return convertView;
	}

	public final class ViewHolder{
		public TextView id;
		public TextView name;
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

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
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
			condition = condition.trim();
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
							&& obj.getSortKey().contains(condition)
							|| (obj.getName() != null && obj.getName()
									.contains(condition))) {
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
			//setAdapter(contacts);
		}
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return mFilter;
	}

}



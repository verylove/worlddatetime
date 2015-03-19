package com.yt.worlddatetime;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CountriesAdapter implements ListAdapter {

	private List<Countries> list;
	private LayoutInflater mInflater;
	
	public CountriesAdapter(Context context,List<Countries> list){
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
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
			convertView  = mInflater.inflate(R.layout.list, null);
			
			holder.id = (TextView)convertView.findViewById(R.id.textView1);
			holder.name = (TextView)convertView.findViewById(R.id.textView2);
		
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
	
}



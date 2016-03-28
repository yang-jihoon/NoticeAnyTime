package com.psalm0105.noticeAnyTime;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ListAdapter extends CursorAdapter {
	
	public ListAdapter(Context context, Cursor c) {
		super(context, c, true);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor){
		
		int id = cursor.getInt((cursor.getColumnIndex(DatabaseAdapter.KEY_ID)));
		view.setId(id);
		
		SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, "RULE_"+id);
		
		TextView enableView = (TextView) view.findViewById(R.id.list_enable);
		enableView.setText(spUtil.getListEnableById());
		if (spUtil.getBooleanByKey("RULE_ENABLED", false)) {
			enableView.setTextColor(Color.GREEN);			
		} else {
			enableView.setTextColor(Color.RED);		
		}
		
		TextView titleView = (TextView) view.findViewById(R.id.list_title);
		titleView.setText(spUtil.getStringByKey("RULE_FILTER", ""));

		TextView messageView = (TextView) view.findViewById(R.id.list_message);
		messageView.setText(spUtil.getListMessageById());	
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater layoutInflater = LayoutInflater.from(context); 
		View view = layoutInflater.inflate(R.layout.listdetail, parent, false);
		
		return view;
	}

}

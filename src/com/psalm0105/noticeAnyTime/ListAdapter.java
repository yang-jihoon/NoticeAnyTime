package com.psalm0105.noticeAnyTime;

import com.psalm0105.noticeAnyTime.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
		
		SharedPreferences prefs = context.getSharedPreferences("RULE_"+id, Context.MODE_PRIVATE);
		
		TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(prefs.getString("RULE_FILTER", "")+" "+id);

		TextView messageView = (TextView) view.findViewById(R.id.type);
		messageView.setText(cursor.getString((cursor.getColumnIndex(DatabaseAdapter.ENABLE)))
				+" "+prefs.getString("RULE_TYPE", "")
				+" "+prefs.getString("RULE_ACTION", ""));	
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater layoutInflater = LayoutInflater.from(context); 
		View view = layoutInflater.inflate(R.layout.listdetail, parent, false);
		
		return view;
	}

}

package com.psalm0105.notice.anytime;

import android.content.Context;
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
		
		view.setId(cursor.getInt((cursor.getColumnIndex(DatabaseAdapter.KEY_ID))));
		
		TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.TITLE))
				+cursor.getInt((cursor.getColumnIndex(DatabaseAdapter.KEY_ID)))
				+cursor.getString((cursor.getColumnIndex(DatabaseAdapter.ENABLE))));

		TextView messageView = (TextView) view.findViewById(R.id.type);
		messageView.setText(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.TYPE)));	
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater layoutInflater = LayoutInflater.from(context); 
		View view = layoutInflater.inflate(R.layout.listdetail, parent, false);
		
		return view;
	}

}

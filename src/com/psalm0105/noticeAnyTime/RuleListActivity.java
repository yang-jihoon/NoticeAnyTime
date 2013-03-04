package com.psalm0105.noticeAnyTime;

import com.psalm0105.noticeAnyTime.NoticeAnyTimeService.LocalBinder;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;

public class RuleListActivity extends Activity {
	
	public static Context context;
	private Cursor mCursor = null;
	private DatabaseAdapter databaseAdapter;

	boolean mBounded;
	NoticeAnyTimeService noticeAnyTimeService;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        RuleListActivity.context = this;
        databaseAdapter = new DatabaseAdapter(this).open();

        mCursor = databaseAdapter.getAll();
        startManagingCursor(mCursor);        

        Window win = getWindow();
        win.requestFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);        
        win.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        ListView listview = (ListView) findViewById(R.id.list);
        listview.setDivider(new ColorDrawable(Color.DKGRAY));
        listview.setDividerHeight(1);
        listview.setAdapter(new ListAdapter(this, mCursor));
        
        listview.setOnItemClickListener(new ListViewItemClickListener());
        
        Button addBtn = (Button) findViewById(R.id.title_addrule);
        addBtn.setOnClickListener(new BtnClickListener());
        
        Button stopBtn = (Button) findViewById(R.id.title_stop);
        stopBtn.setOnClickListener(new BtnClickListener());

        registerForContextMenu(listview);

        Thread serviceThread = new Thread(new Runnable() {
            public void run() {
                Intent intent = NoticeAnyTimeService.getIntent();
                context.startService(intent);
            }
        });
        serviceThread.start();     
    }
	
	 @Override
	 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int index = info.position;
		mCursor.moveToPosition(index);
		String id = mCursor.getString(mCursor.getColumnIndex(DatabaseAdapter.KEY_ID));

    	SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context, "RULE_"+id);    	
    	menu.setHeaderTitle(spUtil.getMessageById());
		menu.add(0, Menu.FIRST, Menu.NONE, "����");
	 }
	 
	 @Override
	 public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		 
		AdapterView.AdapterContextMenuInfo menuInfo;

		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		if (item.getItemId() == Menu.FIRST) {
			int index = menuInfo.position;
			mCursor.moveToPosition(index);
			databaseAdapter.deleteData(mCursor.getString(mCursor.getColumnIndex(DatabaseAdapter.KEY_ID)));
			mCursor.requery();
			return true;
		}
		return false;
	 }	 
	
	public class BtnClickListener implements OnClickListener {

		public void onClick(View view) {
			if (view.getId() == R.id.title_addrule) {				 
	        	//SharedPreferences remove   	
	        	SharedPreferences prefs = getSharedPreferences("RULE_0", Context.MODE_PRIVATE);  
	        	Editor ed = prefs.edit();
	            ed.clear();
	            ed.commit();
				
				Intent intent = new Intent(RuleListActivity.this, RuleSettingActivity.class);
				intent.putExtra("id", "0");
				startActivity(intent);				
			} else if (view.getId() == R.id.title_stop) {
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	            notificationManager.cancel("NAT", 1);

	            noticeAnyTimeService.stopAlarm();
			}
		}

	}
	
	private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			Intent intent = new Intent(RuleListActivity.this, RuleSettingActivity.class);
			intent.putExtra("id", String.valueOf(id));
			startActivity(intent);
		}    	
    }

	@Override
	protected void onResume() {		
		super.onResume();
		
		mCursor.requery();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		mCursor.close();
		databaseAdapter.close();
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		startActivity(new Intent(this, SettingActivity.class));
		return (true);
    }
    
	public ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
	        mBounded = false;
	        noticeAnyTimeService = null;
	    }
		public void onServiceConnected(ComponentName name, IBinder service) {
	        mBounded = true;
	        LocalBinder mLocalBinder = (LocalBinder)service;
	        noticeAnyTimeService = mLocalBinder.getServerInstance();
	    }
	};
	
	@Override
	protected void onStart() {
	    super.onStart();
	    Intent mIntent = new Intent(this, NoticeAnyTimeService.class);
	    bindService(mIntent, mConnection, BIND_AUTO_CREATE);
	};
	
	@Override
	protected void onStop() {
	    super.onStop();
	    if(mBounded) {
	        unbindService(mConnection);
	        mBounded = false;
	    }
	};
}
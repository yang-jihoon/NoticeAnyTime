package com.psalm0105.noticeAnyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
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
import android.widget.Toast;

import com.psalm0105.noticeAnyTime.NoticeAnyTimeService.LocalBinder;

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

        if (mCursor.getCount() == 0) {
	        Toast.makeText(context, R.string.rule_list_toast_text, Toast.LENGTH_LONG).show();
        }

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
		setSnoozeBtnColor(0,R.id.title_stop);

        Button snooze1HourBtn = (Button) findViewById(R.id.title_snooze1);
        snooze1HourBtn.setOnClickListener(new BtnClickListener());
        setSnoozeBtnColor(1,R.id.title_snooze1);

        Button snooze2HourBtn = (Button) findViewById(R.id.title_snooze2);
        snooze2HourBtn.setOnClickListener(new BtnClickListener());
        setSnoozeBtnColor(2,R.id.title_snooze2);

        Button snooze3HourBtn = (Button) findViewById(R.id.title_snooze3);
        snooze3HourBtn.setOnClickListener(new BtnClickListener());
        setSnoozeBtnColor(3,R.id.title_snooze3);



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
		menu.add(0, Menu.FIRST, Menu.NONE, "Delete");
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
			Resources res = getResources();
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
	            notificationManager.cancel("NAT", NOTICE_TYPE.ALARM.getTypeNum());
	            notificationManager.cancel("NAT", NOTICE_TYPE.SNOOZE.getTypeNum());

	            setSnoozeTimeAndBtnColor(0);

	            noticeAnyTimeService.stopAlarm();
			} else if (view.getId() == R.id.title_snooze1) {

				setSnoozeTimeAndBtnColor(1);

			} else if (view.getId() == R.id.title_snooze2) {

				setSnoozeTimeAndBtnColor(2);

			} else if (view.getId() == R.id.title_snooze3) {

				setSnoozeTimeAndBtnColor(3);

			}
		}

	}

	private void setSnoozeBtnColor(int type,int titleSnooze) {
	    SharedPreferences snoozePref = context.getSharedPreferences("SNOOZE_TIME", Context.MODE_PRIVATE);
	    int snoozeType = snoozePref.getInt("TYPE", 0);
	    long snoozeLimitTime = snoozePref.getLong("TIME", 0);

	    long currentTime = (new Date()).getTime();

        Button snoozeBtn = (Button) findViewById(titleSnooze);
		Button stopBtn = (Button) findViewById(R.id.title_stop);

		if (snoozeType == 0) {
			stopBtn.setText(R.string.btn_stop);
		} else {
			Resources res = getResources();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.KOREA);
			String snoozeDate = sdf.format(snoozeLimitTime);

			stopBtn.setText(res.getString(R.string.btn_snooze_cancel) + " (" + snoozeDate + ")");

		}

		if (snoozeType == type && snoozeLimitTime > currentTime) {
			snoozeBtn.setBackgroundColor(Color.WHITE);
			snoozeBtn.setTextColor(Color.RED);
		} else {
			snoozeBtn.setBackgroundColor(Color.LTGRAY);
			snoozeBtn.setTextColor(Color.BLACK);
		}
	}

	private void setSnoozeTimeAndBtnColor(int snoozeTime) {
		Calendar snoozeLimitTime = Calendar.getInstance();
		snoozeLimitTime.setTime(new Date());
		snoozeLimitTime.add(Calendar.HOUR, snoozeTime);

    	SharedPreferences prefs = getSharedPreferences("SNOOZE_TIME", Context.MODE_PRIVATE);
    	Editor ed = prefs.edit();
    	ed.putLong("TIME", snoozeLimitTime.getTime().getTime());
    	ed.putInt("TYPE", snoozeTime);
    	ed.commit();
    	Log.d("SNOOZE", "SNOOZE_TIME"+snoozeTime+" : "+snoozeLimitTime.getTime().toString());

    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel("NAT", NOTICE_TYPE.SNOOZE.getTypeNum());

        Button stopBtn = (Button) findViewById(R.id.title_stop);

		Resources res = getResources();

    	if (snoozeTime > 0) {

	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.KOREA);
	        String snoozeDate = sdf.format(snoozeLimitTime.getTime());

    		stopBtn.setText(res.getString(R.string.btn_snooze_cancel) + " (" + snoozeDate + ")");

	    	Notification notification = new Notification();
	        notification.icon = R.drawable.ic_launcher;
	        notification.defaults = Notification.DEFAULT_LIGHTS;
	        notification.when = System.currentTimeMillis();
	        notification.tickerText = "Snooze "+snoozeTime+"시간 - " + res.getString(R.string.app_name);
			notification.flags = Notification.FLAG_NO_CLEAR;

	        Intent intentForNotify = new Intent(context, RuleListActivity.class);
	        intentForNotify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intentForNotify.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	        intentForNotify.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        intentForNotify.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        intentForNotify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intentForNotify, PendingIntent.FLAG_UPDATE_CURRENT);
	        notification.setLatestEventInfo(context, "Snooze " + snoozeTime + "시간 - " + res.getString(R.string.app_name), snoozeDate + "까지 일시중지", contentIntent);

	        notificationManager.notify("NAT", NOTICE_TYPE.SNOOZE.getTypeNum(), notification);

			Toast.makeText(RuleListActivity.this, snoozeTime+"시간"+res.getString(R.string.snooze_setting_toast_text), Toast.LENGTH_SHORT).show();
    	} else {
    		stopBtn.setText(R.string.btn_stop);
    	}

		setSnoozeBtnColor(0,R.id.title_stop);
		setSnoozeBtnColor(1,R.id.title_snooze1);
		setSnoozeBtnColor(2,R.id.title_snooze2);
		setSnoozeBtnColor(3, R.id.title_snooze3);

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
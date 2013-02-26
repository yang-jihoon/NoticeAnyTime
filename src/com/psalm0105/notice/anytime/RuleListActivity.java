package com.psalm0105.notice.anytime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class RuleListActivity extends Activity {
	
	public static Context context;
	private Cursor mCursor = null;
	private DatabaseAdapter databaseAdapter;
    
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
        listview.setDivider(new ColorDrawable(Color.GRAY));
        listview.setDividerHeight(1);
        listview.setAdapter(new ListAdapter(this, mCursor));
        
        listview.setOnItemClickListener(new ListViewItemClickListener());
        
        Button addBtgn = (Button) findViewById(R.id.title_addrule);
        addBtgn.setOnClickListener(new AddBtnClickListener());

        // Start the service        
//        ServiceManager serviceManager = new ServiceManager(this);
//        serviceManager.setNotificationIcon(R.drawable.notification);
//        serviceManager.startService();      

    }
	
	public class AddBtnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(RuleListActivity.this, RuleSettingActivity.class);
			startActivity(intent);
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

		databaseAdapter.close();
		mCursor.close();
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		startActivity(new Intent(this, SettingActivity.class));
		return (true);
    }
}
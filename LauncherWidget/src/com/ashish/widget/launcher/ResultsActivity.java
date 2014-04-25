package com.ashish.widget.launcher;

import java.util.Vector;

import com.ashish.widget.launcher.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ResultsActivity extends Activity implements OnItemClickListener {

	private Vector<AppInfo> apps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.results_list_layout);

		apps = Constants.getAppsToLaunch();
		
		TextView noMatchFound = (TextView) findViewById(R.id.no_match_found);
		ListView resultsList = (ListView) findViewById(R.id.resuts_list);
		if (apps.size() > 0) {
			
			if (apps.size() > 1) {
				noMatchFound.setVisibility(View.GONE);

				AppListAdapter adapter = new AppListAdapter(this, apps);
				resultsList.setAdapter(adapter);
				resultsList.setOnItemClickListener(this);
			} else {
				startActivity(((AppInfo) apps.get(0)).getLaunchIntent());
				finish();
			}
		} else {
			resultsList.setVisibility(View.GONE);
		}
	}
	

	class AppListAdapter extends BaseAdapter {
		
		Vector<AppInfo> appListVector;
		Context context;
		
		public AppListAdapter(Context context, Vector<AppInfo> appListVector) {
			this.context = context;
			this.appListVector = appListVector;
			if (appListVector == null) {
				appListVector = new Vector<AppInfo>();
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appListVector.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return appListVector.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.results_item_layout, null);
			}
			AppInfo info = (AppInfo) appListVector.get(position);
			ImageView iv = (ImageView) convertView.findViewById(R.id.app_icon_id);
			iv.setImageDrawable(info.getActivityIcon());
			TextView tv = (TextView) convertView.findViewById(R.id.app_name_id);
			tv.setText(info.getActivityLabel());
			return convertView;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent launchIntent = ((AppInfo) apps.get(arg2)).getLaunchIntent();
//		launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(launchIntent);
		finish();
	}
	
}

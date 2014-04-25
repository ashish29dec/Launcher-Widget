package com.ashish.widget.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.ashish.widget.launcher.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.RemoteViews;

public class LauncherWidgetProvider extends AppWidgetProvider {

	public static final String TAG = LauncherWidgetProvider.class.getName();
	
	SpeechRecognizer recognizer;
//	List<ApplicationInfo> appInfoList;
//
//	// HashMap storing the applications installed in the app
//	HashMap<String, AppInfo> applicationMap;
//	AppInfo[] masterList;
//	AppInfo[] clonedList;
	
	// Array of all the Activities that can be launched from the launcher
	AppInfo[] installedApps;
	AppInfo[] clonedInstalledApps;
	
	// HashMap that stores the map between the words of the labels and the index of AppInfo from the installedApps array
	HashMap<String, Vector<Integer>> labelWordInstalledAppsIndexMap;
	
//	HashMap<String, ArrayList<Integer>> labelIndexMap;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(TAG, "onUpdate");
		final int N = appWidgetIds.length;
		
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			
			Intent intent = new Intent(Constants.ACTION_START_RECORDING);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			views.setOnClickPendingIntent(R.id.btn1, pendingIntent);
			
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
	@Override
	public void onEnabled(Context context) {
		Log.i(TAG, "onEnabled");
		
		PackageManager pm = context.getPackageManager();
		Log.i(TAG, "PackageName: " + context.getPackageName());
		ComponentName componentName = new ComponentName("com.ashish.widget.launcher", "com.ashish.widget.launcher.receiver.RecordEventReceiver");
		Log.i(TAG, "componentName: " + componentName);
		pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		
//        applicationMap = new HashMap<String, AppInfo>();
//        labelIndexMap = new HashMap<String, ArrayList<Integer>>();
		
		// Preparing the list of applications
//        PackageManager pm = context.getPackageManager();
//        Intent launcher_intent = new Intent(Intent.ACTION_MAIN);
//        launcher_intent.addCategory(Intent.CATEGORY_LAUNCHER);
	}
	
	@Override
	public void onDisabled(Context context) {
		Log.i(TAG, "onDisabled");
		
		PackageManager pm = context.getPackageManager();
		ComponentName componentName = new ComponentName("com.demo.appwidgetdemo", "com.demo.appwidgetdemo.appwidget.ExampleBroadcastReceiver");
		pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		
    	if (recognizer != null) {
    		recognizer.destroy();
    		recognizer = null;
    	}
	}
}

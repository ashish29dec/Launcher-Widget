package com.ashish.widget.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfo {

	private String appLabel;
	private String activityLabel;
	private Drawable appIcon;
	private Drawable activityIcon;
	private Intent launchIntent;
	private String activityClassName;
	private String packageName;
	
	private boolean shown;
	
	public AppInfo() {
		this.shown = false;
	}
	
//	public AppInfo(String label, Drawable icon) {
//		this();
//		this.appLabel = label;
//		this.appIcon = icon;
//		this.launchIntent = intent;
//		setLaunchPackage();
//	}
	
//	private void setLaunchPackage() {
//		if (launchIntent != null) {
//			ComponentName cn = launchIntent.getComponent();
//			launchClassName = cn.getClassName();
//			packageName = cn.getPackageName();
//		} else {
//			launchClassName = null;
//			packageName = null;
//		}
//	}

	public Drawable getAppIcon() {
		return appIcon;
	}
	
	public String getAppLabel() {
		return appLabel;
	}
	
//	public String getLaunchClassName() {
//		return launchClassName;
//	}
//	
	public Intent getLaunchIntent() {
		
		return launchIntent;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getActivityLabel() {
		return activityLabel;
	}
	
	public Drawable getActivityIcon() {
		return activityIcon;
	}
	
	public String getActivityClassName() {
		return activityClassName;
	}
	
	public void setActivityClassAndPackage(String className, String packageName) {
		this.activityClassName = className;
		this.packageName = packageName;
		
		if (!className.isEmpty() && !packageName.isEmpty()) {
			ComponentName cn = new ComponentName(packageName, activityClassName);
			Intent startIntent = new Intent(Intent.ACTION_MAIN);
			startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startIntent.setComponent(cn);
			startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			setLaunchIntent(startIntent);
		}
	}
	
//	public void setActivityClassName(String activityClassName) {
//		this.activityClassName = activityClassName;
//	}
//	
	public void setActivityIcon(Drawable activityIcon) {
		this.activityIcon = activityIcon;
	}
	
	public void setActivityLabel(String activityLabel) {
		this.activityLabel = activityLabel;
	}
	
//	public void setPackageName(String packageName) {
//		this.packageName = packageName;
//	}
//	
	public boolean isShown() {
		return shown;
	}
	
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	
	public void setAppLabel(String appLabel) {
		this.appLabel = appLabel;
	}
	
//	public void setLaunchClassName(String launchClassName) {
//		this.launchClassName = launchClassName;
//	}
//	
	public void setLaunchIntent(Intent launchIntent) {
		this.launchIntent = launchIntent;
//		setLaunchPackage();
	}
//	
//	public void setPackageName(String packageName) {
//		this.packageName = packageName;
//	}
	
	public void setShown(boolean shown) {
		this.shown = shown;
	}
}

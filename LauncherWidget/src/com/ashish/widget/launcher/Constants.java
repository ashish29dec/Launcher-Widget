package com.ashish.widget.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class Constants {

	public static final String ACTION_START_RECORDING = "com.ashish.widget.launcher.intent.ACTION_START_RECORDING";
	
	private static SpeechRecognizer recognizer;
	private static RecognitionListener listener;
	
	private static HashMap<String, Vector<Integer>> labelWordInstalledAppsIndexMap;
	private static AppInfo[] clonedInstalledApps;
	private static Vector<AppInfo> appsToLaunch;
	
	private static Intent launcherIntent;
	
	public static void setRecognizer(SpeechRecognizer recognizer) {
		Constants.recognizer = recognizer;
	}
	
	public static SpeechRecognizer getRecognizer() {
		return recognizer;
	}
	
	public static void setLabelWordInstalledAppsIndexMap(
			HashMap<String, Vector<Integer>> labelWordInstalledAppsIndexMap) {
		Constants.labelWordInstalledAppsIndexMap = labelWordInstalledAppsIndexMap;
	}

	public static HashMap<String, Vector<Integer>> getLabelWordInstalledAppsIndexMap() {
		return labelWordInstalledAppsIndexMap;
	}
	
	public static void setClonedInstalledApps(AppInfo[] clonedInstalledApps) {
		Constants.clonedInstalledApps = clonedInstalledApps;
	}

	public static AppInfo[] getClonedInstalledApps() {
		return clonedInstalledApps;
	}
	
	public static void setListener(RecognitionListener listener) {
		Constants.listener = listener;
	}
	
	public static RecognitionListener getListener() {
		return listener;
	}
	
	public static Vector<AppInfo> getAppsToLaunch() {
		return appsToLaunch;
	}
	
	public static void setAppsToLaunch(Vector<AppInfo> appsToLaunch) {
		Constants.appsToLaunch = appsToLaunch;
	}
	
	public static Intent getSpeechRecognizerIntent() {
		Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        return recognizerIntent;
	}
	
	public static Intent getLauncherIntent() {
		if (launcherIntent == null) {
			launcherIntent = new Intent(Intent.ACTION_MAIN);
			launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		}
		return launcherIntent;
	}
}

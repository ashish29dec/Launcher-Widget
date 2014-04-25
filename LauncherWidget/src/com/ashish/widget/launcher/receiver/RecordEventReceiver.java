package com.ashish.widget.launcher.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.ashish.widget.launcher.AppInfo;
import com.ashish.widget.launcher.Constants;
import com.ashish.widget.launcher.ResultsActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.ViewDebug.FlagToString;
import android.widget.BaseAdapter;

public class RecordEventReceiver extends BroadcastReceiver implements RecognitionListener {

	public static final String TAG = RecordEventReceiver.class.getName();
	
	SpeechRecognizer recognizer;
//	Intent recognizerIntent;
	Context applicationContext;
	
	// contains the matched apps that will be shown to the user
	Vector<AppInfo> matchedApps;

	HashMap<String, Vector<Integer>> labelWordInstalledAppsIndexMap;
	AppInfo[] clonedInstalledApps;
	
	// Also need to take care of a case where user may press button twice in a quick succession
	// I do not want to create app database twice in this scenario
	// If thread creating app database is already running or finished running, I should use the database it has created
	private volatile boolean loadingStarted;
	
	private long startTime;

	/*
	private DialogInterface.OnClickListener appClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.i(TAG, "which: " + which);
			startActivity(((AppInfo) appsToLaunch.get(which)).getLaunchIntent());
		}
	};
	*/
	
	public RecordEventReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		startTime = System.currentTimeMillis();
		
		applicationContext = context.getApplicationContext();
		Log.i(TAG, "onReceive: " + intent.getAction());
		String action = intent.getAction();
		if (action.equalsIgnoreCase(Constants.ACTION_START_RECORDING)) {
			// Start a new thread that will create the data-structure to hold all the apps installed
			if (!loadingStarted && (Constants.getClonedInstalledApps() == null || Constants.getLabelWordInstalledAppsIndexMap() == null)) {
				// Start a new thread to load applications that can be launched
				Log.i(TAG, "Starting a separate thread to load the applications that can be launched");
				loadingStarted = true;
				new ApplicationInfoLoader().start();
			}
			recognizer = Constants.getRecognizer();
			if (recognizer == null) {
				Log.e(TAG, "Recognizer is null. Creating recognizer again");
				recognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext);
				Constants.setRecognizer(recognizer);
			}
			if (recognizer != null) {
				recognizer.setRecognitionListener(this);
				Log.i(TAG, "Starting Listening");
				try {
					recognizer.startListening(Constants.getSpeechRecognizerIntent());
				} catch (ActivityNotFoundException e) {
					Log.e(TAG, "Activity Not found that can handle speech recognition", e);
				}
			} else {
				Log.e(TAG, "Recognizer is null even after re-creating it");
			}
		}
	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onBeginningOfSpeech");
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onBufferReceived: " + new String(buffer));		
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onEndOfSpeech");
		recognizer.stopListening();
	}

	@Override
	public void onError(int error) {
		// TODO Auto-generated method stub
		String msg = "";
		switch (error) {
		case SpeechRecognizer.ERROR_AUDIO:
			msg = "ERROR_AUDIO";
			break;
		case SpeechRecognizer.ERROR_CLIENT:
			msg = "ERROR_CLIENT";
			break;
		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
			msg = "ERROR_INSUFFICIENT_PERMISSIONS";
			break;
		case SpeechRecognizer.ERROR_NETWORK:
			msg = "ERROR_NETWORK";
			break;
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
			msg = "ERROR_NETWORK_TIMEOUT";
			break;
		case SpeechRecognizer.ERROR_NO_MATCH:
			msg = "ERROR_NO_MATCH";
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			msg = "ERROR_RECOGNIZER_BUSY";
			break;
		case SpeechRecognizer.ERROR_SERVER:
			msg="ERROR_SERVER";
			break;
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			msg = "ERROR_SPEECH_TIMEOUT";
			break;
		}
		Log.i(TAG, "onError: " + msg);
	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onEvent: eventType = " + eventType + ", params = " + params);
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onPartialResults: results = " + partialResults);
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onReadyForSpeech: " + params);
	}

	@Override
	public void onResults(Bundle results) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Time it took to get the results of speech: " + (System.currentTimeMillis() - startTime));
		Log.i(TAG, "onResults: " + results.toString());
		
//		labelIndexMap = ExampleWidgetConstants.getLabelIndexMap();
//		clonedList = ExampleWidgetConstants.getClonedList();
		
		ArrayList<String> resultsList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		Log.i(TAG, "Num results: " + resultsList.size());
//		TextView resultView = (TextView) findViewById(R.id.recognized_text); 
		String text = "";
		boolean matchFound = false;
		if (matchedApps == null) {
			matchedApps = new Vector<AppInfo>();
		}
		matchedApps.removeAllElements();
		
		while((Constants.getLabelWordInstalledAppsIndexMap() == null || Constants.getClonedInstalledApps() == null) && loadingStarted) {
			try {
				Thread.sleep(200);
			} catch (Throwable t) {
				Log.e(TAG, "Error in sleeping main thread", t);
			}
		}
		
		startTime = System.currentTimeMillis();
		
		labelWordInstalledAppsIndexMap = Constants.getLabelWordInstalledAppsIndexMap();
		clonedInstalledApps = Constants.getClonedInstalledApps();
		int numResults = resultsList.size();
		for (int i = 0; i < numResults; i++) {
			Log.i(TAG, "Result[" + i + "]: " + resultsList.get(i));
//			text += ", " + resultsList.get(i);
			String result = resultsList.get(i).toLowerCase();
			if (!result.isEmpty()) {
				String[] words = result.split(" ");
				int numWords = words.length;
				for (int wordIndex = 0; wordIndex < numWords; wordIndex++) {
					String word = words[wordIndex];
					if (labelWordInstalledAppsIndexMap.containsKey(word)) {
						matchFound = true;
						Vector<Integer> indexList = labelWordInstalledAppsIndexMap.get(word);
						int numIndex = indexList.size();
						for (int index = 0; index < numIndex; index++) {
							AppInfo matchedInfo = clonedInstalledApps[indexList.get(index)];
							if (!matchedInfo.isShown()) {
								matchedInfo.setShown(true);
								matchedApps.addElement(matchedInfo);
							}
							Log.i(TAG, "Matched app: " + clonedInstalledApps[indexList.get(index)].getAppLabel());
						}
					}
				}
			}
		}
		
		for (int finishIndex = 0; finishIndex < clonedInstalledApps.length; finishIndex++) {
			clonedInstalledApps[finishIndex].setShown(false);
		}

		Constants.setAppsToLaunch(matchedApps);
		Intent activityIntent = new Intent(applicationContext, ResultsActivity.class);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		applicationContext.startActivity(activityIntent);
		Log.i(TAG, "Time it took to process results and come up with a matching apps: " + (System.currentTimeMillis() - startTime));
		
		/*
		if (appsToLaunch.size() > 0) {
			int cloneSize = clonedList.length;
			for (int cloneIndex = 0; cloneIndex < cloneSize; cloneIndex++) {
				if (clonedList[cloneIndex].isShown()) {
					text += ", " + clonedList[cloneIndex].getAppLabel();
				}
			}
		} else {
			text = "No matches found";
		}
		
		Log.i(TAG, "Results: " + text);
//		resultView.setText(text);
 
		 */
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onRmsChanged: " + rmsdB);
	}
	
	
	class ApplicationInfoLoader extends Thread {
		
		@Override
		public void run() {
			AppInfo[] installedApps;
			AppInfo[] clonedInstalledApps;
			PackageManager pm = applicationContext.getPackageManager();
	        // Getting only those activities that can be launched from the launcher
	        List<ResolveInfo> resolved_activities_list = pm.queryIntentActivities(Constants.getLauncherIntent(), 0);
	        Log.i(TAG, "-------------------------------------------------------------");
	        Log.i(TAG, "Num launchable apps: " + resolved_activities_list.size());
	        int num_resolved_activites = resolved_activities_list.size();
	        
	        installedApps = new AppInfo[num_resolved_activites];
	        labelWordInstalledAppsIndexMap = new HashMap<String, Vector<Integer>>();

	        Log.i(TAG, "Preparing Launchable App List");
	        for (int j = 0; j < num_resolved_activites; j++) {
	        	Log.i(TAG, "***************");
	        	Log.i(TAG, "App Number " + (j+1));
	        	ResolveInfo ri = resolved_activities_list.get(j);
	        	ActivityInfo ai = ri.activityInfo;
//	        	Log.i(TAG, "Activity flags: " + ai.flags);
//	        	Log.i(TAG, "Activity launchMode: " + ai.launchMode);
//	        	Log.i(TAG, "Activity parentActivityName: " + ai.parentActivityName);
//	        	Log.i(TAG, "Activity permission: " + ai.permission);
//	        	Log.i(TAG, "Activity targetActivity: " + ai.targetActivity);
	        	Log.i(TAG, "Activity Label: " + ai.loadLabel(pm));
	        	Log.i(TAG, "Activity Icon: " + ai.loadIcon(pm));
	        	Log.i(TAG, "Activity Logo: " + ai.loadLogo(pm));
	        	IntentFilter filter = ri.filter;
	        	if (filter != null) {
	        		for (int actionIndex = 0; actionIndex < filter.countActions(); actionIndex++) {
	        			Log.i(TAG, "IntentFilter Action: " + filter.getAction(actionIndex));
	        		}
	        		for (int categoryIndex = 0; categoryIndex < filter.countCategories(); categoryIndex++) {
	        			Log.i(TAG, "IntentFilter Category: " + filter.getAction(categoryIndex));
	        		}
	        	}
	        	ApplicationInfo api = ai.applicationInfo;
	        	Log.i(TAG, "App Label: " + pm.getApplicationLabel(api));
	        	Log.i(TAG, "App package name: " + api.packageName);
	        	Intent intent = pm.getLaunchIntentForPackage(api.packageName);
	        	if (intent != null) {
	        		String action = intent.getAction();
	            	Log.i(TAG, "App intent launch action: " + action);
	            	Set<String> categorySet = intent.getCategories();
	            	if (categorySet != null && categorySet.size() > 0) {
	                	String[] categories = new String[categorySet.size()];
	                	categories = categorySet.toArray(categories);
	                	for (int cat = 0; cat < categories.length; cat++) {
	                    	Log.i(TAG, "App categories: " + categories[cat]);
	                	}
	            	}
	        		ComponentName component = intent.getComponent();
	        		String className = component.getClassName();
	            	Log.i(TAG, "App intent launch class name: " + className);
	        		String packageName = component.getPackageName();
	            	Log.i(TAG, "App intent launch package name: " + packageName);
//	            	startActivity(intent);
//	            	break;
	        	}
	        	
	        	String label = pm.getApplicationLabel(api).toString();
	        	AppInfo info = new AppInfo();
	        	info.setAppLabel(label);
	        	String activity_label = ai.loadLabel(pm).toString();
	        	info.setActivityLabel(activity_label);
	        	info.setActivityIcon(ai.loadIcon(pm));
	        	info.setActivityClassAndPackage(ai.name, api.packageName);
	        	info.setAppIcon(pm.getApplicationIcon(api));

	        	installedApps[j] = info;

	        	// Populate the master map of label and appinfo index
	        	String[] words = activity_label.split(" ");
	        	int numWordsInLabel = words.length;
	        	
	        	for (int wordIndex = 0; wordIndex < numWordsInLabel; wordIndex++) {
	        		String word = words[wordIndex].toLowerCase();
	        		if (labelWordInstalledAppsIndexMap.containsKey(word)) {
	        			Vector<Integer> value = labelWordInstalledAppsIndexMap.get(word);
	        			value.add(j);
	        		} else {
	        			Vector<Integer> value = new Vector<Integer>();
	        			value.add(j);
	        			labelWordInstalledAppsIndexMap.put(word, value);
	        		}
	        	}
//	        	
//	        	if (!applicationMap.containsKey(label.toLowerCase())) {
//	        		applicationMap.put(label.toLowerCase(), info);
//	        	}
	        }
	        
	    	clonedInstalledApps = installedApps.clone();

//	        masterList = new AppInfo[applicationMap.size()];
//	        masterList = applicationMap.values().toArray(masterList);
//	        clonedList = masterList.clone();
//	        
//	        int size = clonedList.length;
////	        size = clonedList.length;
//	        for (int index = 0; index < size; index++) {
//	        	AppInfo ai = clonedList[index];
//	        	String label = ai.getAppLabel().toLowerCase();
//	        	if (!label.isEmpty()) {
//	        		String[] words = label.split(" ");
//	        		int numWords = words.length;
//	        		for (int wordIndex = 0; wordIndex < numWords; wordIndex++) {
//	        			String word = words[wordIndex];
//	        			if (labelIndexMap.containsKey(word)) {
//	        				ArrayList<Integer> indexList = labelIndexMap.get(word);
//	        				indexList.add(index);
//	        			} else {
//	        				ArrayList<Integer> indexList = new ArrayList<Integer>();
//	        				indexList.add(index);
//	        				labelIndexMap.put(word, indexList);
//	        			}
//	        		}
//	        	}
//	        }
	        
//	        recognizer = SpeechRecognizer.createSpeechRecognizer(context.getApplicationContext());
//	        recognizer.setRecognitionListener(new ExampleBroadcastReceiver(recognizer, labelIndexMap, clonedList));

	        Constants.setClonedInstalledApps(clonedInstalledApps);
	        Constants.setLabelWordInstalledAppsIndexMap(labelWordInstalledAppsIndexMap);
//	        Constants.setRecognizer(recognizer);
	        loadingStarted = false;
	        Log.i(TAG, "Time it took to create the database of apps: " + (System.currentTimeMillis() - startTime));
		}
	}
}

package com.ashish.widget.launcher;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(
		formKey = "",
		formUri = "http://www.mail.yahoo.com", 
		formUriBasicAuthLogin = "ashish_29_dec",
		formUriBasicAuthPassword = "FC30@lajpat"
)
public class LauncherApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		// The following line triggers the initialization of ACRA
        ACRA.init(this);
	}
}

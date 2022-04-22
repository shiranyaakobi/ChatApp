package com.example;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ChatApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize firebase
        FirebaseApp.initializeApp(this);
        // Set Crash analytics
        FirebaseCrashlytics.getInstance()
                .setCrashlyticsCollectionEnabled(true);

    }
}

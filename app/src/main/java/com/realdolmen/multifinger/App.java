package com.realdolmen.multifinger;

import android.app.Application;

import com.realdolmen.multifinger.config.GuiceConfig;

import roboguice.RoboGuice;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new GuiceConfig());
    }
}

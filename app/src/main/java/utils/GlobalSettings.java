package utils;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import epimelis.com.lyre.MainActivity;
import parse.Alarms;

/**
 * Created by ammonrees on 6/21/14.
 */
public class GlobalSettings extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // do the ACRA init here

        //
        Parse.initialize(getApplicationContext(), "G6cUhIyG0s9DXmU3vDGBi1lXl3fePm1DdSGcRHCV", "EU94EckzH5qlu0GuuJercuxgs4xch5AX3jmExI84");

        ParseFacebookUtils.initialize("798958860124500");

        ParseObject.registerSubclass(Alarms.class);
        PushService.setDefaultPushCallback(this, MainActivity.class);

    }

}


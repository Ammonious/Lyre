package alarm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.RoundedImageView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import epimelis.com.lyre.R;
import slider.SlideToUnlock;
import utils.ImageLoader;

/**
 * Created by ammonrees on 9/20/14.
 */
public class AlarmScreen extends Activity implements SlideToUnlock.OnUnlockListener {

    public final String TAG = "AlarmScreenActivity";
    private PowerManager.WakeLock mWakeLock;
    private MediaPlayer mPlayer;
    private static final int WAKELOCK_TIMEOUT = 60 * 1000;
    SlideToUnlock slideToUnlock;
    public TextView fName,AM_PM_VIEW,friendMessage,snoozeText;
    LinearLayout snooze;
    RoundedImageView Profile;
    public static int snoozeMinutes = 9;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup layout
        this.setContentView(R.layout.alarm_screen);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Regular.ttf");
        slideToUnlock = (SlideToUnlock) findViewById(R.id.slidetounlock);
        slideToUnlock.setOnUnlockListener(this);

        String name = getIntent().getStringExtra(AlarmManagerHelper.NAME);
        int timeHour = getIntent().getIntExtra(AlarmManagerHelper.TIME_HOUR, 0);
        int timeMinute = getIntent().getIntExtra(AlarmManagerHelper.TIME_MINUTE, 0);
        long alarmID = getIntent().getLongExtra(AlarmManagerHelper.ID, 0);
        imageLoader = new ImageLoader(getBaseContext());


        fName = (TextView) findViewById(R.id.alarm_screen_friend);
        fName.setTypeface(tf);

        TextView tvTime = (TextView) findViewById(R.id.alarm_screen_time);
        tvTime.setTypeface(tf);

        AM_PM_VIEW = (TextView) findViewById(R.id.alarm_screen_am_pm);
        AM_PM_VIEW.setTypeface(tf);

        friendMessage = (TextView) findViewById(R.id.message_content);

        snoozeText = (TextView) findViewById(R.id.snooze_text);
        snoozeText.setTypeface(tf);


        snooze = (LinearLayout) findViewById(R.id.snooze);
        snooze.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            snoozeAlarm();

                Toast.makeText(getApplicationContext(), "Alarm Snoozed for 9 Mintues!",
                        Toast.LENGTH_SHORT).show();

            }
        });
//******** Set the current time of day on Alarm Screen *********//

        Calendar currentTime = Calendar.getInstance();
        int hours = currentTime.get(Calendar.HOUR_OF_DAY);
        int minutes = currentTime.get(Calendar.MINUTE);
        String am_pms = "";
        if (currentTime.get(Calendar.AM_PM) == Calendar.AM)
            am_pms = "AM";
        else if (currentTime.get(Calendar.AM_PM) == Calendar.PM)
            am_pms = "PM";
        if(hours > 12) {
            hours -= 12;
            tvTime.setText(String.format("%1d:%02d", hours, minutes));
        } else if(hours == 0) {
            hours += 12;
            tvTime.setText(String.format("%1d:%02d",hours,minutes));
        } else {
            tvTime.setText(String.format("%1d:%02d",hours,minutes));
        }

        AM_PM_VIEW.setText(am_pms);




//******** Query Friends name and Facebook ID for Pic and Name on Alarm Screen *********//

        Profile = (RoundedImageView) findViewById(R.id.alarm_screen_pic);
        String FbId = ParseUser.getCurrentUser().get("fbId").toString();


        ParseQuery<ParseObject> Namequery = new  ParseQuery<ParseObject>("Alarms");
        Namequery.whereEqualTo("facebookId",FbId);
        Namequery.whereEqualTo("alarm_id", alarmID);
        Namequery.whereExists("soundfile");
        Namequery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> alarmInfo, ParseException e) {
                if (e == null) {

                    if (alarmInfo.size() > 0) {
                        ParseObject alarmID = alarmInfo.get(0);

                        String Name = alarmID.get("name").toString();
                        final String fID = alarmID.get("friendId").toString();
                        fName.setText(Name);
                        String url = String.format(
                                "https://graph.facebook.com/%s/picture?width=150&height=150",fID);

                        imageLoader.DisplayImage(url, Profile);


                    }
                }
            }
        });


//******** Query Alarm that friends have set and play it if available  *********//


    ParseQuery<ParseObject> query = new  ParseQuery<ParseObject>("Alarms");
    query.whereEqualTo("facebookId",FbId);
    query.whereEqualTo("alarm_id", alarmID);
    query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> alarm, ParseException e) {
            if (e == null) {

                if (alarm.size() == 0) {

                    mPlayer = new MediaPlayer();
                    String tone = getIntent().getStringExtra(AlarmManagerHelper.TONE);
                    try {
                        if (tone != null && !tone.equals("")) {
                            Uri toneUri = Uri.parse(tone);
                            if (toneUri != null) {
                                mPlayer.setDataSource(AlarmScreen.this, toneUri);
                                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                                mPlayer.setLooping(true);
                                mPlayer.prepare();
                                mPlayer.start();
                            }
                        }
                    } catch (Exception t) {
                        e.printStackTrace();
                    }

                } else if (alarm.size() > 0) {
                    ParseObject alarmCheck = alarm.get(0);
                    alarmCheck.get("soundfile");

                    if (alarmCheck.get("soundfile") == null) {


                        mPlayer = new MediaPlayer();
                        String tone = getIntent().getStringExtra(AlarmManagerHelper.TONE);
                        try {
                            if (tone != null && !tone.equals("")) {
                                Uri toneUri = Uri.parse(tone);
                                if (toneUri != null) {
                                    mPlayer.setDataSource(AlarmScreen.this, toneUri);
                                    mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                                    mPlayer.setLooping(true);
                                    mPlayer.prepare();
                                    mPlayer.start();
                                }
                            }
                        } catch (Exception t) {
                            e.printStackTrace();
                        }


                } else {


                        ParseObject alarmTone = alarm.get(0);

                        // alarmTone.getParseFile("soundfile");
                        final String message = alarmTone.getString("message");
                        ParseFile parseFile = alarmTone.getParseFile("soundfile");


                        if (parseFile != null) {
                            parseFile.getDataInBackground(new GetDataCallback() {

                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {

                                        if (message != null) {
                                            friendMessage.setText(message);
                                            //Log.v("MAD","We've got data in data.");
                                        } else {


                                            friendMessage.setText("No Message Left");
                                        }
                                        File newAlarm = new File(Environment.getExternalStorageDirectory().
                                                getAbsolutePath(), "/friendsalarm.3gpp");
                                        mPlayer = new MediaPlayer();
                                        try {
                                            FileOutputStream fos = new FileOutputStream(newAlarm.getPath());
                                            fos.write(data);
                                            fos.flush();
                                            fos.close();

                                            Uri alarmUri = Uri.parse(Environment.getExternalStorageDirectory().
                                                    getAbsolutePath() + "/friendsalarm.3gpp");


                                            mPlayer.setDataSource(AlarmScreen.this, alarmUri);
                                            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                                            mPlayer.setLooping(true);
                                            mPlayer.prepare();
                                            mPlayer.start();

                                        } catch (FileNotFoundException e1) {
                                            e1.printStackTrace();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }

                                    } else {
                                        Log.d("MAD", " There was a problem downloading the data.");
                                    }
                                }
                            });
                        }


                }
            }
            }
        }
    });

        //Ensure wakelock release
        Runnable releaseWakelock = new Runnable() {

@Override
public void run() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        if (mWakeLock != null && mWakeLock.isHeld()) {
        mWakeLock.release();
        }
        }
        };

        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
        }

@SuppressWarnings("deprecation")
@Override
protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
        mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
        mWakeLock.acquire();
        Log.i(TAG, "Wakelock aquired!!");
        }

        }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
        mWakeLock.release();
        }
        }

    private void snoozeAlarm() {
        mPlayer.stop();
        createSnoozeAlarm();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        finish();
    }

    private void createSnoozeAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, snoozeMinutes);

        Intent intent = new Intent(this, AlarmService.class);
        intent.putExtra("alarmModel", getIntent().getLongExtra(AlarmManagerHelper.ID, 0));
        intent.putExtra("alarmTone", getIntent().getStringExtra(AlarmManagerHelper.TONE));

        PendingIntent pIntent = PendingIntent.getService(getBaseContext(), (int) getIntent().getLongExtra(AlarmManagerHelper.ID, 0), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManagerHelper.setAlarm(this, calendar, pIntent);
    }

    @Override
    public void onUnlock() {
        if(mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            deleteSound();
            finish();
            onStop();
        } else {

            finish();
            onStop();
        }
    }

    public void deleteSound(){

        Toast.makeText(getApplicationContext(), "Alarm Deleted",
                Toast.LENGTH_SHORT).show();

        String FbId = ParseUser.getCurrentUser().get("fbId").toString();
        long alarmID = getIntent().getLongExtra(AlarmManagerHelper.ID, 0);

        ParseQuery<ParseObject> deleteQuery = new  ParseQuery<ParseObject>("Alarms");
        deleteQuery.whereEqualTo("facebookId",FbId);
        deleteQuery.whereEqualTo("alarm_id", alarmID);
        deleteQuery.whereExists("soundfile");
        deleteQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> alarmInfo, ParseException e) {
                if (e == null) {

                    if (alarmInfo.size() > 0) {
                        ParseObject alarmID = alarmInfo.get(0);

                       alarmID.remove("soundfile");
                       alarmID.saveInBackground();

                    }
                }
            }
        });
    }
}
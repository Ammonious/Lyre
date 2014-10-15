package epimelis.com.lyre;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.makeramen.RoundedImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import adapters.AlarmAdapter;
import alarm.AlarmDBHelper;
import alarm.AlarmListAdapter;
import alarm.AlarmManagerHelper;
import alarm.AlarmModel;
import parse.Alarms;
import parse.Users;


public class FriendsAlarm extends Activity {


    private ParseUser mCurrentUser;
    AlarmAdapter mAdapter;
    RoundedImageView Profile;
    TextView Username;
    Button mButton;

    private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
    public ListView mListView;
    FloatingActionButton mFab;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_alarm);

        ParseObject.registerSubclass(Alarms.class);


        getActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.alarmlist);

        Typeface tf3 = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Roboto-Thin.ttf");

        Profile = (RoundedImageView) findViewById(R.id.userProfilePicture);

        Username = (TextView) findViewById(R.id.username);
        Username.setTypeface(tf3);
        Username.setText(getIntent().getExtras().getString("name"));

        mAdapter = new AlarmAdapter(getBaseContext(), new ArrayList<Alarms>());

        mListView.setAdapter(mAdapter);

        new AsyncTask<Void, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(Void... params)
            {
                // safety check
                if (ParseUser.getCurrentUser().get("fbId") == null)
                    return null;

                String url = String.format(
                        "https://graph.facebook.com/%s/picture?width=150&height=150",getIntent().getExtras().getString("picId"));

                // you'll need to wrap the two method calls
                // which follow in try-catch-finally blocks
                // and remember to close your input stream

                InputStream inputStream = null;
                try {
                    inputStream = new URL(url).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap)
            {
                // safety check
                if (bitmap != null);

                Profile.setImageBitmap(bitmap);

                // do what you need to do with the bitmap :)
            }
        }.execute();

        updateData();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateData(){
        ParseQuery<Alarms> query = ParseQuery.getQuery(Alarms.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.orderByDescending("createdAt");
        query.whereEqualTo("facebookId",getIntent().getExtras().getString("picId"));
        query.findInBackground(new FindCallback<Alarms>() {

            @Override
            public void done(List<Alarms> alarms, ParseException error) {
                if(alarms != null){
                    mAdapter.clear();
                    mAdapter.addAll(alarms);
                }
            }
        });
    }








}

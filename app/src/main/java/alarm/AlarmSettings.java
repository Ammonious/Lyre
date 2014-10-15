package alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
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
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;



import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import alarm.AlarmDetailsActivity;
import epimelis.com.lyre.R;
import parse.Alarms;
import parse.ParseConstants;
import parse.Users;


public class AlarmSettings extends Activity {


    private ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mAlarmsRelation;
    RoundedImageView Profile;
    TextView Username;
    Button mButton;
    private AlarmListAdapter mAdapter;
    private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
    public ListView mListView;
    FloatingActionButton mFab;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);



        mFab = (FloatingActionButton) findViewById(R.id.fabbutton);
        mFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                startAlarmDetailsActivity(-1);


                // Setup the transition to the detail activity
                //  ActivityOptions options =  ActivityOptions.makeSceneTransitionAnimation(FriendList.this, view, "photo" + i);
                //  startActivity(detailIntent, options.toBundle());

            }
        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.listView);

        Typeface tf3 = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Roboto-Thin.ttf");

        Profile = (RoundedImageView) findViewById(R.id.userProfilePicture);

        Username = (TextView) findViewById(R.id.username);
        Username.setTypeface(tf3);
        Username.setText(ParseUser.getCurrentUser().get("name").toString());

        mAdapter = new AlarmListAdapter(this, dbHelper.getAlarms());

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
                        "https://graph.facebook.com/%s/picture?width=150&height=150",ParseUser.getCurrentUser().get("fbId"));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mAdapter.setAlarms(dbHelper.getAlarms());
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setAlarmEnabled(long id, boolean isEnabled) {
        AlarmManagerHelper.cancelAlarms(this);

        AlarmModel model = dbHelper.getAlarm(id);
        model.isEnabled = isEnabled;
        dbHelper.updateAlarm(model);

        AlarmManagerHelper.setAlarms(this);
    }

    public void setAlarmVisible(long id, boolean isVisible) {


        AlarmModel model = dbHelper.getAlarm(id);
        model.isVisible = isVisible;
        dbHelper.updateAlarm(model);




    }



    public void startAlarmDetailsActivity(long id) {
        Intent intent = new Intent(this, AlarmDetailsActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 0);
    }

    public void deleteAlarm(long id) {
        final long alarmId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please confirm")
                .setTitle("Delete Alarm?")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel Alarms
                        AlarmManagerHelper.cancelAlarms(getBaseContext());
                        //Delete alarm from DB by id
                        dbHelper.deleteAlarm(alarmId);
                        //Refresh the list of the alarms in the adaptor
                        mAdapter.setAlarms(dbHelper.getAlarms());
                        //Notify the adapter the data has changed
                        mAdapter.notifyDataSetChanged();
                        //Set the alarms
                        AlarmManagerHelper.setAlarms(getBaseContext());
                    }
                }).show();
    }


}

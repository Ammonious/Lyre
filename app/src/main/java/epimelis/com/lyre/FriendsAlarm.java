package epimelis.com.lyre;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.devpaul.filepickerlibrary.FilePickerActivity;
import com.makeramen.RoundedImageView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.quentindommerc.superlistview.SuperListview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import adapters.FriendsAlarmAdapter;
import alarm.AlarmDBHelper;
import parse.Alarms;
import utils.BaseActivity;
import utils.ImageLoader;
import utils.MessageDialog;
import utils.RecordDialog;
import utils.TinyDB;


public class FriendsAlarm extends BaseActivity {

    ImageLoader imageLoader;
    FriendsAlarmAdapter mAdapter;
    RoundedImageView Profile;
    TextView Username;
    public static final String EXTRA_IMAGE = "FreindsAlarm:Profile";
    public static final String EXTRA_NAME = "FreindsAlarm:Username";
    public SuperListview mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ParseObject.registerSubclass(Alarms.class);
        String url = String.format(
                "https://graph.facebook.com/%s/picture?width=150&height=150",getIntent().getExtras().getString(EXTRA_IMAGE));


        imageLoader = new ImageLoader(this);
        mListView = (SuperListview) findViewById(R.id.alarmList);

        Typeface tf3 = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Thin.ttf");

        Profile = (RoundedImageView) findViewById(R.id.userProfilePicture);
        imageLoader.DisplayImage(url, Profile);
        ViewCompat.setTransitionName(Profile, EXTRA_IMAGE);
        Username = (TextView) findViewById(R.id.username);
        Username.setTypeface(tf3);
        Username.setText(getIntent().getExtras().getString(EXTRA_NAME));

        mAdapter = new FriendsAlarmAdapter(this, new ArrayList<Alarms>());

        mListView.setAdapter(mAdapter);


        updateData();


    }

    @Override
    protected int getLayoutResource() {

        return R.layout.friends_alarm;
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
        query.whereEqualTo("facebookId", getIntent().getExtras().getString(EXTRA_IMAGE));
        query.whereDoesNotExist("soundfile");
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

    public static void launch(BaseActivity activity, View transitionView, String url, String Username) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);
        Intent intent = new Intent(activity, FriendsAlarm.class);
        intent.putExtra(EXTRA_IMAGE, url);
        intent.putExtra(EXTRA_NAME, Username);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != FriendsAlarm.RESULT_CANCELED) {
            if (requestCode == FilePickerActivity.REQUEST_FILE && resultCode == RESULT_OK) {
                Toast.makeText(getBaseContext(), "Friend's Alarm Set Successfully!", Toast.LENGTH_LONG).show();

                TinyDB tinyDB = new TinyDB(getBaseContext());
                final String FbId = ParseUser.getCurrentUser().get("fbId").toString();
                final String username = ParseUser.getCurrentUser().get("name").toString();

               // THIS NEEDS TO BE FIXED!!!!!!!! need to getStringExtra instead of TinyDB
               // String objectId = data.getStringExtra("objectId");
                String objectId = tinyDB.getString("objectId");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Alarms");
                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                    public void done(ParseObject alarm, ParseException e) {
                        if (e == null) {

                            File file = new File(data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH));
                            System.out.println("This is the File Locaion " + file);
                            byte[] byteArray = new byte[(int) file.length()];
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                fileInputStream.read(byteArray);
                            } catch (FileNotFoundException d) {
                                System.out.println("File Not Found.");
                                e.printStackTrace();
                            }
                            catch (IOException e1) {
                                System.out.println("Error Reading The File.");
                                e1.printStackTrace();
                            }
                            byte[] data = FilePickerActivity.FILE_EXTRA_DATA_PATH.getBytes();
                            ParseFile parseFile = new ParseFile("alarm.3gpp", byteArray);

                            parseFile.saveInBackground();
                            alarm.put("soundfile", parseFile);
                            alarm.put("name",username);
                            alarm.put("friendId", FbId);
                            alarm.saveInBackground();
                        }
                    }
                });

                // NEEDS TO SEND OBJECTID THAT WE ALSO NEED TO RECIEVE ABOVE USING getStringExtra(): \\

                FragmentActivity activity = (FragmentActivity)(this);
                FragmentManager fm = activity.getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("objectId", objectId);
                MessageDialog alertDialog = new MessageDialog();
                alertDialog.setArguments(bundle);
                alertDialog.show(fm, "");

              /*  String friendId = tinyDB.getString("pushfriendId");
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("fbId",tinyDB.getString("pushfriendId"));

                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setMessage("Your Alarm at " + tinyDB.getString("alarmTime") + tinyDB.getString("alarmAM") + " was set by " + username);
                push.sendInBackground();

                */
            }

        }

    }

}

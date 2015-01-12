package epimelis.com.lyre;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.quentindommerc.superlistview.SuperListview;
import java.util.ArrayList;
import java.util.List;import adapters.FriendRequestAdapter;
import parse.Friends;
import parse.Users;
import utils.BaseActivity;


public class FriendRequests extends BaseActivity {


    FriendRequestAdapter mAdapter;
    public SuperListview mListView;
    public static final String TAG = FriendRequests.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ParseObject.registerSubclass(Friends.class);
        ParseObject.registerSubclass(Users.class);


        mListView = (SuperListview) findViewById(R.id.listView);

        mAdapter = new FriendRequestAdapter(this, new ArrayList<Friends>());

        mListView.setAdapter(mAdapter);
        updateData();



    }

    @Override
    protected int getLayoutResource() {

        return R.layout.friend_requests;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_settings, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home

                Intent intentHome = new Intent(this, MainActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHome);
                setResult(RESULT_OK);
                finish();
                this.finish();
                return true;


            case R.id.friend:
                Intent intent = new Intent(this, FriendRequests.class);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public void updateData(){
        String userID = ParseUser.getCurrentUser().get("fbId").toString();
        // Query to see if user exists
        ParseQuery<Friends> query = ParseQuery.getQuery(Friends.class);
        query.whereEqualTo("friendRequested",userID);
        query.whereEqualTo("Accepted", "false");
        query.findInBackground( new FindCallback<Friends>() {

            @Override
            public void done(List<Friends> friends, ParseException e) {

                if(friends != null){
                    Log.e(TAG, friends.toString() + " Friend results");
                    mAdapter.addAll(friends);

                } else {
                    Log.e(TAG, e.getMessage());

                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intentHome = new Intent(this, MainActivity.class);
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentHome);
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}

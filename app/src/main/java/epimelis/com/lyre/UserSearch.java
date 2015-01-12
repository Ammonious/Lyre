package epimelis.com.lyre;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.quentindommerc.superlistview.SuperListview;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import adapters.SearchArrayAdapter;
import parse.Alarms;
import parse.Friends;
import parse.ParseConstants;
import parse.ParseUserAdapter;
import parse.Users;
import utils.BaseActivity;


public class UserSearch extends BaseActivity {


    private ParseUser mCurrentUser;
    SearchArrayAdapter mAdapter;
    protected ParseRelation<ParseUser> mFriendsRelation;
    public static final String TAG = UserSearch.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseObject.registerSubclass(Users.class);
        ParseObject.registerSubclass(Friends.class);
        handleIntent(getIntent());
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser
                .getRelation(ParseConstants.KEY_FRIENDS_RELATION);


        SuperListview mListView = (SuperListview) findViewById(R.id.listView);

        mAdapter = new SearchArrayAdapter(this, new ArrayList<Users>());

        mListView.setAdapter(mAdapter);
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.user_search;
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
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String friend = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow


            // Query to see if user exists
            ParseQuery<Users> query = ParseQuery.getQuery(Users.class);
            query.whereContains("name_lower", friend.toLowerCase());
            query.orderByAscending(ParseConstants.KEY_USERNAME);
            query.setLimit(50);
            query.findInBackground( new FindCallback<Users>() {

                @Override
                public void done(List<Users> friends, ParseException e) {

                    if(friends != null){
                        Log.e(TAG, friends.toString() + " Friend results");
                        mAdapter.addAll(friends);

                    } else {
                        Log.e(TAG, e.getMessage());

                    }
                }
            });
        }






        }
/*
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListView().isItemChecked(position)) {
            // add friend
            mFriendsRelation.add(mUsers.get(position));
        } else {
            // remove friend
            mFriendsRelation.remove(mUsers.get(position));
        }
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void addFriendCheckmarks() {
        mFriendsRelation.getQuery().findInBackground(
                new FindCallback<ParseUser>() {

                    @Override
                    public void done(List<ParseUser> friends, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < mUsers.size(); i++) {
                                ParseUser user = mUsers.get(i);
                                for (ParseUser friend : friends) {
                                    if (friend.getObjectId().equals(
                                            user.getObjectId())) {
                                        getListView().setItemChecked(i, true);
                                    }
                                }
                            }
                        } else {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
    }
     */
    }



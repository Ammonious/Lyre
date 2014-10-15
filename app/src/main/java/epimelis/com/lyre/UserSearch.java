package epimelis.com.lyre;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import parse.ParseConstants;
import parse.ParseUserAdapter;


public class UserSearch extends ListActivity {


    private ParseUser mCurrentUser;

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    public static final String TAG = UserSearch.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        handleIntent(getIntent());
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser
                .getRelation(ParseConstants.KEY_FRIENDS_RELATION);


        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContains("profile", friend);//friend);
            query.orderByAscending(ParseConstants.KEY_USERNAME);
            query.setLimit(50);
            query.findInBackground(new FindCallback<ParseUser>() {

                @Override
                public void done(List<ParseUser> users, ParseException e) {

                    if (e == null) {
                        //Success we have Users to display
                        //Get users match us
                        System.out.println( friend + "Parse Query");
                        mUsers = users;
                        //store users in array
                        String[] friend = new String[mUsers.size()];
                        //Loop Users
                        int i = 0;
                        for (ParseUser user : mUsers) {
                            ParseObject myuser =null;
                            try {
                                myuser = user.fetch();
                                JSONObject profile = new JSONObject(myuser.get("profile").toString());
                                friend[i] = profile.get("name").toString();
                                i++;
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }


                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                UserSearch.this,
                                android.R.layout.simple_list_item_checked,
                                friend
                        );
                        setListAdapter(adapter);
                        addFriendCheckmarks();


                    } else {
                        Log.e(TAG, e.getMessage());

                    }
                }
            });
        }


       /*
        */


        }

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
    }



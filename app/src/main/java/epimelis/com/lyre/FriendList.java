package epimelis.com.lyre;

import android.app.ActionBar;
import android.app.Activity;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.widget.AbsListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.AlarmAdapter;
import alarm.AlarmSettings;
import parse.Alarms;
import parse.ParseConstants;
import parse.ParseUserAdapter;
import parse.Users;


public class FriendList extends Activity {
    FloatingActionButton mFab;
    private ParseUser mCurrentUser;
    private ParseUserAdapter mFriendAdapter;
    protected ParseRelation<Users> mFriendsRelation;
    protected ParseRelation<ParseObject> mAlarmRelation;
    public static final String TAG = FriendList.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);



        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.logo);
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface robotoBoldCondensedItalic = Typeface.createFromAsset(getAssets(), "fonts/Righteous-Regular.ttf");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(robotoBoldCondensedItalic);
        }
        mFab = (FloatingActionButton) findViewById(R.id.fabbutton);
        mFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), AlarmSettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getBaseContext().startActivity(intent);


                // Setup the transition to the detail activity
              //  ActivityOptions options =  ActivityOptions.makeSceneTransitionAnimation(FriendList.this, view, "photo" + i);
              //  startActivity(detailIntent, options.toBundle());

            }
        });

        ParseObject.registerSubclass(Users.class);
        AbsListView mListView = (AbsListView) findViewById(R.id.listView);
        mFab.listenTo(mListView);

        mFriendAdapter = new ParseUserAdapter(getBaseContext(), new ArrayList<Users>());

        mListView.setAdapter(mFriendAdapter);



        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
            loadFriendList();
            mFriendAdapter.addAll();
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
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
    public void onResume() {
        super.onResume();


        ParseUser mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
            mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
           // updateViewsWithProfileInfo();
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startLoginActivity();
        }
    }

    private void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {

                        if (user != null) {

                            ParseUser.getCurrentUser().put("fbId", user.getId());
                            ParseUser.getCurrentUser().put("name", user.getFirstName());
                            // Create a JSON object to hold the profile info
                            JSONObject userProfile = new JSONObject();
                            try {
                                // Populate the JSON object
                                userProfile.put("facebookId", user.getId());
                                userProfile.put("name", user.getName());
                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser
                                        .getCurrentUser();
                                currentUser.put("profile", userProfile.toString());
                                currentUser.saveInBackground();

                                // Show the user info
                                updateViewsWithProfileInfo();
                            } catch (JSONException e) {

                            }

                        } else if (response.getError() != null) {
                            if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
                                    || (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {


                            } else {

                            }
                        }
                    }
                });
        request.executeAsync();
                }

    private void updateViewsWithProfileInfo() {
        ParseUser mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser.get("profile") != null) {

            JSONObject userProfile = null;
            try {
                userProfile = new JSONObject(mCurrentUser.get("profile").toString());
                try {
                    if (userProfile.getString("facebookId") != null) {
                        String facebookId = userProfile.get("facebookId")
                                .toString();

                    } else {
                        // Show the default, blank user profile picture

                    }
                    if (userProfile.getString("name") != null) {

                    } else {

                    }

                } catch (JSONException e) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, ParseLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void loadFriendList() {


        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser
                .getRelation(ParseConstants.KEY_FRIENDS_RELATION);


        ParseQuery<Users> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(
                new FindCallback<Users>() {

                    @Override
                    public void done(List<Users> friends, ParseException e) {

                        if(friends != null){
                            Log.e(TAG, friends.toString() + "Your friends");
                           // mFriendAdapter.clear();
                           //mFriendAdapter.add(friends);
                           mFriendAdapter.addAll(friends);

                        } else {
                            Log.e(TAG, e.getMessage());

                        }
                    }
                });
    }

    public void hideFab(View view) {
        mFab.hide(true);
        //getActionBar().hide();
    }

    public void showFab(View view) {
        mFab.hide(false);
        //getActionBar().show();
    }

    public void fabClicked(View view) {
        Toast.makeText(this, getResources().getString(R.string.hello_world), Toast.LENGTH_LONG)
                .show();
    }







        /*ParseQuery<ParseObject> query = ParseQuery.getQuery("Friend");
        query.whereEqualTo("user", mCurrentUser);
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereMatchesKeyInQuery("username", "friend", query);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (parseUsers == null) {
                    Toast.makeText(getBaseContext(), getString(R.string.error_oops),
                            Toast.LENGTH_LONG).show();
                } else {
                    mUserFriends = parseUsers;
                    mFriendAdapter = new ParseUserAdapter(getBaseContext(), mUserFriends);
                    listview.setAdapter(mFriendAdapter);
                    mFriendAdapter.notifyDataSetChanged();
                }
            }
        });*/
    }




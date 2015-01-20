package epimelis.com.lyre;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.quentindommerc.superlistview.SuperListview;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import alarm.MyAlarmScreen;
import parse.Friends;
import parse.ParseConstants;
import parse.ParseUserAdapter;
import parse.Users;
import utils.BaseActivity;
import utils.TinyDB;
import utils.UserPreferenceActivity;


public class MainActivity extends BaseActivity {

    private static final int SAMPLE2_ID = 34535;
    private ParseUser mCurrentUser;
    private ParseUserAdapter mFriendAdapter;
    protected ParseRelation<Users> mFriendsRelation;
    protected ParseRelation<ParseObject> mAlarmRelation;
    public static final String TAG = MainActivity.class.getSimpleName();
    public static int REQUEST_CODE = 2;
    ProgressWheel progressWheel;
    SuperListview mListView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarIcon(R.drawable.logo2);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);
        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.button_floating_action);
        mFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                MyAlarmScreen.launch(MainActivity.this, v.findViewById(R.id.button_floating_action));


            }

        });

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(this);
         PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        boolean notifications = getPrefs.getBoolean("prefNotification", true);

        if (notifications) {

            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("Subscriber", "True");
            installation.saveInBackground();
        }
        else{

                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("Subscriber", "False");
                installation.saveInBackground();
        }

        ParseObject.registerSubclass(Users.class);
        mListView = (SuperListview) findViewById(R.id.listView);
        mFriendAdapter = new ParseUserAdapter(this, new ArrayList<Users>());

        mListView.setAdapter(mFriendAdapter);



        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
         //   loadFriendList();
           // RelationshipCheck();
          //  getFriendRequestsCount();
            mFriendAdapter.addAll();
            mFriendAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.main_screen;
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_list, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        TinyDB tinyDB = new TinyDB(this);
        int badgeCount = tinyDB.getInt("badgeCount");

      //  if (badgeCount > 0) {
      //      ActionItemBadge.update(this, menu.findItem(R.id.friend), ActionItemBadge.BadgeStyle.DARKGREY, badgeCount);
      //   } else {
     //       ActionItemBadge.hide(menu.findItem(R.id.friend));
     //    }

        //If you want to add your ActionItem programmatically you can do this too. You do the following:
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:


                return true;
            case R.id.friend:
                Intent intent = new Intent(this, FriendRequests.class);

                startActivityForResult(intent, 2);
                return true;
            case R.id.rate:
                Intent rateIntent = new Intent(Intent.ACTION_VIEW);
                rateIntent.setData(Uri.parse("market://details?id=epimelis.com.lyre"));
                startActivity(rateIntent);
                return true;
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=epimelis.com.lyre");
                startActivity(Intent.createChooser(shareIntent, "Share app"));
                return true;
            case R.id.action_settings:
                Intent nextScreen4 = new Intent(getApplicationContext(), UserPreferenceActivity.class);
                startActivityForResult(nextScreen4,2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mFriendAdapter.clear();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        progressWheel.setVisibility(View.VISIBLE);
        ParseQuery<Users> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(
                new FindCallback<Users>() {

                    @Override
                    public void done(List<Users> friends, ParseException e) {

                        if(friends != null){

                            mFriendAdapter.addAll(friends);
                            mFriendAdapter.populateAlarmCount();
                            mListView.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                        } else {
                            Log.e(TAG, e.getMessage());

                        }
                    }
                });

        RelationshipCheck();

        mFriendAdapter.notifyDataSetChanged();
        ParseUser mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
            mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startLoginActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            mFriendAdapter.notifyDataSetChanged();
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean notifications = getPrefs.getBoolean("prefNotification", true);

            if (notifications) {

                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("Subscriber", "True");
                installation.saveInBackground();
            }
            else{

                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("Subscriber", "False");
                installation.saveInBackground();
            }

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
                            ParseUser.getCurrentUser().put("name_lower", user.getName().toLowerCase());



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
                             //   updateViewsWithProfileInfo();

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


    private void startLoginActivity() {
        Intent intent = new Intent(this, ParseLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void loadFriendList() {


        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);


        ParseQuery<Users> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(
                new FindCallback<Users>() {

                    @Override
                    public void done(List<Users> friends, ParseException e) {

                        if(friends != null){

                           mFriendAdapter.addAll(friends);

                        } else {
                            Log.e(TAG, e.getMessage());

                        }
                    }
                });

       RelationshipCheck();

    }



    public void RelationshipCheck() {
        // First Check if you have FB Id so you can do query
        if (ParseUser.getCurrentUser().get("fbId") != null) {

            String userID = ParseUser.getCurrentUser().get("fbId").toString();
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("fbId", userID);
            installation.saveInBackground();
            // Query to see if user exists
            ParseQuery<Friends> query = ParseQuery.getQuery(Friends.class);
            query.whereEqualTo("requestedFrom", userID);
            query.whereEqualTo("Accepted", "true");
            query.findInBackground(new FindCallback<Friends>() {

                @Override
                public void done(List<Friends> friends, ParseException e) {

                    if (friends != null) {
                        for (final ParseObject friend : friends) {// create a loop to go through each friend in the list
                            String them = friend.get("friendRequested").toString();
                            ParseQuery<ParseUser> requestingUser = ParseUser.getQuery();
                            requestingUser.whereEqualTo("fbId", them);
                            requestingUser.findInBackground(new FindCallback<ParseUser>() {

                                @Override
                                public void done(final List<ParseUser> userlist, ParseException e) {
                                    if (e == null) {
                                        com.parse.ParseUser Him = userlist.get(0);
                                        mCurrentUser = ParseUser.getCurrentUser();
                                        ParseRelation<ParseUser> awesomeRelation = mCurrentUser
                                                .getRelation(ParseConstants.KEY_FRIENDS_RELATION);
                                        awesomeRelation.add(Him);
                                        friend.deleteInBackground();
                                        mCurrentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e != null) {
                                                    Log.e("Meow", e.getMessage());
                                                }
                                            }
                                        });

                                        return;
                                    } else {

                                    }
                                }
                            });
                        }


                    } else

                    {
                        Log.e(TAG, e.getMessage());

                    }


                }


            });

        }

    }

    public void  getFriendRequestsCount(){

        final TinyDB tinyDB = new TinyDB(this);

        // ******* Get Friend Request Count ******* \\
        if (ParseUser.getCurrentUser().get("fbId") != null) {
            String userID = ParseUser.getCurrentUser().get("fbId").toString();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
            query.whereEqualTo("facebookId", userID);
            query.whereEqualTo("Accepted", "false");
            query.countInBackground(new CountCallback() {
                public void done(int count, ParseException e) {
                    if (e == null) {
                        int badgeCount = count;

                        tinyDB.putInt("badgeCount", badgeCount);
                    } else {

                    }
                }
            });
        }

    }


    }




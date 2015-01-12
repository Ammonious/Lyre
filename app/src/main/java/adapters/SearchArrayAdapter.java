package adapters;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.RoundedImageView;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.PushService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import epimelis.com.lyre.FriendRequests;
import epimelis.com.lyre.MainActivity;
import epimelis.com.lyre.R;
import mehdi.sakout.fancybuttons.FancyButton;
import parse.Friends;
import parse.ParseConstants;
import parse.Users;
import utils.ImageLoader;

/**
 * Created by ammonrees on 10/26/14.
 */
public class SearchArrayAdapter extends ArrayAdapter<Users> {
    private List<Users> mParseUsers,mUserId,mName;
    ImageLoader imageLoader;
    protected ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;

    public SearchArrayAdapter(Context ctx, List<Users> parseUsers) {
        super(ctx, R.layout.search_row, parseUsers);
        mUserId = parseUsers;
        mName = parseUsers;

        imageLoader= new ImageLoader(ctx);
    }

    private ViewHolder holder;

    public static class ViewHolder {

        public TextView username;
        public RoundedImageView profilePic;
        public FancyButton friend;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Bold.ttf");
        //  Typeface tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface tf3 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_row,parent, false);
            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.user_name);
            holder.profilePic = (RoundedImageView) convertView.findViewById(R.id.userSearchPicture);
            holder.friend = (FancyButton) convertView.findViewById(R.id.add_friend);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        holder.friend.setTag(position);
        final Users userId = mUserId.get(position);
        final Users name = mName.get(position);
       // System.out.println("This Persons Name" + name.getName()+' '+position);


        holder.username.setText(name.getName());
        holder.username.setTypeface(tf3);

        // Setting the button View to invisible as to prevent users from re-adding same friend \\
        final ViewHolder finalHolder = holder;
        convertView.post(new Runnable() {

            @Override
            public void run() {


                mCurrentUser = ParseUser.getCurrentUser();
                mFriendsRelation = mCurrentUser
                        .getRelation(ParseConstants.KEY_FRIENDS_RELATION);
                mFriendsRelation.getQuery().findInBackground(
                        new FindCallback<ParseUser>() {

                            @Override
                            public void done(List<ParseUser> friends, ParseException e) {
                                if (e == null) {
                                    for (int i = 0; i < mUserId.size(); i++) {
                                        ParseUser user = mUserId.get(i);
                                        for (ParseUser friend : friends) {
                                            if (friend.getObjectId().equals(
                                                    user.getObjectId())) {
                                                //finalHolder.friend.getChildAt(i).findViewById(R.id.add_friend).setVisibility((View.INVISIBLE));
                                                //finalHolder.friend.setTag(position);
                                                //finalHolder.friend.getTag(position);
                                                System.out.println(" Tag " + finalHolder.friend.getTag());
                                                finalHolder.friend.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                } else {

                                }
                            }
                        });


                         }

        });
      /*  if(position>=mUserId.size()-1) {
            final ViewHolder finalHolder = holder;
            mCurrentUser = ParseUser.getCurrentUser();
            mFriendsRelation = mCurrentUser
                    .getRelation(ParseConstants.KEY_FRIENDS_RELATION);
            mFriendsRelation.getQuery().findInBackground(
                    new FindCallback<ParseUser>() {

                        @Override
                        public void done(List<ParseUser> friends, ParseException e) {
                            if (e == null) {
                                for (int i = 0; i < mUserId.size(); i++) {
                                    ParseUser user = mUserId.get(i);
                                    for (ParseUser friend : friends) {
                                        if (friend.getObjectId().equals(
                                                user.getObjectId())) {
                                            //finalHolder.friend.getChildAt(i).findViewById(R.id.add_friend).setVisibility((View.INVISIBLE));
                                            //finalHolder.friend.setTag(position);
                                            //finalHolder.friend.getTag(position);
                                            finalHolder.friend.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }
                            } else {

                            }
                        }
                    });
        }
*/
        String url = String.format(
                "https://graph.facebook.com/%s/picture?width=150&height=150",userId.getUserId());

        imageLoader.DisplayImage(url, holder.profilePic);





        holder.friend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final String myID = ParseUser.getCurrentUser().get("fbId").toString();
                final String friendID = userId.getUserId();
                final String myName = ParseUser.getCurrentUser().get("name").toString();

                                Friends newfriend = new Friends();

                                newfriend.setUserId(myID); // Requested From FB ID
                                newfriend.setName(myName); // Requested From Name
                                newfriend.setAccepted("false");
                                newfriend.setFriendId(userId.getUserId()); // Gets ID of Friend according to Row
                                newfriend.setfriendName(name.getName()); // Sets friends name
                                newfriend.saveInBackground(); // Saves

                                // String friendId = tinyDB.getString("pushfriendId");
                                ParseQuery pushQuery = ParseInstallation.getQuery();
                                pushQuery.whereEqualTo("fbId",friendID);

                                // Send push notification to query
                                ParsePush push = new ParsePush();
                                push.setQuery(pushQuery); // Set our Installation query
                                push.setMessage(myName + " Has Requested To Be Your Friend");
                                push.sendInBackground();


                Users friends = getItem(position);
                SearchArrayAdapter.this.remove(friends);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Friend Request Sent", Toast.LENGTH_SHORT).show();




            }

        });


        return convertView;
    }


}
package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.makeramen.RoundedImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import java.util.List;
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
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
    private List<Users> mUsers;
    ImageLoader imageLoader;
    protected ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private List<ParseUser> addedFriends;
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
    public SearchArrayAdapter(Context ctx, List<Users> parseUsers) {
        super(ctx, R.layout.search_row, parseUsers);
        mUsers = parseUsers;
        imageLoader= new ImageLoader(ctx);

        // Setting the button View to invisible as to prevent users from re-adding same friend \\
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        mFriendsRelation.getQuery().findInBackground(
                new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> friends, ParseException e) {
                        if (e == null) {
                            addedFriends = friends;
                        }
                    }
                });
    }

    private ViewHolder holder;
    private int holderId = 0;

    public static class ViewHolder {

        public TextView username;
        public RoundedImageView profilePic;
        public FancyButton friend;
        public boolean added = false;
        public int id = 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
<<<<<<< Updated upstream
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Bold.ttf");
        //  Typeface tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Bold.ttf");
=======

>>>>>>> Stashed changes
        Typeface tf3 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_row,parent, false);
            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.user_name);
            holder.profilePic = (RoundedImageView) convertView.findViewById(R.id.userSearchPicture);
            holder.friend = (FancyButton) convertView.findViewById(R.id.add_friend);
            holder.id = ++holderId;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            System.out.print("User holder already been created.  has he been added?" );
            System.out.println(holder.added);
        }
<<<<<<< Updated upstream

        System.out.println("User id: " + String.valueOf(holder.id));

        holder.friend.setTag(position);
        final Users user = mUsers.get(position);
        System.out.println("User name: " + String.valueOf(user.getName()));
       // System.out.println("This Persons Name" + name.getName()+' '+position);

        holder.username.setText(user.getName());
        holder.username.setTypeface(tf3);
=======

        System.out.println("User id: " + String.valueOf(holder.id));

        holder.friend.setTag(position);
        final Users user = mUsers.get(position);
        System.out.println("User name: " + String.valueOf(user.getName()));
        // System.out.println("This Persons Name" + name.getName()+' '+position);
>>>>>>> Stashed changes

        holder.username.setText(user.getName());
        holder.username.setTypeface(tf3);

<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
        System.out.println( "checking if: " + user.getName() + "(" + user.getObjectId() + ") has been added already");
        ///first set it to false
        holder.friend.setVisibility(View.VISIBLE);
        holder.added = false;
        for (ParseUser friend : addedFriends) {
            System.out.println("checking against: " + friend.getObjectId());
            if (friend.getObjectId().equals(user.getObjectId())) {
                holder.friend.setVisibility(View.INVISIBLE);
                holder.added = true;
                System.out.println("holder id (" + String.valueOf(holder.id) + ") changing to added.");
                break;
            }
        }

        String url = String.format(
                "https://graph.facebook.com/%s/picture?width=150&height=150",user.getUserId());

        imageLoader.DisplayImage(url, holder.profilePic);

        holder.friend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final String myID = ParseUser.getCurrentUser().get("fbId").toString();
                final String friendID = user.getUserId();
                final String myName = ParseUser.getCurrentUser().get("name").toString();

                Friends newfriend = new Friends();
<<<<<<< Updated upstream

                newfriend.setUserId(myID); // Requested From FB ID
                newfriend.setName(myName); // Requested From Name
                newfriend.setAccepted("false");
                newfriend.setFriendId(user.getUserId()); // Gets ID of Friend according to Row
                newfriend.setfriendName(user.getName()); // Sets friends name
                newfriend.saveInBackground(); // Saves

=======

                newfriend.setUserId(myID); // Requested From FB ID
                newfriend.setName(myName); // Requested From Name
                newfriend.setAccepted("false");
                newfriend.setFriendId(user.getUserId()); // Gets ID of Friend according to Row
                newfriend.setfriendName(user.getName()); // Sets friends name
                newfriend.saveInBackground(); // Saves

>>>>>>> Stashed changes
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
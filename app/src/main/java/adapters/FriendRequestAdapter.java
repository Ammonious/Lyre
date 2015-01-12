package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;
import android.widget.Toast;


import com.makeramen.RoundedImageView;
import com.parse.FindCallback;
import com.parse.ParseException;

import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import epimelis.com.lyre.R;
import mehdi.sakout.fancybuttons.FancyButton;
import parse.Friends;
import parse.ParseConstants;
import parse.Users;

/**
 * Created by ammonrees on 11/1/14.
 */
public class FriendRequestAdapter extends ArrayAdapter<Friends> {

    Friends friends = new Friends();
    Users users = new Users();
    ViewHolder holder;
    private ParseUser mCurrentUser;
    private List<Friends> mParseUsers,mUserId,mName,mAccept;
    Context mContext;
    protected ParseRelation<ParseUser> mFriendsRelation;


    public FriendRequestAdapter(Context ctx, List<Friends> parseUsers) {
        super(ctx, R.layout.request_row, parseUsers);
        mParseUsers = parseUsers;
        mUserId = parseUsers;
        mName = parseUsers;
        mAccept = parseUsers;
    }



    public static class ViewHolder {

        public TextView username;
        public RoundedImageView profilePic;

        public FancyButton accept,decline;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface tf3 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.request_row, parent, false);
            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.user_name);
            holder.profilePic = (RoundedImageView) convertView.findViewById(R.id.userSearchPicture);

            holder.accept = (FancyButton) convertView.findViewById(R.id.accept);
            holder.decline = (FancyButton) convertView.findViewById(R.id.decline);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        final Friends userId = mUserId.get(position);
        final Friends name = mName.get(position);
        final Friends accepted = mAccept.get(position);



        holder.username.setText(name.getName());
        holder.username.setTypeface(tf3);




        final ViewHolder finalHolder = holder;


        new AsyncTask<Void, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(Void... params)
            {
                // safety check
                if (userId.getUserId() == null)
                    return null;

                String url = String.format(
                        "https://graph.facebook.com/%s/picture?width=150&height=150",userId.getUserId());

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

                finalHolder.profilePic.setImageBitmap(bitmap);

                // do what you need to do with the bitmap :)
            }
        }.execute();



        holder.accept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                accepted.setAccepted("true");
                accepted.saveInBackground();
                final String fbid = userId.getUserId();
                System.out.println(fbid);
                ParseQuery<ParseUser> requestingUser= ParseUser.getQuery();
                requestingUser.whereEqualTo("fbId", fbid);
                requestingUser.findInBackground(new FindCallback<ParseUser>() {

                    @Override
                    public void done(final List<ParseUser> userlist, ParseException e) {
                        if (e == null) {
                            ParseUser Him = userlist.get(0);
                            mCurrentUser = ParseUser.getCurrentUser();
                            mFriendsRelation = mCurrentUser
                                    .getRelation(ParseConstants.KEY_FRIENDS_RELATION);
                            mFriendsRelation.add(Him);
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

                            System.out.println(" exceptions");
                        }
                    }

                });
                Friends friends = getItem(position);
                Toast.makeText(getContext(), "Friend Request Accepted!",
                        Toast.LENGTH_SHORT).show();
                FriendRequestAdapter.this.remove(friends);
                notifyDataSetChanged();
                }

            });

        holder.decline.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Friends friends = getItem(position);
                friends.deleteInBackground();
                FriendRequestAdapter.this.remove(friends);
                Toast.makeText(getContext(), "Friend Request Declined!",
                        Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }

        });


        return convertView;
    }



}
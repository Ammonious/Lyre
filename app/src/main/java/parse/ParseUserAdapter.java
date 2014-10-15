package parse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.makeramen.RoundedTransformationBuilder;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import alarm.AlarmSettings;
import alarm.AlarmDetailsActivity;
import epimelis.com.lyre.FriendsAlarm;
import epimelis.com.lyre.R;

/**
 * Created by ammonrees on 8/28/14.
 */
public class ParseUserAdapter extends ArrayAdapter<Users> {


    ViewHolder holder;
    private List<Users> mParseUsers,mUserId,mName;
    Context mContext;
   // http://graph.facebook.com/<facebookId>/picture?type=square



    public ParseUserAdapter(Context ctx, List<Users> parseUsers) {
       super(ctx, R.layout.friend_row, parseUsers);
       mUserId = parseUsers;
       mName = parseUsers;
    }

   /* @Override
    public int getCount() {
        return mUserId != null ? mUserId.size() : 0;
    } */

   /* @Override
    public ParseUser getItem(int position) {
        return mParseUsers.get(position);
    } */

 /*   @Override
    public long getItemId(int position) {
        return 0;
    }*/

    public static class ViewHolder {

        public TextView username;
        public RoundedImageView profilepic;
        public LinearLayout friend;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Bold.ttf");
        //  Typeface tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface tf3 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_row, null);
            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.profilepic = (RoundedImageView) convertView.findViewById(R.id.userProfilePicture);
            holder.friend = (LinearLayout) convertView.findViewById(R.id.friend);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

       final Users userId = mUserId.get(position);
       final Users name = mName.get(position);



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

              finalHolder.profilepic.setImageBitmap(bitmap);

                // do what you need to do with the bitmap :)
            }
        }.execute();



        holder.friend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(getContext(), FriendsAlarm.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("name", name.getName());
                intent.putExtra("picId",userId.getUserId());
                getContext().startActivity(intent);



            }

        });


        return convertView;
    }



}
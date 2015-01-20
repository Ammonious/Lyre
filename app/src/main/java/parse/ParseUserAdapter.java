package parse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
import com.balysv.materialripple.MaterialRippleLayout;
import com.makeramen.RoundedImageView;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
import java.util.List;

import epimelis.com.lyre.MainActivity;
import epimelis.com.lyre.FriendsAlarm;
import epimelis.com.lyre.R;
import mehdi.sakout.fancybuttons.FancyButton;
import utils.ImageLoader;

/**
 * Created by ammonrees on 8/28/14.
 */
public class ParseUserAdapter extends ArrayAdapter<Users> {


    ViewHolder holder;
    ///TODO: only need one list of users.

    private List<Users> mUserId;
    private int[] mAlarms;
    Context mContext;
    public ImageLoader imageLoader;

    public interface OnDataChangeListener{
        public void onDataChanged(int size);
    }

    OnDataChangeListener mOnDataChangeListener;
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }

    public ParseUserAdapter(Context ctx, List<Users> parseUsers) {
<<<<<<< Updated upstream
       super(ctx, R.layout.friend_row, parseUsers);
        mUserId = parseUsers;
        imageLoader= new ImageLoader(ctx);

    }

    public void populateAlarmCount()
    {
        mAlarms = new int[mUserId.size()];

        for(int i=0; i<mUserId.size(); i++)
        {
            Users userId = mUserId.get(i);

            final int iHateJava = i;

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Alarms");
            query.whereEqualTo("facebookId", userId.getUserId());
            query.whereDoesNotExist("soundfile");
            query.countInBackground(new CountCallback() {
                public void done(int count, ParseException e) {
                    if (e == null) {
                        mAlarms[iHateJava] = count;
                    } else {
                        mAlarms[iHateJava] = 0;
                    }
                }
            });
        }
=======
        super(ctx, R.layout.friend_row, parseUsers);
        mUserId = parseUsers;
        imageLoader= new ImageLoader(ctx);

>>>>>>> Stashed changes
    }





    public static class ViewHolder {

        public TextView username;
        public RoundedImageView profilepic;
        public MaterialRippleLayout friend;
        public FancyButton countView;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Bold.ttf");
        //  Typeface tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface tf3 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_row, parent, false);
            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.profilepic = (RoundedImageView) convertView.findViewById(R.id.userProfilePicture);
            holder.friend = (MaterialRippleLayout) convertView.findViewById(R.id.friend);
            holder.countView = (FancyButton) convertView.findViewById(R.id.count_view);
            convertView.setTag(holder);

            if(mOnDataChangeListener != null) {
                mOnDataChangeListener.onDataChanged(mUserId.size());
            }
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

<<<<<<< Updated upstream
       final Users userId = mUserId.get(position);
       final Users name = mUserId.get(position);
       final int alarmCount = mAlarms[position];

                    holder.username.setText(name.getName());
                holder.username.setTypeface(tf3);

        holder.countView.setText(String.valueOf(alarmCount));
=======
        final Users userId = mUserId.get(position);
        final Users name = mUserId.get(position);
      //  final int alarmCount = mAlarms[position];

        holder.username.setText(name.getName());
        holder.username.setTypeface(tf3);

      //  holder.countView.setText(String.valueOf(alarmCount));
>>>>>>> Stashed changes

        String url = String.format(
                "https://graph.facebook.com/%s/picture?width=150&height=150", userId.getUserId());

        imageLoader.DisplayImage(url, holder.profilepic);


        holder.friend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //   Intent intent = new Intent(getContext(), FriendsAlarm.class);
                //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //   intent.putExtra("name", name.getName());
                //   intent.putExtra("picId",userId.getUserId());
                String url = userId.getUserId();
                String Username = name.getName();
                FriendsAlarm.launch(((MainActivity)getContext()), v.findViewById(R.id.userProfilePicture), url, Username);
<<<<<<< Updated upstream
             //   getContext().startActivity(intent);

           }
=======
                //   getContext().startActivity(intent);

            }
>>>>>>> Stashed changes

        });


        return convertView;
    }

    public void populateAlarmCount()
    {
        mAlarms = new int[mUserId.size()];

        for(int i=0; i<mUserId.size(); i++)
        {
            Users userId = mUserId.get(i);

            final int iHateJava = i;

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Alarms");
            query.whereEqualTo("facebookId", userId.getUserId());
            query.whereDoesNotExist("soundfile");
            query.countInBackground(new CountCallback() {
                public void done(int count, ParseException e) {
                    if (e == null) {
                        mAlarms[iHateJava] = count;
                        System.out.println("The Count of Monte " + count);
//                        holder.countView.setText(String.valueOf(count));
                    } else {
                        mAlarms[iHateJava] = 0;
                    }
                }
            });
        }
    }


}

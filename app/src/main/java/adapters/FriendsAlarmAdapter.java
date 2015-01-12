package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devpaul.filepickerlibrary.FilePickerActivity;
import com.devpaul.filepickerlibrary.enums.FileScopeType;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import epimelis.com.lyre.FriendsAlarm;
import epimelis.com.lyre.R;
import parse.Alarms;
import utils.RecordDialog;
import utils.TinyDB;

/**
 * Created by ammonrees on 9/6/14.
 */
public class FriendsAlarmAdapter extends ArrayAdapter<Alarms> {
    private Context mContext;
    private List<Alarms> mAlarms,mAM,mDesc;


    ViewHolder viewHolder;

    public FriendsAlarmAdapter(Context context, List<Alarms> objects) {
        super(context, R.layout.friends_alarm_row, objects);
        this.mContext = context;
        this.mAlarms = objects;
        this.mAM = objects;
        this.mDesc = objects;



    }


    private class ViewHolder
    {

        TextView alarm,AM_PM,Desc;
        RelativeLayout music,record;
    }

    public View getView(final int position, View convertView, ViewGroup parent){

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Thin.ttf");
        //  Typeface tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface tf3 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");

        if(convertView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.friends_alarm_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.alarm = (TextView) convertView.findViewById(R.id.alarm);
            viewHolder.AM_PM = (TextView) convertView.findViewById(R.id.friend_am_pm);
            viewHolder.Desc = (TextView) convertView.findViewById(R.id.alarm_description);
            viewHolder.music = (RelativeLayout) convertView.findViewById(R.id.music);
            viewHolder.record = (RelativeLayout) convertView.findViewById(R.id.record);

            convertView.setTag( viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();


        }

        final Alarms parseAlarm = mAlarms.get(position);
        final Alarms parseAM = mAM.get(position);
        final Alarms parseD = mDesc.get(position);
        final Alarms parseFriend = mAlarms.get(position);



        viewHolder.alarm.setText(parseAlarm.getAlarm());
        viewHolder.alarm.setTypeface(tf);

        viewHolder.AM_PM.setText(parseAM.getAM_PM());
        viewHolder.AM_PM.setTypeface(tf3);

        viewHolder.Desc.setText(parseD.getAlarmDes());
        viewHolder.Desc.setTypeface(tf3);











        viewHolder.music.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String objectId = parseAlarm.getFriendId();
                TinyDB tinyDB = new TinyDB(mContext);
                tinyDB.putString("objectId", parseAlarm.getObjectId());
                tinyDB.putString("pushfriendId",parseAlarm.getFriendId());
                tinyDB.putString("alarmTime", parseAlarm.getAlarm());
                tinyDB.putString("alarmAM", parseAlarm.getAM_PM());
                Intent filePicker = new Intent(mContext, FilePickerActivity.class);
                filePicker.putExtra(FilePickerActivity.SCOPE_TYPE, FileScopeType.ALL);
                filePicker.putExtra("objectId", parseAlarm.getObjectId());
                filePicker.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
                filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_orange_dark);
                ((FriendsAlarm) mContext).startActivityForResult(filePicker, FilePickerActivity.REQUEST_FILE);




            }

        });

        viewHolder.record.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String objectId = parseAlarm.getObjectId();
                FragmentActivity activity = (FragmentActivity)(mContext);
                FragmentManager fm = activity.getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("objectId", objectId);
                bundle.putString("friendFB",parseFriend.getFriendId());
                RecordDialog alertDialog = new RecordDialog();
                alertDialog.setArguments(bundle);
                alertDialog.show(fm, "");



            }
        });





        return convertView;
    }


}

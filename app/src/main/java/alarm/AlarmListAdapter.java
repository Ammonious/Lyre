package alarm;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import epimelis.com.lyre.R;
import parse.Alarms;
import parse.Users;
import utils.TinyDB;

/**
 * Created by ammonrees on 9/20/14.
 */
public class AlarmListAdapter extends BaseAdapter {

    private Context mContext;
    private List<AlarmModel> mAlarms;

    ViewHolder viewHolder;
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
    Alarms displayAlarm = new Alarms();

    private AlarmDBHelper dbHelper;

    public AlarmListAdapter(Context context, List<AlarmModel> alarms) {
        mContext = context;
        mAlarms = alarms;
    }

    public void setAlarms(List<AlarmModel> alarms) {
        mAlarms = alarms;
    }

    @Override
    public final boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        if (mAlarms != null) {
            return mAlarms.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mAlarms != null) {
            return mAlarms.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mAlarms != null) {
            return mAlarms.get(position).id;
        }
        return 0;
    }

    private class ViewHolder
    {
        RelativeLayout share;
        RelativeLayout fav;
        RelativeLayout options;
        CheckBox checkBox;

        SwitchCompat btnToggle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

<<<<<<< Updated upstream

        final AlarmModel model = (AlarmModel) getItem(position);
=======
>>>>>>> Stashed changes
        final TinyDB tinydb = new TinyDB(mContext);

        System.out.println("View Position: " + String.valueOf(position));
        final AlarmModel model = (AlarmModel) getItem(position);

        ViewHolder viewHolder;

        if(convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.alarm_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.visible_toggle);
            viewHolder.btnToggle = (SwitchCompat) convertView.findViewById(R.id.alarm_item_toggle);
            viewHolder.options = (RelativeLayout) convertView.findViewById(R.id.options);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final TextView txtTime = (TextView) convertView.findViewById(R.id.alarm_item_time);

        if(model.timeHour > 12) {
            model.timeHour -= 12;
            txtTime.setText(String.format("%1d:%02d", model.timeHour, model.timeMinute));
           viewHolder = (ViewHolder) convertView.getTag();
<<<<<<< Updated upstream

           viewHolder = (ViewHolder) convertView.getTag();
        }
            else if (model.timeHour == 0) {
=======
           viewHolder = (ViewHolder) convertView.getTag();
        }
            else if (model.timeHour == 0) {

>>>>>>> Stashed changes
            model.timeHour += 12;
            txtTime.setText(String.format("%1d:%02d",model.timeHour,model.timeMinute));
        } else {

            txtTime.setText(String.format("%1d:%02d",model.timeHour,model.timeMinute));
        }

<<<<<<< Updated upstream
        final TextView txtTime = (TextView) convertView.findViewById(R.id.alarm_item_time);
=======
>>>>>>> Stashed changes
        txtTime.setText(String.format("%02d:%02d", model.timeHour, model.timeMinute));

        TextView txtName = (TextView) convertView.findViewById(R.id.am_pm_item);
        txtName.setText(model.am_pm);

        viewHolder.btnToggle.setOnCheckedChangeListener(null);
        viewHolder.btnToggle.setTag(model.id);
        viewHolder.btnToggle.setChecked(model.isEnabled);
        viewHolder.btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
<<<<<<< Updated upstream

                ((MyAlarmScreen) mContext).setAlarmEnabled((Long) buttonView.getTag(), isChecked);
                System.out.println(" Enable Position " + buttonView.getTag());
=======
                ((MyAlarmScreen) mContext).setAlarmEnabled((Long) buttonView.getTag(), isChecked);

>>>>>>> Stashed changes
            }
        });

        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setTag(model.id);
        viewHolder.checkBox.setChecked(model.isVisible);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                       @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isVisible) {
                        ((MyAlarmScreen) mContext).setAlarmVisible((Long) buttonView.getTag(), isVisible);


                             if (isVisible) {
                                 Alarms displayAlarm = new Alarms();
                                 String userID = ParseUser.getCurrentUser().get("fbId").toString();
                                 String alarmTxt = txtTime.getText().toString();
                                 long alarmID = model.id;
                                 displayAlarm.setAlarmID(alarmID);
                                 displayAlarm.setAlarm(alarmTxt);
                                 displayAlarm.setFbId(userID);
                                 displayAlarm.setFriendFBId(userID);
                                 displayAlarm.setAlarmDes(model.name);
                                 displayAlarm.setAM_PM(model.am_pm);
                                 displayAlarm.saveInBackground();
                                 tinydb.putLong("alarmID", alarmID);

                                 ParseQuery<ParseObject> query = new ParseQuery<>("_User");
                                 query.whereEqualTo("fbId", userID);
                                 query.findInBackground(new FindCallback<ParseObject>() {

                                     @Override
                                     public void done(List<ParseObject> alarm, ParseException e) {
                                         if (e == null) {

                                             if(alarm.size() > 0) {
                                                 ParseObject alarmTone = alarm.get(0);
                                                 alarmTone.increment("alarmCount");
                                                 alarmTone.saveInBackground();
                                             }
                                         }

                                     }
                                 });

                                 } else {


                                   ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Alarms");
                                   query.whereEqualTo("alarm_id", model.id);
                                   query.findInBackground(new FindCallback<ParseObject>() {

                                   @Override
                                   public void done(List<ParseObject> alarm, ParseException e) {
                                         if (e == null) {

                                             if(alarm.size() > 0) {
                                                 ParseObject alarmTone = alarm.get(0);
                                                 alarmTone.deleteInBackground();

                                             }
                                         }

                                   }
                                 });
                                 String userID = ParseUser.getCurrentUser().get("fbId").toString();
                                 ParseQuery<ParseObject> Userquery = new ParseQuery<>("_User");
                                 Userquery.whereEqualTo("fbId", userID);
                                 Userquery.findInBackground(new FindCallback<ParseObject>() {

                                     @Override
                                     public void done(List<ParseObject> alarm, ParseException e) {
                                         if (e == null) {

                                             if(alarm.size() > 0) {
                                                 ParseObject alarmTone = alarm.get(0);
                                                 alarmTone.increment("alarmCount",-1);
                                                 alarmTone.saveInBackground();
                                             }
                                         }

                                     }
                                 });
                             }
                        }
        });







        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_sunday), model.getRepeatingDay(AlarmModel.SUNDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_monday), model.getRepeatingDay(AlarmModel.MONDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_tuesday), model.getRepeatingDay(AlarmModel.TUESDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_wednesday), model.getRepeatingDay(AlarmModel.WEDNESDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_thursday), model.getRepeatingDay(AlarmModel.THURSDAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_friday), model.getRepeatingDay(AlarmModel.FRDIAY));
        updateTextColor((TextView) convertView.findViewById(R.id.alarm_item_saturday), model.getRepeatingDay(AlarmModel.SATURDAY));

        viewHolder.options.setTag(model.id);
        viewHolder.options.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((MyAlarmScreen) mContext).startAlarmDetailsActivity((Long) view.getTag());

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Alarms");
                query.whereEqualTo("alarm_id", model.id);
                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> alarm, ParseException e) {
                        if (e == null) {

                            if(alarm.size() > 0) {
                                ParseObject alarmTone = alarm.get(0);
                                alarmTone.deleteInBackground();
                            }
                        }

                    }
                });
            }
        });

        viewHolder.options.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                ((MyAlarmScreen) mContext).deleteAlarm((Long) view.getTag());


                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Alarms");
                query.whereEqualTo("alarm_id", model.id);
                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> alarm, ParseException e) {
                        if (e == null) {

                            if(alarm.size() > 0) {
                                ParseObject alarmTone = alarm.get(0);
                                alarmTone.deleteInBackground();
                            }
                        }

                    }
                });
                return true;
            }
        });

        return convertView;
    }



    private void updateTextColor(TextView view, boolean isOn) {
        if (isOn) {
            view.setTextColor(mContext.getResources().getColor(R.color.primary_dark));
        } else {
            view.setTextColor(Color.BLACK);
        }
    }







}
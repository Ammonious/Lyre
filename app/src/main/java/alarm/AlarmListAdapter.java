package alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

import epimelis.com.lyre.R;
import parse.Alarms;
import parse.ParseConstants;

/**
 * Created by ammonrees on 9/20/14.
 */
public class AlarmListAdapter extends BaseAdapter {

    private Context mContext;
    private List<AlarmModel> mAlarms;
    Alarms displayAlarm = new Alarms();
    ViewHolder viewHolder;


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
        RelativeLayout info;
        CheckBox checkBox;

        Switch btnToggle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        System.out.println("View Position: " + String.valueOf(position));
        final AlarmModel model = (AlarmModel) getItem(position);

        if(convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.alarm_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.visible_toggle);
            viewHolder.btnToggle = (Switch) convertView.findViewById(R.id.alarm_item_toggle);
            convertView.setTag(viewHolder);
        }
        else {
          // viewHolder = (ViewHolder) convertView.getTag();
           viewHolder.btnToggle.getTag();
           viewHolder.checkBox.getTag();
           viewHolder.checkBox.setOnCheckedChangeListener(null);
           viewHolder.btnToggle.setOnCheckedChangeListener(null);
        }

        final TextView txtTime = (TextView) convertView.findViewById(R.id.alarm_item_time);
        txtTime.setText(String.format("%02d:%02d", model.timeHour, model.timeMinute));

        TextView txtName = (TextView) convertView.findViewById(R.id.alarm_item_name);
        txtName.setText(model.name);

        viewHolder.btnToggle.setTag(model.id);
        viewHolder.btnToggle.setChecked(model.isEnabled);
        viewHolder.btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((AlarmSettings) mContext).setAlarmEnabled((Long) buttonView.getTag(), isChecked);

                System.out.println(" Enable Position " + buttonView.getTag());
            }
        });
        viewHolder.checkBox.setTag(model.id);
        viewHolder.checkBox.setChecked(model.isVisible);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isVisible) {
                ((AlarmSettings) mContext).setAlarmVisible((Long) buttonView.getTag(), isVisible);

                System.out.println(" Visible Position " + buttonView.getTag());

                if(isVisible) {

                    String userID = ParseUser.getCurrentUser().get("fbId").toString();
                    String alarmTxt = txtTime.getText().toString();
                    displayAlarm.setAlarm(alarmTxt);
                    displayAlarm.setFbId(userID);
                    displayAlarm.saveInBackground();

                }
                else {

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

        convertView.setTag(Long.valueOf(model.id));
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((AlarmSettings) mContext).startAlarmDetailsActivity(((Long) view.getTag()).longValue());
            }
        });
        convertView.setTag(Long.valueOf(model.id));
        convertView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                ((AlarmSettings) mContext).deleteAlarm((Long) view.getTag());
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
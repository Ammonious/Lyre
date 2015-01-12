package alarm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import com.getbase.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;
import epimelis.com.lyre.R;
import utils.BaseActivity;
import utils.TinyDB;


public class SetAlarmActivity extends BaseActivity {

    private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
    private AlarmModel alarmDetails;
    private EditText edtName;
    private CheckBox chkWeekly;
    private CheckBox chkSunday;
    private CheckBox chkMonday;
    private CheckBox chkTuesday;
    private CheckBox chkWednesday;
    private CheckBox chkThursday;
    private CheckBox chkFriday;
    private CheckBox chkSaturday;
    private TextView txtToneSelection,timeView,AM_PM_VIEW;
    private FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TinyDB tinyDB = new TinyDB(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Typeface tf3 = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Thin.ttf");

        timeView = (TextView) findViewById(R.id.alarm_time_view);
        timeView.setTypeface(tf3);

        AM_PM_VIEW = (TextView) findViewById(R.id.am_pm_view);
        AM_PM_VIEW.setTypeface(tf3);

        mFab = (FloatingActionButton) findViewById(R.id.button_floating_action);
        mFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                setResult(RESULT_OK);
                finish();

                updateModelFromLayout();

                AlarmManagerHelper.cancelAlarms(getBaseContext());

                if (alarmDetails.id < 0) {
                    dbHelper.createAlarm(alarmDetails);
                } else {
                    dbHelper.updateAlarm(alarmDetails);
                }

                AlarmManagerHelper.setAlarms(getBaseContext());





            }

        });

        // *** Set TextView with Current time AM/PM *** \\

        Calendar currentTime = Calendar.getInstance();
        int hours = currentTime.get(Calendar.HOUR_OF_DAY);
        int minutes = currentTime.get(Calendar.MINUTE);
        String am_pms = "";
        if (currentTime.get(Calendar.AM_PM) == Calendar.AM)
            am_pms = "AM";
        else if (currentTime.get(Calendar.AM_PM) == Calendar.PM)
            am_pms = "PM";
        if(hours > 12) {
            hours -= 12;
            timeView.setText(String.format("%1d:%02d", hours, minutes));
        } else if(hours == 0) {
            hours += 12;
            timeView.setText(String.format("%1d:%02d",hours,minutes));
        } else {
            timeView.setText(String.format("%1d:%02d",hours,minutes));
        }

        AM_PM_VIEW.setText(am_pms);

        // ********************************************* \\


        timeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog(SetAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                        String timeSet = "";
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            timeSet = "PM";
                        } else if (selectedHour == 0) {
                            selectedHour += 12;
                            timeSet = "AM";
                        } else if (selectedHour == 12)
                            timeSet = "PM";
                        else
                            timeSet = "AM";

                        String min = "";
                        if (selectedMinute < 10)
                            min = "0" + selectedMinute ;
                        else
                            min = String.valueOf(selectedMinute);

                        // Append in a StringBuilder
                        String aTime = new StringBuilder().append(selectedHour).append(':')
                                .append(min ).append(" ").toString();
                        timeView.setText(aTime);
                        AM_PM_VIEW.setText(timeSet);

                        if(timeSet == "PM" && selectedHour != 12) {

                            tinyDB.putInt("hour", selectedHour + 12);
                            tinyDB.putInt("min", selectedMinute);
                        } else if(timeSet == "AM" && selectedHour == 12) {
                            tinyDB.putInt("hour", selectedHour - 12);
                            tinyDB.putInt("min", selectedMinute);
                        } else {
                            tinyDB.putInt("hour", selectedHour);
                            tinyDB.putInt("min", selectedMinute);
                        }

                    }

                },hour, minute, false);   //No 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();




            }
        });

        edtName = (EditText) findViewById(R.id.alarm_details_name);
        chkWeekly = (CheckBox) findViewById(R.id.alarm_details_repeat_weekly);
        chkSunday = (CheckBox) findViewById(R.id.alarm_details_repeat_sunday);
        chkMonday = (CheckBox) findViewById(R.id.alarm_details_repeat_monday);
        chkTuesday = (CheckBox) findViewById(R.id.alarm_details_repeat_tuesday);
        chkWednesday = (CheckBox) findViewById(R.id.alarm_details_repeat_wednesday);
        chkThursday = (CheckBox) findViewById(R.id.alarm_details_repeat_thursday);
        chkFriday = (CheckBox) findViewById(R.id.alarm_details_repeat_friday);
        chkSaturday = (CheckBox) findViewById(R.id.alarm_details_repeat_saturday);
        txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);

        long id = getIntent().getExtras().getLong("id");

        if (id == -1) {
            alarmDetails = new AlarmModel();
        } else {
            alarmDetails = dbHelper.getAlarm(id);

            if(alarmDetails.timeHour > 12){
                alarmDetails.timeHour -= 12;
                timeView.setText(String.format("%1d:%02d", alarmDetails.timeHour, alarmDetails.timeMinute));
            }
            else if (alarmDetails.timeHour == 0) {

                alarmDetails.timeHour += 12;
                timeView.setText(String.format("%1d:%02d",alarmDetails.timeHour,alarmDetails.timeMinute));
            } else {

                timeView.setText(String.format("%1d:%02d",alarmDetails.timeHour,alarmDetails.timeMinute));
            }
           // timeView.setText(alarmDetails.timeHour + ":" + alarmDetails.timeMinute);
            AM_PM_VIEW.setText(alarmDetails.am_pm);
            edtName.setText(alarmDetails.name);
            chkWeekly.setChecked(alarmDetails.repeatWeekly);
            chkSunday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
            chkMonday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
            chkTuesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
            chkWednesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
            chkThursday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
            chkFriday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.FRDIAY));
            chkSaturday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));

            txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
        }

        final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, 100);

            }
        });
    }
    @Override protected int getLayoutResource() {
        return R.layout.set_alarm;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK)
                {
                    alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));


            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home

                this.finish();
                return true;

            case R.id.action_save_alarm_details: {

            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateModelFromLayout() {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        alarmDetails.timeHour = tinyDB.getInt("hour");
        alarmDetails.timeMinute = tinyDB.getInt("min");
        alarmDetails.am_pm = AM_PM_VIEW.getText().toString();
        alarmDetails.name = edtName.getText().toString();
        alarmDetails.repeatWeekly = chkWeekly.isChecked();
        alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, chkSunday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.MONDAY, chkMonday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, chkTuesday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, chkWednesday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, chkThursday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.FRDIAY, chkFriday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, chkSaturday.isChecked());
        alarmDetails.isEnabled = true;
        alarmDetails.isVisible = false;

    }



}

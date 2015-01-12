package alarm;



import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.makeramen.RoundedImageView;
import com.parse.ParseUser;
import com.quentindommerc.superlistview.SuperListview;
import epimelis.com.lyre.MainActivity;
import epimelis.com.lyre.R;
import utils.BaseActivity;
import utils.ImageLoader;


public class MyAlarmScreen extends BaseActivity {



    RoundedImageView Profile;
    TextView Username;
    private AlarmListAdapter mAdapter;
    ImageLoader imageLoader;
    public SuperListview mListView;
    public static final String FAB_VIEW = "AlarmSettings:FAB";

    private AlarmDBHelper dbHelper = AlarmDBHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = new ImageLoader(getBaseContext());
        String url = String.format(
                "https://graph.facebook.com/%s/picture?width=150&height=150",ParseUser.getCurrentUser().get("fbId"));


        final RoundedImageView Profile = (RoundedImageView) findViewById(R.id.userProfilePicture);
        imageLoader.DisplayImage(url, Profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here

       // final View view = findViewById(R.id.userProfilePicture);

            Profile.post(new Runnable() {

            @Override
            public void run() {
                int width = Profile.getWidth();
                int height = Profile.getHeight();

                // get the center for the clipping circle
                int cx = (Profile.getLeft() + Profile.getRight()) / 2;
                int cy = (Profile.getTop() + Profile.getBottom()) / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(width, height);

                Animator animator =
                        ViewAnimationUtils.createCircularReveal(Profile, cx, cy, 0, finalRadius);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(1500);

              /*  if(Animator.LOLLIPOP){
                    android.animation.Animator a = animator.getNativeAnimator();
                }else{
                    ObjectAnimator a = (ObjectAnimator)
                            animator.getSupportAnimator();
                    a.setAutoCancel(true);
                } */

                animator.start();
            }

            });
        } else {
            // Implement this feature without material design
        }

       mListView = (SuperListview) findViewById(R.id.listView);
        Typeface tf3 = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Roboto-Thin.ttf");


        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.button_floating_action);
        ViewCompat.setTransitionName(mFab, FAB_VIEW);
        mFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                startAlarmDetailsActivity(-1);

            }

        });
        Username = (TextView) findViewById(R.id.username);
        Username.setTypeface(tf3);
        Username.setText(ParseUser.getCurrentUser().get("name").toString());

        mAdapter = new AlarmListAdapter(this, dbHelper.getAlarms());

        mListView.setAdapter(mAdapter);


    }

    @Override protected int getLayoutResource() {
        return R.layout.users_alarms;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_settings, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home

                Intent intentHome = new Intent(this, MainActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHome);
                setResult(RESULT_OK);
                finish();
                this.finish();
                return true;


            case R.id.friend:
                Intent intent = new Intent(this, MyAlarmScreen.class);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mAdapter.setAlarms(dbHelper.getAlarms());
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setAlarmEnabled(long id, boolean isEnabled) {
        AlarmManagerHelper.cancelAlarms(this);

        AlarmModel model = dbHelper.getAlarm(id);
        model.isEnabled = isEnabled;
        dbHelper.updateAlarm(model);

        AlarmManagerHelper.setAlarms(this);
    }

    public void setAlarmVisible(long id, boolean isVisible) {


        AlarmModel model = dbHelper.getAlarm(id);
        model.isVisible = isVisible;
        dbHelper.updateAlarm(model);


    }



    public void startAlarmDetailsActivity(long id) {
        Intent intent = new Intent(this, SetAlarmActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 0);
    }

    public void deleteAlarm(long id) {
        final long alarmId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please confirm")
                .setTitle("Delete Alarm?")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel Alarms
                        AlarmManagerHelper.cancelAlarms(getBaseContext());
                        //Delete alarm from DB by id
                        dbHelper.deleteAlarm(alarmId);
                        //Refresh the list of the alarms in the adaptor
                        mAdapter.setAlarms(dbHelper.getAlarms());
                        //Notify the adapter the data has changed
                        mAdapter.notifyDataSetChanged();
                        //Set the alarms
                        AlarmManagerHelper.setAlarms(getBaseContext());
                    }
                }).show();
    }
    public static void launch(BaseActivity activity, View transitionView) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, FAB_VIEW);
        Intent intent = new Intent(activity, MyAlarmScreen.class);

        ActivityCompat.startActivityForResult(activity, intent, 2, options.toBundle());


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
           // Intent intentHome = new Intent(this, MainActivity.class);
           //startActivity(intentHome);
            setResult(MainActivity.RESULT_OK);
            finishActivity(MainActivity.REQUEST_CODE);
        }
        return super.onKeyDown(keyCode, event);
    }
  /*  private void revealImageCircular() {
        int x = (Profile.getLeft() + Profile.getRight()) / 2;
        int y = (Profile.getTop() + Profile.getBottom()) / 2;
        int radius = Profile.getWidth();

        Animator anim =
                ViewAnimationUtils.createCircularReveal(Profile, x, y, 0, radius);

        anim.setDuration( 1000 );
        anim.addListener( new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Profile.setVisibility(View.VISIBLE);
            }
        });

        anim.start();
    }

*/
}

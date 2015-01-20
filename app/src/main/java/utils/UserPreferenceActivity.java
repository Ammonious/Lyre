package utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import epimelis.com.lyre.MainActivity;
import epimelis.com.lyre.R;

/**
 * Created by ammonrees on 1/11/15.
 */
public class UserPreferenceActivity extends PreferenceActivity {

    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.pref_general);


    }



    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu);
        inflater.inflate(R.menu.friend_list, (Menu) menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intentHome = new Intent(this, MainActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHome);
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
}

package utils;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import epimelis.com.lyre.R;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;

/**
 * Created by ammonrees on 10/22/14.
 */
public abstract class BaseActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private ImageView iconView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iconView = (ImageView) toolbar.findViewById(R.id.icon);

      //  toolbar.setLogo(R.drawable.logo);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected abstract int getLayoutResource();

    protected void setActionBarIcon(int iconRes) {

        toolbar.setNavigationIcon(iconRes);
    }


}

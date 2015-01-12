package adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import epimelis.com.lyre.R;
import parse.Alarms;

/**
 * Created by ammonrees on 9/6/14.
 */
public class AlarmAdapter extends ArrayAdapter<Alarms> {
    private Context mContext;
    private List<Alarms> mAlarms;

    ViewHolder viewHolder;

    public AlarmAdapter(Context context, List<Alarms> objects) {
        super(context, R.layout.alarm_row, objects);
        this.mContext = context;
        this.mAlarms = objects;
    }

    private class ViewHolder
    {
        TextView alarm;
        RelativeLayout music,record;
    }

    public View getView(final int position, View convertView, ViewGroup parent){

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Thin.ttf");
        //  Typeface tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface tf3 = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoSlab-Regular.ttf");

        if(convertView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.alarm_row, null);
            viewHolder = new ViewHolder();
            viewHolder.alarm = (TextView) convertView.findViewById(R.id.alarm);
            viewHolder.music = (RelativeLayout) convertView.findViewById(R.id.music);
            viewHolder.record = (RelativeLayout) convertView.findViewById(R.id.record);

            convertView.setTag( viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Alarms parseAlarm = mAlarms.get(position);

        TextView recipeView = (TextView) convertView.findViewById(R.id.alarm);
        recipeView.setText(parseAlarm.getAlarm());
        recipeView.setTypeface(tf);

        viewHolder.music.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

            }

        });

        viewHolder.record.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

            }
        });

        return convertView;
    }
}

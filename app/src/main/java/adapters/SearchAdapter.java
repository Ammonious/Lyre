package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import epimelis.com.lyre.R;

/**
 * Created by ammonrees on 9/6/14.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements View.OnClickListener{

    private ArrayList<String> mDataset;
    private static Context sContext;

    // Adapter's Constructor
    public SearchAdapter(Context context, ArrayList<String> myDataset) {
        mDataset = myDataset;
        sContext = context;
    }

    // Create new views. This is invoked by the layout manager.
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_row, parent, false);

        // Set the view to the ViewHolder
        ViewHolder holder = new ViewHolder(v);
        holder.mAlarm.setOnClickListener(SearchAdapter.this);

        holder.mAlarm.setTag(holder);

        return holder;
    }

    // Replace the contents of a view. This is invoked by the layout manager.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mAlarm.setText(String.valueOf(position) + ". ");
        // Get element from your dataset at this position and set the text for the specified element
      //  holder.mNameTextView.setText(mDataset.get(position));

        // Set the color to red if row is even, or to green if row is odd.
      /*  if (position % 2 == 0) {
            holder.mNumberRowTextView.setTextColor(Color.RED);
        } else {
            holder.mNumberRowTextView.setTextColor(Color.GREEN);
        }*/
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Implement OnClick listener. The clicked item text is displayed in a Toast message.
    @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (view.getId() == holder.mRecord.getId()) {

        }
    }



    // Create the ViewHolder class to keep references to your views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mAlarm;
        public ImageView mRecord,mMusic;

        /**
         * Constructor
         * @param v The container view which holds the elements from the row item xml
         */
        public ViewHolder(View v) {
            super(v);

            mAlarm = (TextView) v.findViewById(R.id.alarm);
            mRecord = (ImageView) v.findViewById(R.id.record);
            mMusic = (ImageView) v.findViewById(R.id.music);

        }
    }
}

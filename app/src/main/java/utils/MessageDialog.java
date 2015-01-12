package utils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import epimelis.com.lyre.R;

/**
 * Created by ammonrees on 12/31/14.
 */
public class MessageDialog extends DialogFragment {

    Button submit, cancel;
    EditText message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.message_dialog, null);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);

        message = (EditText) dialog.findViewById(R.id.set_message);


        submit = (Button) dialog.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Toast.makeText(getActivity(), "Message Set!", Toast.LENGTH_LONG).show();

                Bundle bundle =getArguments();

                String objectId = bundle.getString("objectId");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Alarms");
                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                    public void done(ParseObject alarm, ParseException e) {
                        if (e == null) {
                            final String messageText = message.getText().toString();
                            alarm.put("message",messageText);
                            alarm.saveInBackground();
                        }
                    }
                });
                final String username = ParseUser.getCurrentUser().get("name").toString();

                // can get BUNDLE we can retrieve FriendID, alarmTime and alarmAM \\
                TinyDB tinyDB = new TinyDB(getActivity());
                String friendId = tinyDB.getString("pushfriendId");
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("fbId", tinyDB.getString("pushfriendId"));

                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setMessage("Your Alarm at " + tinyDB.getString("alarmTime") + tinyDB.getString("alarmAM") + " was set by " + username);
                push.sendInBackground();
                dismiss();

            }
        });

        cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                dismiss();

            }
        });


        return dialog;
    }

    public void uploadMessage(){



    }

}

package utils;


import android.app.Dialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import epimelis.com.lyre.R;
import mehdi.sakout.fancybuttons.FancyButton;
import parse.Alarms;

/**
 * Created by ammonrees on 11/22/14.
 */
public class RecordDialog extends DialogFragment {

    MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;
    Button cancel,submit;
    TextView title;
    EditText message;
    MaterialRippleLayout stop,record,play;

    byte[] byteArray;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);


        outputFile = Environment.getExternalStorageDirectory().
        getAbsolutePath() + "/alarmrecording.3gpp";

        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myRecorder.setOutputFile(outputFile);


        message = (EditText) dialog.findViewById(R.id.alarm_message);

        title = (TextView) dialog.findViewById(R.id.title_view);

        record = (MaterialRippleLayout)dialog.findViewById(R.id.record_alarm);
        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                start(v);
            }
        });
        stop = (MaterialRippleLayout)dialog.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stop(v);

            }
        });

        play = (MaterialRippleLayout)dialog.findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                play(v);
            }
        });

        submit = (Button)dialog.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                uploadSound();
                Toast.makeText(getActivity(), "Friend's Alarm Set Successfully!", Toast.LENGTH_LONG).show();
                final String username = ParseUser.getCurrentUser().get("name").toString();


                TinyDB tinyDB = new TinyDB(getActivity());
                String friendId = tinyDB.getString("pushfriendId");
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("fbId",tinyDB.getString("pushfriendId"));

                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setMessage("Your Alarm at " + tinyDB.getString("alarmTime") + tinyDB.getString("alarmAM") + " was set by " + username);
                push.sendInBackground();
                dismiss();

            }
        });

        cancel = (Button)dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                dismiss();

            }
        });

        return dialog;
    }

    public void start(View view) {
        if (myRecorder == null) {
            myRecorder = new MediaRecorder();
            myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myRecorder.setOutputFile(outputFile);

            try {
                myRecorder.prepare();
                myRecorder.start();
            } catch (IllegalStateException e) {
                // prepare: it is called after start() or before setOutputFormat()
                e.printStackTrace();
            } catch (IOException e) {
                // prepare() fails
                e.printStackTrace();
            }
            title.setText("Recording...");

            Toast.makeText(getActivity(), "Start recording...",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                myRecorder.prepare();
                myRecorder.start();
            } catch (IllegalStateException e) {
                // prepare: it is called after start() or before setOutputFormat()
                e.printStackTrace();
            } catch (IOException e) {
                // prepare() fails
                e.printStackTrace();
            }
            title.setText("Recording...");

            Toast.makeText(getActivity(), "Start recording...",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void stop(View view){

        title.setText("Recording Stopped...");
        try {
            myRecorder.stop();
            myRecorder.reset();
            myRecorder.release();
            myRecorder = null;



            Toast.makeText(getActivity(), "Recording Stopped...",
                    Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            //  it is called before start()
            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received
            e.printStackTrace();
        }
    }

    public void play(View view) {
        try{
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(outputFile);
            myPlayer.prepare();
            myPlayer.start();



            title.setText("Playing...");


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void uploadSound(){
        final String FbId = ParseUser.getCurrentUser().get("fbId").toString();
        final String username = ParseUser.getCurrentUser().get("name").toString();
        Bundle bundle = getArguments();
        final String messageText = message.getText().toString();
        String objectId = bundle.getString("objectId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alarms");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject alarm, ParseException e) {
                if (e == null) {

                    File file = new File(outputFile);
                    byte[] byteArray = new byte[(int) file.length()];
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        fileInputStream.read(byteArray);
                    } catch (FileNotFoundException d) {
                        System.out.println("File Not Found.");
                        e.printStackTrace();
                    }
                    catch (IOException e1) {
                        System.out.println("Error Reading The File.");
                        e1.printStackTrace();
                    }
                    byte[] data = outputFile.getBytes();
                    ParseFile parseFile = new ParseFile("alarm.3gpp", byteArray);

                    parseFile.saveInBackground();
                    alarm.put("soundfile", parseFile);
                    alarm.put("name",username);
                    alarm.put("friendId", FbId);
                    alarm.put("message",messageText);
                    alarm.saveInBackground();
                }
            }
        });
        String friendId = bundle.getString("friendFB");
        ParseQuery<ParseObject> Userquery = new ParseQuery<>("_User");
        Userquery.whereEqualTo("fbId", friendId);
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
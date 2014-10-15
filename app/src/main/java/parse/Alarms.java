package parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;

/**
 * Created by ammonrees on 9/6/14.
 */
@ParseClassName("Alarms")
public class Alarms extends ParseObject {

    public Alarms(){



    }



    public String getAlarm(){
        return getString("alarm");
    }
    public void setAlarm(String Alarm){
        put("alarm", Alarm);
    }

    public ParseFile getRecording(){
        return getParseFile("record");
    }
    public void setRecording(ParseFile Record){
        put("record", Record);
    }

    public ParseFile getMusic(){
        return getParseFile("music");
    }
    public void setMusic(ParseFile Music){
        put("music", Music);
    }

    public String getFbId(){
        return getString("facebookId");
    }
    public void setFbId(String facebookId){
        put("facebookId", facebookId);
    }
}

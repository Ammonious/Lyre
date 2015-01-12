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

    public Long getAlarmID(){
        return getLong("alarm_id");
    }
    public void setAlarmID(Long AlarmID){
        put("alarm_id", AlarmID);
    }

    public ParseFile getSoundfile(){
        return getParseFile("soundfile");
    }
    public void setSoundfile(ParseFile soundfile){
        put("soundfile", soundfile);
    }

    public String getFriendId(){
        return getString("friendId");
    }
    public void setFriendFBId(String friendId){
        put("friendId", friendId);
    }

    public String getAM_PM(){
        return getString("am_pm");
    }
    public void setAM_PM(String AM_PM){
        put("am_pm", AM_PM);
    }

    public String getAlarmDes(){
        return getString("description");
    }
    public void setAlarmDes(String Description){
        put("description", Description);
    }

    public String getFbId(){
        return getString("facebookId");
    }
    public void setFbId(String facebookId){
        put("facebookId", facebookId);
    }
}

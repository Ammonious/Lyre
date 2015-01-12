package parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by ammonrees on 11/1/14.
 */

@ParseClassName("FriendRequest")
public class Friends extends ParseObject {

    public Friends(){



    }

    public String getUserId(){
        return getString("requestedFrom");
    }
    public void setUserId(String UserID){
        put("requestedFrom", UserID);
    }

    public String getFriendId(){
        return getString("friendRequested");
    }
    public void setFriendId(String FriendID){
        put("friendRequested", FriendID);
    }

    public String getfriendName(){
        return getString("friendName");
    }
    public void setfriendName(String friendName){
        put("friendName", friendName);
    }

    public String getName(){
        return getString("requestedName");
    }
    public void setName(String name){
        put("requestedName", name);
    }

    public String getAccepted(){
        return getString("Accepted");
    }
    public void setAccepted(String accepted){
        put("Accepted", accepted);
    }

}

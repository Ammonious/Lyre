package parse;

import com.parse.ParseClassName;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by ammonrees on 8/31/14.
 */
@ParseClassName("_User")
public class Users extends ParseUser {

    public Users(){



    }



    public String getUserId(){
        return getString("fbId");
    }
    public void setUserId(String UserID){
        put("fbId", UserID);
    }

    public String getName(){
        return getString("name");
    }
    public void setName(String name){
        put("name", name);
    }





}

package mebeidcreations.apps.livegosee;

import java.io.Serializable;

/**
 * Created by Mazen Ebeid on 14/01/2017.
 */

public class UserInfo implements Serializable {

    public UserInfo() {

    }

    String user_id, user_name, user_dob, user_country;
    int user_trip_count;

    public String getuser_id() {
        return user_id;
    }

    public String getuser_name() {

        return user_name;
    }

    public String getuser_dob() {

        return user_dob;
    }
    public int getuser_trip_count()
    {
        return user_trip_count;
    }


    public String getuser_country() {
        return user_country;
    }

}
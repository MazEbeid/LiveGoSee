package mebeidcreations.apps.livegosee;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Mazen Ebeid on 30/03/2017.
 */


public class TripInfo implements Serializable {

    public TripInfo() {

    }

    String trip_thumbnail_stamp, tripuser_id, trip_id ,trip_name, trip_origin, trip_destination, trip_from_date,
            trip_to_date,origin_lat, origin_lng, destination_lat, destination_lng, trip_privacy, trip_search_string;


    public String gettripuser_id() {

        return tripuser_id;
    }

    public String gettrip_id() {
        return trip_id;
    }

    public String gettrip_name() {

        return trip_name;
    }

    public String gettrip_origin() {

        return trip_origin;
    }

    public String getorigin_lat()
    {
        return origin_lat;
    }
    public String getorigin_lng(){return  origin_lng;}
    public String getdestination_lat()
    {
        return destination_lat;
    }
    public String getdestination_lng(){return  destination_lng;}
    public String gettrip_privacy() {return trip_privacy;}

    public String gettrip_destination() {
        return trip_destination;
    }

    public String gettrip_from_date() {
        return trip_from_date;
    }

    public String gettrip_to_date() {
        return trip_to_date;
    }


    public void setttrip_thumbnail_stamp(String stamp)

    {
        trip_thumbnail_stamp = stamp;
    }

    public String gettrip_thumbnail_stamp()
    {
        return trip_thumbnail_stamp;
    }
    public String getTranslatedTripMonthYear()
    {

        String translatedMonth = "";

        String date []  = gettrip_from_date().split("/");
        Log.d("MONTH",date[1]);
        Log.d("YEAR",date[2]);

        if(date[1].equals("01"))
        {
            translatedMonth = "January";
        }
        else if(date[1].equals("02"))
        {
            translatedMonth = "February";
        }

        else if(date[1].equals("03"))
        {
            translatedMonth = "March";
        }

        else if(date[1].equals("04"))
        {
            translatedMonth = "April";
        }
        else if(date[1].equals("05"))
        {
            translatedMonth = "May";
        }
        else if(date[1].equals("06"))
        {
            translatedMonth = "June";
        }
        else if(date[1].equals("07"))
        {
            translatedMonth = "July";
        }
        else if(date[1].equals("08"))
        {
            translatedMonth = "August";
        }
        else if(date[1].equals("09"))
        {
            translatedMonth = "September";
        }
        else if(date[1].equals("10"))
        {
            translatedMonth = "October";
        }

        else if(date[1].equals("11"))
        {
            translatedMonth = "November";
        }
        else if(date[1].equals("12"))
        {
            translatedMonth = "December";
        }


        return translatedMonth+", "+date[2];
    }

    public String getCityCountry()
    {
        String cityCountry = "";
        String[] destination = gettrip_destination().split(",");
        if(destination.length==1)
        {
            cityCountry = destination[0];
        }
        else
        {
            cityCountry = destination[destination.length-2]+" , "+destination[destination.length-1];
            Log.d("DESTINATION []",cityCountry);


        }

        return cityCountry;
    }

    public String gettrip_search_string()
    {
        return trip_search_string;
    }

}
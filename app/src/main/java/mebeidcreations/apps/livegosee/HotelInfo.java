package mebeidcreations.apps.livegosee;

/**
 * Created by Mazen Ebeid on 29/09/2017.
 */

public class HotelInfo {

    String entry_type, hotel_id,hotel_name, hotel_address, hotel_checkin, hotel_checkout,hotel_review,hotel_lat, hotel_lng, hotel_rating;

    public HotelInfo()
    {
        //empty constructor for firebase
    }

    public String gethotel_address() {
        return hotel_address;
    }

    public String gethotel_checkin() {
        return hotel_checkin;
    }

    public String gethotel_checkout() {
        return hotel_checkout;
    }

    public String gethotel_id() {
        return hotel_id;
    }

    public String gethotel_name()
    {
        return hotel_name;
    }

    public String gethotel_lat() {
        return hotel_lat;
    }

    public String gethotel_lng()
    {
        return hotel_lng;
    }

    public String gethotel_review()
    {
        return hotel_review;
    }

    public String gethotel_rating()
    {
        return hotel_rating;
    }
    public String getentry_type()
    {
        return entry_type;
    }

}

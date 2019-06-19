package mebeidcreations.apps.livegosee;

/**
 * Created by Mazen Ebeid on 16/01/2018.
 */

public class TransportMode {

    public TransportMode()
    {

    }
    String transport_origin, transport_destination, transport_date, transport_type, transport_notes,
            transport_origin_lat, transport_origin_lng, transport_destination_lat, transport_destination_lng, transport_time, transport_notification_interval, transport_notification,transport_chipId;

    public String gettransport_date() {
        return transport_date;
    }

    public String gettransport_destination() {
        return transport_destination;
    }

    public String gettransport_notes() {
        return transport_notes;
    }

    public String gettransport_origin() {
        return transport_origin;
    }

    public String gettransport_type() {
        return transport_type;
    }

    public String gettransport_destination_lat() {
        return transport_destination_lat;
    }

    public String gettransport_destination_lng() {
        return transport_destination_lng;
    }

    public String gettransport_origin_lat() {
        return transport_origin_lat;
    }

    public String gettransport_origin_lng() {
        return transport_origin_lng;
    }
    public String gettransport_time()
    {
        return transport_time;
    }

    public String gettransport_notification_interval()
    {
        return transport_notification_interval;
    }

    public String gettransport_notification()
    {
        return transport_notification;
    }

    public String gettransport_chipId() {
        return transport_chipId;
    }
}

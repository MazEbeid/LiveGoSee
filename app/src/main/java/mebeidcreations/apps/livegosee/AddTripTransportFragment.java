package mebeidcreations.apps.livegosee;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static mebeidcreations.apps.livegosee.AddNewTripFragment.newTripId;
import static mebeidcreations.apps.livegosee.AddNewTripFragment.tripFromDate;
import static mebeidcreations.apps.livegosee.AddNewTripFragment.tripToDate;
import static mebeidcreations.apps.livegosee.Helper.showDatePicker;


public class AddTripTransportFragment extends Fragment{

    static View ChooseTransportModeLayout, DefineTransportRouteLayout;
    FrameLayout TransportLayoutContainer;
    TextView backButton;
    ImageButton walkTransport,bicycleTransport, planeTransport,seaPlaneTransport, cabTransport,bikeTransport,mertoTransport,
                busTransport, camperTransport,trainTransport, tubeTransport, carTransport,ferryTransport, boatTransport, cableTransport
                ,sailTransport, cruiseTransport, chopperTransport, balloonTransport;
    FirebaseDBHandler firebaseDBHandler;
    static TextView numberOfTransportAddedText;
    static AutoCompleteTextView transportOrigin,transportDestination;
    static EditText transportNotes;
    static ImageButton transportDate;
    static FragmentTransaction fragmentTransaction;
    static TextView transportDateText,transportTimeText;
    static LinearLayout chipsContainer;
    static List<View> chipViewsList = new ArrayList<>();
    static List <TransportMode> transportModesObjectList = new ArrayList<TransportMode>();
    Spinner notificationSpinner;
    ImageButton notificationButton, transportTimeButton;
    TextView notifcationToggleText;
    static SwitchCompat notificationToggle;
    LinearLayout chipLinearLayout;
    static boolean originFlag = false;
    static boolean destFlag = false;
    static boolean notificationFlag = false;
    static String destLat, destLng, originLat, originLng;
    static LatLng originLatLng, destinationLatLng;
    static TextView titleText, addDetailButton;
    static String chipType;
    static int chipViewID =0;
    static String chipId = "";
    static boolean choose = true;
    static FragmentManager fragmentManager;
    static String notificationIntervalString = "";
    static TransportMode transportModeToEdit;
    static AlertDialogAction transportDefineAlertAction;
    static AlertDialogAction transportChooseAlertAction;


    ///////////////////////////////On create Method shite
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_add_trip_transport_card, container, false);
        firebaseDBHandler = new FirebaseDBHandler(getActivity().getApplicationContext());
        Log.d("BACK_TRANSPORT", MainActivity.BACK_NAVIGATION);
        MainActivity.BACK_NAVIGATION = "SHOW_MORE_TRIP_OPTIONS";
        transportDefineAlertAction = new AlertDialogAction() {
            @Override
            public void positiveAction() {
                showChooseModesLayout();
                resetDefineTransportFields();
            }

            @Override
            public void negativeAction() {

            }
        };

        transportChooseAlertAction = new AlertDialogAction() {
            @Override
            public void positiveAction() {

                AddFragment(new MyTripsListFragment(),R.id.main_activity_fragments_container);
            }

            @Override
            public void negativeAction() {

            }
        };


        Log.d("TRANSPORT_OBJECTS_LIST",""+transportModesObjectList.size());
        Log.d("CHIP_VIEW_LIST", ""+chipViewsList.size());

        titleText = (TextView)rootView.findViewById(R.id.title_text);
        titleText.setText("Getting There");


        backButton = (TextView)rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d("BACK", "Choose: "+choose);

                if(choose==true)
                {
                    AddFragment(new AddTripOptionsFragment(), R.id.main_activity_fragments_container);
                }
                else if(choose == false)
                {
                    showChooseModesLayout();
                }
            }
        });

        TransportLayoutContainer = (FrameLayout)rootView.findViewById(R.id.transpot_layout_container); //Add transport layout container to host the two different views
        ChooseTransportModeLayout = getLayoutInflater().inflate(R.layout.choose_transport_mode_layout,null); //init chhoose transport mode layout
        DefineTransportRouteLayout = getLayoutInflater().inflate(R.layout.define_transport_mode,null); // init define the transport layout
        TransportLayoutContainer.addView(ChooseTransportModeLayout);                                       //add choose layout as default
       TransportLayoutContainer.addView(DefineTransportRouteLayout);                                      //Although the view is invisible it could still be added to the layout container
        DefineTransportRouteLayout.setVisibility(View.INVISIBLE);



        initialiseViews();


        addDetailButton = (TextView)rootView.findViewById(R.id.add_detail_button);
        addDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(addDetailButton.getText().toString().equals("ADD"))
                {
                    List<AutoCompleteTextView> locations = new ArrayList<AutoCompleteTextView>();
                    locations.add(AddTripTransportFragment.transportOrigin);
                    locations.add(AddTripTransportFragment.transportDestination);
                    if(Helper.validateTransportFields(transportDateText,transportTimeText,locations,tripFromDate,tripToDate))
                    {
                        Toast.makeText(getActivity().getApplicationContext(), "add transport", Toast.LENGTH_SHORT).show();
                        showChooseModesLayout();
                        transportModesObjectList.add(CreateTransportModeObject(chipType)); //creates a transport object and add it to objects list
                        titleText.setText("Getting There");
                        addDetailButton.setText("DONE");
                        addDetailButton.setVisibility(View.VISIBLE);
                        if(transportModesObjectList.size()>1)
                        {
                            numberOfTransportAddedText.setText("You have added "+transportModesObjectList.size()+" transports");
                        }
                        else
                        {
                            numberOfTransportAddedText.setText("You have added "+transportModesObjectList.size()+" transport");
                        }


                    }
                    else
                    {
                        Toast.makeText(getActivity().getApplicationContext(), "Failed to add transport", Toast.LENGTH_SHORT).show();
                    }

                }


                else if(addDetailButton.getText().toString().equals("DONE"))//DONE
                {
                    Log.d("TRIP_JOURNEY","DONE adding detail");
                    titleText.setText("New Trip");
                   Log.d("TRIP", "ID: "+ newTripId);

                    AddFragment(new AddTripOptionsFragment(),R.id.main_activity_fragments_container);

                }
                else if(addDetailButton.getText().toString().equals("SAVE"))
                {
                    transportModeToEdit.transport_origin = transportOrigin.getText().toString();
                    transportModeToEdit.transport_destination = transportDestination.getText().toString();
                    transportModeToEdit.transport_date = transportDateText.getText().toString();
                    transportModeToEdit.transport_notes = transportNotes.getText().toString();
                    transportModeToEdit.transport_destination_lat = destLat;
                    transportModeToEdit.transport_destination_lng = destLng;
                    transportModeToEdit.transport_origin_lat = originLat;
                    transportModeToEdit.transport_origin_lng = originLng;
                    transportModeToEdit.transport_time = transportTimeText.getText().toString();
                    transportModeToEdit.transport_notification = ""+notificationFlag;
                    transportModeToEdit.transport_notification_interval = notificationIntervalString;
                    showChooseModesLayout();
                    addDetailButton.setText("DONE");
                }
                Log.d("CHIPS_CONTAINER", "REFRESHED DRAW "+transportModesObjectList.size()+" chips");
                drawTransportChips(transportModesObjectList,getLayoutInflater(),getResources(),getActivity());
            }




                    });


        return rootView;
    }

    public static TransportMode CreateTransportModeObject(final String tag)
    {
        Calendar cal1 = Calendar.getInstance();
        String stamp = cal1.get(Calendar.HOUR)+""+cal1.get(Calendar.MINUTE)+""+cal1.get(Calendar.SECOND);
        TransportMode transportMode = new TransportMode();
        Log.d("Button",tag);
        transportMode.transport_type = tag;
        transportMode.transport_origin = transportOrigin.getText().toString();
        transportMode.transport_destination = transportDestination.getText().toString();
        transportMode.transport_date = transportDateText.getText().toString();
        transportMode.transport_notes = transportNotes.getText().toString();
        transportMode.transport_destination_lat = destLat;
        transportMode.transport_destination_lng = destLng;
        transportMode.transport_origin_lat = originLat;
        transportMode.transport_origin_lng = originLng;
        transportMode.transport_time = transportTimeText.getText().toString();
        transportMode.transport_notification = ""+notificationFlag;
        transportMode.transport_notification_interval = notificationIntervalString;
        transportMode.transport_chipId = stamp;
        Log.d("CHIP_ID",stamp);
        return transportMode;

    }

    public static void showDefineLayout(String tag, boolean populate)
    {
        MainActivity.BACK_NAVIGATION = "SHOW_LOSE_TRANSPORT_DIALOG";
        Log.d("POPULATE", ""+populate);
        choose = false;
        resetDefineTransportFields();
        DefineTransportRouteLayout.setVisibility(View.VISIBLE);
        ChooseTransportModeLayout.setVisibility(View.INVISIBLE);
        titleText.setText(tag);
        chipType = tag;
        Log.d("CHIPTYPE",tag);
        addDetailButton.setText("ADD");
       if(populate)
        {
            addDetailButton.setText("SAVE");
            Log.d("POPULATE", ""+ chipViewID);
            Log.d("TRANSPORT_OBJECTS_LIST", ""+transportModesObjectList.size());
            for(TransportMode transportMode:transportModesObjectList)
            {
                if(transportMode.gettransport_chipId().equals(chipId))
                {
                    Log.d("TRANSPORT_OBJECTS_LIST", "Found transport to be populated");
                    populateFields(transportMode);
                    transportModeToEdit = transportMode;

                }
            }
        }

    }
    public void initialiseViews()
    {
        //Init DEFINE layout views

        transportOrigin = (AutoCompleteTextView) DefineTransportRouteLayout.findViewById(R.id.transport_origin_field);
        transportOrigin.setAdapter(MainActivity.placeAutocompleteAdapter);
        transportOrigin.setOnItemClickListener(transportAutoCompleteListener);
        transportOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==true)
                {
                    destFlag = false;
                    originFlag = true;
                    Log.d("FLAG", "ORIGIN");
                }
            }
        });

        transportDestination = (AutoCompleteTextView)DefineTransportRouteLayout.findViewById(R.id.transport_destination_field);
        transportDestination.setAdapter(MainActivity.placeAutocompleteAdapter);
        transportDestination.setOnItemClickListener(transportAutoCompleteListener);
        transportDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if(hasFocus == true)
                {
                    originFlag = false;
                    destFlag = true;
                    Log.d("FLAG", "DESTINATION");

                }
            }
        });


        transportDateText = (TextView)DefineTransportRouteLayout.findViewById(R.id.transport_date_text);
        transportDate = (ImageButton)DefineTransportRouteLayout.findViewById(R.id.transport_date_button);
        transportDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(transportDateText,getFragmentManager());
            }
        });
        transportNotes = (EditText)DefineTransportRouteLayout.findViewById(R.id.transport_description);
        notificationSpinner = (Spinner)DefineTransportRouteLayout.findViewById(R.id.transport_notification_spinner);
        ArrayList<String> notificationIntervals = new ArrayList<>();
        loadIntervals(notificationIntervals);
        ArrayAdapter notificationAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,notificationIntervals);
        notificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationSpinner.setAdapter(notificationAdapter);
        notificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                Log.d("SPINNER", ""+position);
                Log.d("INTERVAL_SELECTED", ""+parent.getItemAtPosition(position).toString());
                notificationIntervalString = ""+parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {



            }
        });

        notifcationToggleText = (TextView)DefineTransportRouteLayout.findViewById(R.id.notify_me_toggle_text);

        notificationToggle = (SwitchCompat)DefineTransportRouteLayout.findViewById(R.id.transport_notify_me_toggle);
        notificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    notifcationToggleText.setText("ON");
                    notificationSpinner.setVisibility(View.VISIBLE);
                    notificationFlag = true;
                }
                else
                {
                    notifcationToggleText.setText("OFF");
                    notificationFlag = false;
                    notificationSpinner.setVisibility(View.GONE);
                }
            }
        });

        transportTimeText = (TextView)DefineTransportRouteLayout.findViewById(R.id.transport_time_text);

        transportTimeButton = (ImageButton)DefineTransportRouteLayout.findViewById(R.id.transport_time_button);
        transportTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog transportTimePicker = new TimePickerDialog(getContext(),R.style.MyDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        Log.d("TIME", ""+hourOfDay+":"+minute );
                        String h = "";
                        String m = "";

                        if(hourOfDay<10)
                        {
                            h ="0"+hourOfDay;
                        }
                        else
                        {
                            h = ""+hourOfDay;
                        }

                        if(minute<10)
                        {
                            m = "0"+minute;
                        }
                        else
                        {
                            m = ""+minute;
                        }

                        transportTimeText.setText(h+":"+m);
                    }
                }, hour, minute,true);

                transportTimePicker.show();
            }
        });

        //INIT_CHOOSE layout views


        chipsContainer = (LinearLayout) ChooseTransportModeLayout.findViewById(R.id.chips_container);
        numberOfTransportAddedText = (TextView)ChooseTransportModeLayout.findViewById(R.id.number_of_transport_modes_added);
        if(transportModesObjectList.size()>0)
        {
            if(transportModesObjectList.size()>1)
            {
                numberOfTransportAddedText.setText("You have added "+transportModesObjectList.size()+" transports");
            }
            else
            {
                numberOfTransportAddedText.setText("You have added "+transportModesObjectList.size()+" transport");
            }

        }

        walkTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.walk_transport);
        walkTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDefineLayout("Walk",false);


            }
        });

        bicycleTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.bicycle_transport);
        bicycleTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Cycle",false);
            }
        });

        carTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.car_transport);
        carTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Drive",false);
            }
        });
        cabTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.cab_transport);
        cabTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Hired Car",false);
            }
        });
        busTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.bus_transport);
        busTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Bus",false);
            }
        });
        bikeTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.bike_transport);
        bikeTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Bike",false);
            }
        });
        camperTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.camper_transport);
        camperTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Camper Van", false);
            }
        });
        trainTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.train_transport);
        trainTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Train",false);
            }
        });
        tubeTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.underground_transport);
        tubeTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Underground",false);
            }
        });
        mertoTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.tram_transport);
        mertoTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Metro",false);
            }
        });

        cableTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.cable_transport);
        cableTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Cable Car",false);
            }
        });

        ferryTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.ferry_transport);
        ferryTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Ferry",false);
            }
        });

        seaPlaneTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.seaplane_transport);
        seaPlaneTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Sea Plane",false);
            }
        });

        planeTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.plane_transport);
        planeTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Aeroplane",false);
            }
        });

        chopperTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.chopper_transport);
        chopperTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Helicopter",false);
            }
        });

        balloonTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.balloon_transport);
        balloonTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Air Balloon",false);
            }
        });

        sailTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.sailboat_transport);
        sailTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Sail Boat",false);
            }
        });

        cruiseTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.cruise_transport);
        cruiseTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Cruise Ship",false);
            }
        });

        boatTransport = (ImageButton)ChooseTransportModeLayout.findViewById(R.id.boat_transport);
        boatTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefineLayout("Boat",false);
            }
        });



//Draw the added transport objects from transportModeList

        Log.d("CHIPS_CONTAINER", "Initial draw "+transportModesObjectList.size()+" chips");
        drawTransportChips(transportModesObjectList,getLayoutInflater(),getResources(),getActivity());
    }

    public static void showChooseModesLayout()
    {
        MainActivity.BACK_NAVIGATION = "SHOW_MORE_TRIP_OPTIONS";
        choose = true;
        addDetailButton.setText("ADD");
        titleText.setText("Getting There");
        DefineTransportRouteLayout.setVisibility(View.INVISIBLE);
        ChooseTransportModeLayout.setVisibility(View.VISIBLE);
        //re-draw chips from transportModeList;

    }

    public void AddFragment(Fragment fragment, int container) {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(!AddNewTripFragment.initialFrag)
        {
            if(AddNewTripFragment.moreFlag==false) //enter,exit
            {
                fragmentTransaction.setCustomAnimations(R.anim.card_flip_left_in,R.anim.fade_out);

            }
            else if(AddNewTripFragment.moreFlag==true)
            {
                fragmentTransaction.setCustomAnimations(R.anim.card_flip_right_in,R.anim.fade_out);
            }
        }


        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();

    }



    public static View createTransportChipView(String chipType,String chipId,  LayoutInflater layoutInflater, Resources resources, Activity activity ) {
        int marginLeft = 0;
        Log.d("MARGIN", "LIST size is "+chipViewsList.size());
        if(chipViewsList.size()>0)
        {
            marginLeft = 25;
            Log.d("MARGIN", "List is not empty");
        }
        View  transportChip = layoutInflater.inflate(R.layout.chip_layout,null);
        ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mp.setMarginStart(marginLeft);
        transportChip.setLayoutParams(mp);

        Log.d("CHIP_TYPE", chipType);
        ImageButton deleteChip = (ImageButton) transportChip.findViewById(R.id.delete_chip_button);
        ImageButton chipImage = (ImageButton) transportChip.findViewById(R.id.chip_image);
        String mDrawableName = chipType.toLowerCase().replace(" ", "") + "_transport";
        Log.d("Drawable", mDrawableName);
        int resID = resources.getIdentifier(mDrawableName, "drawable", activity.getApplicationContext().getPackageName());
        chipImage.setBackgroundResource(resID);
        transportChip.setId(Integer.parseInt(chipId));
        Log.d("CHIP_ID", "ID: "+transportChip.getId());
        return transportChip;
    }
    public void loadIntervals(ArrayList<String> notificationIntervals)
    {

        int mins=0;
        int hours=1;
        int days = 0;
        while(mins<60)
        {
            mins = mins+5;
            notificationIntervals.add(""+mins+" mins");
        }
        while(hours<6)
        {
            hours++;
            notificationIntervals.add(""+hours+" hours");

        }
        while(days<7)
        {
            days++;
            notificationIntervals.add(""+days+" days");
        }

    }


    public AdapterView.OnItemClickListener transportAutoCompleteListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = MainActivity.placeAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("AUTOCOMPLETE", "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(MainActivity.mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


        }
    };

    static ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("AUTOCOMPLETE", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();

                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.d("AUTOCOMPLETE", "Place details received: " + place.getName());
            Log.d("AUTOCOMPLETE", "Palce details received" + place.getLatLng());


            if(originFlag == true)
            {
                originLatLng = place.getLatLng();
                Log.d("ORIGIN", originLatLng.toString());

            }
            else
            {
                destinationLatLng = place.getLatLng();
                Log.d("DESTINATION", destinationLatLng.toString());

            }

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, LatLng latLng) {
        return null;

    }

    public static void resetDefineTransportFields()
    {
        transportDestination.setText("");
        transportOrigin.setText("");
        transportTimeText.setText("");
        transportNotes.setText("");
        notificationFlag=false;
        notificationToggle.setChecked(false);
        transportDateText.setText("");
    }

    private static void showTransportChipDetailDialog(String title, TransportMode transport) {
        if(fragmentManager!=null)
        {
            TransportDetailDialog transportDetailDialog= TransportDetailDialog.newInstance(title, transport);
            transportDetailDialog.show(fragmentManager, "fragment_edit_name");
        }
        else
        {
            Log.d("ACTIVITY", "NULL .. maza na7nu fa3loun izan?");
        }

    }
    @Override
    public void onResume() {
        super.onResume();
       fragmentManager = getActivity().getSupportFragmentManager();
    }

 public static void populateFields(TransportMode transportMode)
 {
     titleText.setText(transportMode.gettransport_type());
     transportOrigin.setText(transportMode.gettransport_origin());
     transportDestination.setText(transportMode.gettransport_destination());
     transportDateText.setText(transportMode.transport_date);
     transportNotes.setText(transportMode.gettransport_notes());
     transportTimeText.setText(transportMode.gettransport_time());

 }

 public static void drawTransportChips(final List<TransportMode> transportList, LayoutInflater inflater, Resources resources, Activity activity)
 {
     chipViewsList.clear(); // clear chips list so you dont draw duplicates ever time the draw function is called
     if(chipsContainer.getChildCount()>0) // if the container has children remove them so you dont add duplicates
     {
         chipsContainer.removeAllViewsInLayout();
     }
     for(TransportMode transportMode:transportList)
     {
        final View chipView = createTransportChipView(transportMode.gettransport_type(),transportMode.gettransport_chipId(),inflater,resources,activity);
        chipViewsList.add(chipView); //iterate over the objects list and create a view for each object then add it to the views list
     }

     for(final View chipView:chipViewsList) //iterate over views list to set up onClicklistener for delete and open dialog
     {
         chipView.findViewById(R.id.delete_chip_button).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 chipViewsList.remove(chipView); //remove view from container, view list and object in list
                 chipsContainer.removeView(chipView);
                 for(TransportMode transportMode:transportList)
                 {
                     if(Integer.parseInt(transportMode.gettransport_chipId())==chipView.getId())
                     {
                         transportList.remove(transportMode);
                     }
                 }
                 if(transportModesObjectList.size()>1)
                 {
                     numberOfTransportAddedText.setText("You have added "+transportModesObjectList.size()+" transports");//update text after deletion
                 }
                 else
                 {
                     numberOfTransportAddedText.setText("You have added "+transportModesObjectList.size()+" transport");
                 }
             }
         });

        chipView.findViewById(R.id.chip_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(TransportMode transportMode:transportList)
                {
                    if(Integer.parseInt(transportMode.gettransport_chipId())==chipView.getId())
                    {
                        showTransportChipDetailDialog(transportMode.gettransport_type(),transportMode);
                    }
                }
            }
        });
     }

     Log.d("CHIPS_LIST", "DRAW "+chipViewsList.size()+" chips");

     for(View chip:chipViewsList)
     {
         chipsContainer.addView(chip);
     }

 }



}
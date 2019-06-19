package mebeidcreations.apps.livegosee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import static mebeidcreations.apps.livegosee.Helper.Toasting;
import static mebeidcreations.apps.livegosee.Helper.showAlertDialog;
import static mebeidcreations.apps.livegosee.Helper.showDatePicker;


public class AddNewTripFragment extends Fragment {

    FirebaseDBHandler firebaseDBHandler;

    Button saveNewTripButton;
    static boolean moreFlag = false;
    static boolean initialFrag = true;
    FrameLayout fragmentsContainer;
    static  Fragment AddMoreTripOptions;
    static TripInfo newTripInfo;
    static String newTripId;
    static boolean saveButtonFlag = false;
    static AlertDialogAction newTripAlertDialogAction;
    static FragmentTransaction fragmentTransaction;
    static ImageButton fromDateButton, toDateButton;
    static EditText newTripNameField;
    static AutoCompleteTextView newTripOriginField, newTripDestinationField;
    static TextView newTripFromDateText, newTripToDateText, tripPrivacyText;
    static boolean fromDateFlag = false;
    static boolean toDateFlag = false;
    static boolean originFlag = false;
    static boolean destFlag = false;
    static  boolean itemSelected = false;
    static boolean knownOrigin = false;
    static boolean knownDestination = false;

    SwitchCompat privacyToggle;

    static String destLat, destLng, originLat, originLng;
    static LatLng originLatLng, destinationLatLng;


    static String tripFromDate, tripToDate;

    ///////////////////////////////On create Method shite
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_add_new_trip, container, false);
        ////////////////////////// /////////////////////////////

        tripPrivacyText = (TextView)rootView.findViewById(R.id.trip_privacy);

        privacyToggle = (SwitchCompat)rootView.findViewById(R.id.privacy_toggle);
        privacyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    tripPrivacyText.setText("Public");
                }
                else
                {
                    tripPrivacyText.setText("Private");
                }
            }
        });



        newTripNameField = (EditText)rootView.findViewById(R.id.new_trip_name_field);
        newTripDestinationField = (AutoCompleteTextView)rootView.findViewById(R.id.new_trip_destination_field);
        newTripDestinationField.setAdapter(MainActivity.placeAutocompleteAdapter);
        newTripDestinationField.setOnItemClickListener(newTripAutoCompleteListener);
        newTripDestinationField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                knownDestination = false;

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newTripDestinationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus)
                {
                    originFlag = false;
                    destFlag = true;
                    Log.d("FLAG", "DESTINATION");

                }
            }
        });


        newTripOriginField = (AutoCompleteTextView)rootView.findViewById(R.id.new_trip_origin_field);
        newTripOriginField.setAdapter(MainActivity.placeAutocompleteAdapter);
        newTripOriginField.setOnItemClickListener(newTripAutoCompleteListener);
        newTripOriginField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               knownOrigin = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newTripOriginField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus)
                {
                    destFlag = false;
                    originFlag = true;
                    Log.d("FLAG", "ORIGIN");
                }
            }
        });





        newTripFromDateText = (TextView)rootView.findViewById(R.id.new_trip_from_date_text);
        newTripToDateText = (TextView)rootView.findViewById(R.id.new_trip_to_date_text);

        fromDateButton = (ImageButton)rootView.findViewById(R.id.new_trip_from_date_button);
        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDateFlag = true;
                toDateFlag = false;
                showDatePicker(newTripFromDateText, getFragmentManager());
            }
        });

        toDateButton = (ImageButton)rootView.findViewById(R.id.new_trip__to_date_button);
        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fromDateFlag = false;
                toDateFlag = true;
                showDatePicker(newTripToDateText,getFragmentManager());

            }
        });



        AddTripTransportFragment.transportModesObjectList.clear();
        MainActivity.BACK_NAVIGATION = "SHOW_LOSE_TRIP_INFO_DIALOG";
        fragmentsContainer = (FrameLayout)rootView.findViewById(R.id.main_activity_fragments_container);
        AddMoreTripOptions = new AddTripOptionsFragment();
        initialFrag = false;



        newTripAlertDialogAction = new AlertDialogAction() {
            @Override
            public void positiveAction() {
                Log.d("ALERT_DIALOG", "POSITIVE");
                AddFragment(new HomeFeedFragment(),R.id.main_activity_fragments_container);
                destinationLatLng = null;
                originLatLng = null;
            }

            @Override
            public void negativeAction() {
                Log.d("ALERT_DIALOG", "NEGATIVE");
            }
        };




        saveNewTripButton = (Button)rootView.findViewById(R.id.new_trip_details_save_button);
        saveNewTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validateTrip();
            }
        });


        return  rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("TRIP_NAME", newTripNameField.getText().toString());

    }

    public void AddFragment(Fragment fragment, int container) {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(!initialFrag)
        {
            if(moreFlag==false) //enter,exit
            {
                fragmentTransaction.setCustomAnimations(R.anim.card_flip_left_in,R.anim.fade_out);

            }
            else if(moreFlag==true)
            {
                fragmentTransaction.setCustomAnimations(R.anim.card_flip_right_in,R.anim.fade_out);
            }
        }
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();

    }

    public AdapterView.OnItemClickListener newTripAutoCompleteListener
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
            Log.d("FLAG", originFlag+","+destFlag);
           if(destFlag)
           {
               knownDestination = true;
           }


           if(originFlag)
           {
               knownOrigin = true;
           }

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


            if(originFlag)
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




    public static void unknownLocationAlertDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);


        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {




            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



            }
        });
        builder.show();



    }
public boolean validateTrip()
{
    saveButtonFlag=true;
    boolean valid = true;


    LinkedList<TextView> views = new LinkedList<TextView>();
    views.add(newTripDestinationField);
    views.add(newTripNameField);
    views.add(newTripOriginField);
    views.add(newTripFromDateText);
    views.add(newTripToDateText);

    if(Helper.validateTextViews(views))
    {
        newTripInfo = new TripInfo();
        newTripInfo.trip_name = newTripNameField.getText().toString();
        newTripInfo.trip_origin = newTripOriginField.getText().toString();
        newTripInfo.trip_destination = newTripDestinationField.getText().toString();
        tripFromDate = newTripFromDateText.getText().toString();
        tripToDate = newTripToDateText.getText().toString();
        newTripInfo.trip_from_date = tripFromDate;
        newTripInfo.trip_to_date = tripToDate;
        newTripInfo.tripuser_id = MainActivity.currentUserId;
        newTripInfo.trip_privacy = tripPrivacyText.getText().toString().toLowerCase();
        Log.d("PRIVACY", newTripInfo.gettrip_privacy());
        newTripInfo.trip_thumbnail_stamp = "NO_PHOTO";
        Calendar cal1 = Calendar.getInstance();
        String stamp = cal1.get(Calendar.DAY_OF_MONTH)+""+cal1.get(Calendar.MONTH)+""+cal1.get(Calendar.YEAR)+
                ""+cal1.get(Calendar.HOUR)+""+cal1.get(Calendar.MINUTE)+""+cal1.get(Calendar.SECOND);

        newTripId = newTripInfo.gettripuser_id()+stamp;
        newTripInfo.trip_id = newTripId;

        newTripInfo.trip_search_string = newTripInfo.gettrip_name().toLowerCase()+newTripInfo.gettrip_privacy().toLowerCase()+
                newTripInfo.gettrip_from_date()+newTripInfo.gettrip_to_date()+
                newTripInfo.getCityCountry().toLowerCase();

        if(Helper.validDates(newTripFromDateText.getText().toString(), newTripToDateText.getText().toString()))
        {
            //TO do .. the if statement is working fine .. if the user selects an address from the
            //autocomplete it doesnt change if he deletes it or nulls it only changes on an item select
            //maybe add an on textchange listener if it changes the lat and lang are reset or nulled until the user selects an item from the autocomplete


            Log.d("AUTOCOMPLETE", knownOrigin+" , "+knownDestination);

     //  if ((originLatLng!=null)&&destinationLatLng!=null) //check that locations are valid and display warning dialog to let the user add it manually
          if(knownDestination&&knownOrigin)
           {
               //null origin is working fine
                Log.d("MAP", "Known locations");
                Log.d("MAP", "ORIGIN: "+originLatLng);
                Log.d("MAP","DESTINATION: "+destinationLatLng);
                originLat = ""+originLatLng.latitude;
                originLng = ""+originLatLng.longitude;
                newTripInfo.origin_lng = originLng;
                newTripInfo.origin_lat = originLat;

                destLat = ""+ destinationLatLng.latitude;
                destLng = ""+ destinationLatLng.longitude;
                newTripInfo.destination_lat = destLat;
                newTripInfo.destination_lng = destLng;
            // if not null the trip should be created else go to else
            // condition to check if the user has entered a location that cant be found on Google Places API

               MainActivity.BACK_NAVIGATION = "SHOW_CHOOSE_MORE_OPTIONS_LATER";
               AddFragment(new AddTripOptionsFragment(),R.id.main_activity_fragments_container);
               firebaseDBHandler.addTripInfoToDB(newTripInfo,MainActivity.currentUserId);
               saveNewTripButton.setVisibility(View.INVISIBLE);
               Log.d("SAVE_BUTTON", ""+saveButtonFlag);
               knownOrigin = false;
               knownDestination = false;

            }
            else
            {
                //if the user has entered false locations he will be prompted by a dialog to enter the location manually on the map using a drop pin, this is not implemented yer
                Log.d("MAP","Unknown locations");
                showAlertDialog("Location", "One of your locations can't be found, would you like to add it manually?",
                        "YES", "LATER", getActivity(), new AlertDialogAction() {
                            @Override
                            public void positiveAction() {

                                    //Show map fragment with only one pin in the middle that the user can drag and drop
                                    //start with the user location
                              }

                            @Override
                            public void negativeAction() {
                                showAlertDialog("", "Would you like to add more trip details?",
                                        "YES", "LATER", getActivity(), new AlertDialogAction() {
                                            @Override
                                            public void positiveAction() {
                                                MainActivity.BACK_NAVIGATION = "SHOW_CHOOSE_MORE_OPTIONS_LATER";
                                                AddFragment(new AddTripOptionsFragment(),R.id.main_activity_fragments_container);
                                                firebaseDBHandler.addTripInfoToDB(newTripInfo,MainActivity.currentUserId);
                                                saveNewTripButton.setVisibility(View.INVISIBLE);
                                                Log.d("SAVE_BUTTON", ""+saveButtonFlag);
                                            }

                                            @Override
                                            public void negativeAction() {
                                                firebaseDBHandler.addTripInfoToDB(newTripInfo,MainActivity.currentUserId);
                                                AddFragment(new MyTripsListFragment(),R.id.main_activity_fragments_container);
                                            }
                                        });
                                if(destinationLatLng==null)
                                {
                                    newTripInfo.destination_lng = "";
                                    newTripInfo.destination_lat = "";
                                }
                                else
                                {
                                    newTripInfo.origin_lng = "";
                                    newTripInfo.origin_lat = "";
                                }
                            }
                        });
                knownOrigin = false;
                knownDestination = false;

            }

            if(!MainActivity.tripInfoQ.isEmpty())
            {
                MainActivity.tripInfoQ.clear();
            }


        }

    }
    else
    {
        Log.d("TRIP","Failed to add trip");
        valid = false;
    }



    return valid;
}



}
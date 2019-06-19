package mebeidcreations.apps.livegosee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.media.RatingCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static mebeidcreations.apps.livegosee.Helper.Toasting;

/**
 * Created by Mazen Ebeid on 21/09/2017.
 */

public class AddHotelDialogFragment extends DialogFragment  {

    FirebaseDBHandler firebaseDBHandler;
    ImageButton checkInDateButton, checkOutDateButton;
    EditText hotelReviewField;
    AutoCompleteTextView hotelAddressAutoComplete;
    static TextView checkInDateText, checkDateOutText, hotelAddressText;
    static boolean checkInDateFlag = false;
    static boolean checkOutDateFlag = false;
    static PlaceAutocompleteAdapter  placeAutocompleteAdapter;
    static String destLat, destLng, originLat, originLng;
    Button saveHotelButton;
    private static final int PERMISSION_REQUEST_CODE = 100;
    Calendar cal;
    AppCompatRatingBar hotelRatingBar;

    static boolean past = false;
    static String hotel_lat, hotel_lng;
    static String hotel_rating;


    public AddHotelDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddHotelDialogFragment newInstance(String title) {
        AddHotelDialogFragment frag = new AddHotelDialogFragment();
        frag.setStyle(STYLE_NORMAL,R.style.NewTripDialogStyle);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.add_hotel_dialog, container);

    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);




        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity().getApplicationContext(),MyTripLogActivity.mGoogleApiClient,MainActivity.BOUNDS_GREATER_SYDNEY,null);


        hotelRatingBar = (AppCompatRatingBar)view.findViewById(R.id.hotel_rating_bar);
        hotelRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                hotel_rating = ""+rating;
                Toasting("Rating: "+hotel_rating);
            }
        });


        hotelReviewField = (EditText)view.findViewById(R.id.hotel_review_field);


        hotelAddressAutoComplete = (AutoCompleteTextView)view.findViewById(R.id.hotel_name_field);
        hotelAddressAutoComplete.setAdapter(placeAutocompleteAdapter);
        hotelAddressAutoComplete.setOnItemClickListener(addHotelAutoCompleteListener);

        hotelAddressText = (TextView)view.findViewById(R.id.hotel_address_text);




        checkInDateText = (TextView)view.findViewById(R.id.check_in_date_text);
        checkDateOutText = (TextView)view.findViewById(R.id.check_out_date_text);

        checkInDateButton = (ImageButton)view.findViewById(R.id.hotel_check_in_button);
        checkInDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInDateFlag = true;
                checkOutDateFlag = false;
                showDatePicker();
            }
        });

        checkOutDateButton = (ImageButton)view.findViewById(R.id.hotel_check_out_button);
        checkOutDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkInDateFlag = false;
                checkOutDateFlag = true;
                showDatePicker();

            }
        });

        saveHotelButton = (Button)view.findViewById(R.id.save_hotel_button);
        saveHotelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                List<TextView> views = new ArrayList<TextView>();
                views.add(hotelAddressAutoComplete);
                views.add(checkInDateText);
                views.add(checkDateOutText);


                if(Helper.validateTextViews(views))
                {
                    if(Helper.validDates(checkInDateText.getText().toString(),checkDateOutText.getText().toString()))
                    {
                        HotelInfo hotelInfo = new HotelInfo();
                        hotelInfo.entry_type = "hotel";
                        hotelInfo.hotel_name = hotelAddressAutoComplete.getText().toString();
                        hotelInfo.hotel_checkin = checkInDateText.getText().toString();
                        hotelInfo.hotel_checkout = checkDateOutText.getText().toString();
                        hotelInfo.hotel_lat = hotel_lat;
                        hotelInfo.hotel_lng = hotel_lng;
                        hotelInfo.hotel_address = hotelAddressText.getText().toString();
                        hotelInfo.hotel_review = hotelReviewField.getText().toString();
                        hotelInfo.hotel_rating = hotel_rating;
                        Calendar cal1 = Calendar.getInstance();
                        String stamp = cal1.get(Calendar.DAY_OF_MONTH)+""+cal1.get(Calendar.MONTH)+""+cal1.get(Calendar.YEAR)+
                                ""+cal1.get(Calendar.HOUR)+""+cal1.get(Calendar.MINUTE)+""+cal1.get(Calendar.SECOND);

                        hotelInfo.hotel_id = MainActivity.currentUserId+MyTripLogActivity.selectedTrip.getCityCountry()+hotelInfo.gethotel_name()+stamp;

                     //   firebaseDBHandler.addHotelToDB(MyTripLogActivity.selectedTrip.gettrip_id(),hotelInfo);
                        firebaseDBHandler.addTripDetailObject(MyTripLogActivity.selectedTrip.gettrip_id(),hotelInfo);
                         Toasting("Adding Hotel");
                        dismiss();
                    }
                }
                else
                {
                    hotelAddressAutoComplete.setError("Required");
                    checkDateOutText.setText("Check out date can't be empty");
                    checkInDateText.setText("Check in date can't be empty");


                }





            }
        });





    }

    public void showDatePicker()
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "Age");

    }





    public AdapterView.OnItemClickListener addHotelAutoCompleteListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("AUTOCOMPLETE", "Autocomplete item selected: " + primaryText);
            hotelAddressAutoComplete.setText(primaryText);
            item.getPlaceId();



            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(MyTripLogActivity.mGoogleApiClient, placeId);

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

            // Format details of the place for display and show it in a TextView.
            //     mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
            //              place.getId(), place.getAddress(), place.getPhoneNumber()));


            // Display the third party attributions if set.
            //       final CharSequence thirdPartyAttribution = places.getAttributions();
            //        if (thirdPartyAttribution == null) {
            //            mPlaceDetailsAttribution.setVisibility(View.GONE);
            //       } else {
            //           mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
            //            mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            //        }

            Log.d("AUTOCOMPLETE", "Place details received: " + place.getName());
            Log.d("AUTOCOMPLETE", "Palce details received" + place.getLatLng());


                LatLng hotelLatLng = place.getLatLng();
                 hotel_lat = ""+hotelLatLng.latitude;
                 hotel_lng = ""+hotelLatLng.longitude;
                Log.d("HOTEL", hotelLatLng.toString());
                hotelAddressText.setText(place.getAddress());


            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, LatLng latLng) {


        return null;

    }



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

}
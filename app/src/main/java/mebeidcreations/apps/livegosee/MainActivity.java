package mebeidcreations.apps.livegosee;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.LinkedList;
import java.util.List;

import static mebeidcreations.apps.livegosee.AddNewTripFragment.newTripAlertDialogAction;
import static mebeidcreations.apps.livegosee.AddTripOptionsFragment.moreOptionsDialogAction;
import static mebeidcreations.apps.livegosee.AddTripTransportFragment.transportChooseAlertAction;
import static mebeidcreations.apps.livegosee.AddTripTransportFragment.transportDefineAlertAction;
import static mebeidcreations.apps.livegosee.Helper.AddFragment;
import static mebeidcreations.apps.livegosee.Helper.showAlertDialog;


public class MainActivity extends AppCompatActivity {

    Helper help;
    static String user_first_name="";

    static ImageButton UserButton;
    DrawerLayout mDrawerLayout;
    int backPress=0;
    static int mapStyle;
    static int togglePressed = 0;
    ListView mDrawerList;
    static UserInfo currentUserInfo;
    static ProgressDialog progressDialog;
    static LinearLayout bottomToolbar;
    static TextView userName, titleText;
    static LinkedList<TripInfo> selectedTripsList;
    static boolean unknownPlace = false;
    static GoogleApiClient mGoogleApiClient;
    int count = 0;
    static int  displayHeight = 0;
    static List<TripInfo> tripInfoQ;
    static int displayWidth = 0;
    static float originalY;
    static FragmentTransaction Ft;
    static Vibrator vibrator;
    static String BACK_NAVIGATION = "";
    ImageButton addNewTripButton;
    static PlaceAutocompleteAdapter placeAutocompleteAdapter;
    static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    static Intent goToLoginActivity;
    static String currentUserId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);




        userName = (TextView)findViewById(R.id.user_name_text);

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        AddFragment(new HomeFeedFragment(), R.id.main_activity_fragments_container,getSupportFragmentManager());


        help = new Helper(getApplicationContext());

        Bundle bundle = getIntent().getExtras();

        if(bundle!= null)
        {
            currentUserId = bundle.getString("user_id");
        }

        currentUserInfo = new UserInfo();


        FirebaseDBHandler firebaseDBHandler = new FirebaseDBHandler(getApplicationContext());
        firebaseDBHandler.findUserById(currentUserId);



           addNewTripButton = (ImageButton)findViewById(R.id.adD_new_trip);
           if(addNewTripButton!=null)
           {

            addNewTripButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AddFragment(new AddNewTripFragment(),R.id.main_activity_fragments_container,getSupportFragmentManager());
                }
            });
            }


      //  bottomToolbar = (LinearLayout) findViewById(R.id.bottom_toolbar);
   //     originalY = bottomToolbar.getY();

        progressDialog = new ProgressDialog(this, R.style.MyDialogTheme);
        progressDialog.setMessage("Loading Trips ....");
        progressDialog.setInverseBackgroundForced(true);

        tripInfoQ = new LinkedList<>();
        selectedTripsList = new LinkedList<>();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;


        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).
                enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // connection failed, should be handled

                        Log.d("GOOGLE", "CONNECTION FAILED");
                    }
                }).addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API).build();


        mGoogleApiClient.connect();
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getApplicationContext(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);



        goToLoginActivity = new Intent(this,LoginActivity.class);



        DrawerListItem drawerListItems[] = new DrawerListItem[]
                {
                        new DrawerListItem(R.drawable.icon_username, "Profile"),
                        new DrawerListItem(R.drawable.trip_settings_button, "Settings"),
                        new DrawerListItem(R.drawable.icon_username,"About"),
                        new DrawerListItem(R.drawable.icon_username,"Help/Tutorial"),
                        new DrawerListItem(R.drawable.back_arrow, "Logout")

                };

        DrawerListItemAdapter drawerListAdapter = new DrawerListItemAdapter(this, R.layout.drawer_list_item, drawerListItems);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_drawer);
        mDrawerList.setAdapter(drawerListAdapter);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        UserButton = (ImageButton)findViewById(R.id.user_button);
        UserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.END);
                count++;
            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if(i==0)
            {
            //Profile
            }

              if(i==1)
              {
                  //Settings
              }
                if(i==4) ///Logout
                {

                    LoginActivity.signOut();
                    startActivity(goToLoginActivity);
                    finish();
                    moveTaskToBack(true);

                }

            }
        });

        mapStyle = R.raw.dark_map_style_json;


    }




    @Override
    public void onBackPressed() {

        Log.d("BACK_NAVIGATION", ""+ BACK_NAVIGATION);
        if(BACK_NAVIGATION.equals("HOME_FEED"))
        {
            AddFragment(new HomeFeedFragment(),R.id.main_activity_fragments_container,getSupportFragmentManager());
            UserTripsListAdapter.longPress=false;
            UserTripsListAdapter.longPress=false;
            tripInfoQ.clear();
        }


        else if(BACK_NAVIGATION.equals("MY_TRIPS_LIST")) //go back to trips list
        {
            AddFragment(new MyTripsListFragment(),R.id.main_activity_fragments_container,getSupportFragmentManager());
        }

        else if(BACK_NAVIGATION.equals("EXIT_APP"))// if the back button is pressed twice when the user in the main home home feed
        {
            if(backPress==0)
            {
                help.Toasting("Press back one more time to close App");
                backPress++;
            }

            else if(backPress>0)
            {

                ExitActivity.exitApplication(getApplicationContext());

            }


        }
        else if(BACK_NAVIGATION.equals("SHOW_LOSE_TRIP_INFO_DIALOG")) //if you are adding a new trip and pressed the back button, a dialog will warn the user tha his data will be lost
        {
            showAlertDialog("", "Discard trip info?",
                    "Yes", "No", MainActivity.this, newTripAlertDialogAction);
        }
        else if(BACK_NAVIGATION.equals("SHOW_LOSE_TRANSPORT_DIALOG")) //if you are adding a new trip and pressed the back button, a dialog will warn the user tha his data will be lost
        {
            showAlertDialog("", "Discard transport info?",
                    "Yes", "No", MainActivity.this, transportDefineAlertAction);
        }
        else if(BACK_NAVIGATION.equals("SHOW_CHOOSE_MORE_OPTIONS_LATER"))
        {
            showAlertDialog("", "Would like to choose more options?",
                    "Yes", "Later", MainActivity.this, moreOptionsDialogAction);
        }
        else if(BACK_NAVIGATION.equals("SHOW_MORE_TRIP_OPTIONS"))
        {
            AddFragment(new AddTripOptionsFragment(), R.id.main_activity_fragments_container, getSupportFragmentManager());
        }
    }




    public static AdapterView.OnItemClickListener mAutocompleteClickListener
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

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            if (primaryText.toString().isEmpty()) {
                unknownPlace = true;
            }

            //toast
        }
    };

    static ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.d("AUTOCOMPLETE", "Place query did not complete. Error: " + places.getStatus().toString());
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

            Log.i("AUTOCOMPLETE", "Place details received: " + place.getName());
            Log.d("AUTOCOMPLETE", "Palce details received" + place.getLatLng());

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, LatLng latLng) {


        return null;

    }



    public static void translateToolbarUp() {

  //      Log.d("TRANSLATION", "UP");
   //     bottomToolbar.animate().setDuration(600);
  //      bottomToolbar.animate().y(displayHeight*0.94f);
        //0.01 is approx equivalent to 10 dp

    }


    public static void translateToolbarDown() {


  //      bottomToolbar.animate().y(displayHeight*1.02f);
   //     Log.d("TRANSLATION", "ORIGINAL "+displayHeight);
 //       Log.d("TRANSLATION", "Y"+displayHeight*1.02f);
 //       Log.d("TRANSLATION", "DOWN");

        }










}


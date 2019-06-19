package mebeidcreations.apps.livegosee;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import static mebeidcreations.apps.livegosee.MainActivity.displayHeight;


public class MyTripLogActivity extends AppCompatActivity implements MediaScannerConnection.MediaScannerConnectionClient{

    TextView selectedTripName, selectedTripFromDate, selectedTripOrigin, selectedTripToDate,
            selectedTripDestination, selectedTripStartCount, selectedTripDurationCount, selectedTripStat,
            selectedTripPrivacyText;


    static LinearLayout hotelLLParent, hotelCardLL, entriesLL;

    Intent goToCameraActivity;

    ImageView selectedTripImage;

    static List<HotelInfo> hotelInfoQ;

    static LayoutInflater layoutInflater;

    FirebaseDBHandler firebaseDBHandler;

    static  TripInfo selectedTrip;

    ImageButton selectedTripEditButton, selectedTripDeleteButton, selectedTripAddImage, selectedTripdAddPeopleButton
            ,selectedTripAddEventButton, selectedTripAddHotelButton,
            selectedTripMapButton;



    SwitchCompat selectedTripPrivacyToggle;

    String tripStartCount, tripDurationCount, tripEndCount, tripStat;

    File LGSdirectory;

    MediaScannerConnection conn;

    final int REQUEST_IMAGE_CAPTURE = 1;

    Bitmap mImageBitmap;

    String mCurrentPhotoPath;

    boolean addEventFlag = false;

    DrawerLayout sideDrawer;
    ListView sideDrawerList;
    static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_log);

        layoutInflater = getLayoutInflater();
        initializeViews();
        MainActivity.BACK_NAVIGATION="MY_TRIPS_LIST";
     //  AddFragment(new MapFragment(),R.id.trip_log_relative_layout);
        firebaseDBHandler = new FirebaseDBHandler(getApplicationContext());
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




        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("MyApp", "No SDCARD");
        } else {
            LGSdirectory = new File(Environment.getExternalStorageDirectory()+File.separator+"LIVE GO SEE");
            LGSdirectory.mkdirs();
        }


        goToCameraActivity = new Intent(this, CameraActivity.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle.get("selected_trip") != null) {

            Log.d("BUNDLE", "NOT NULL");
            selectedTrip  = (TripInfo) bundle.getSerializable("selected_trip");
            Log.d("TRIP_INFO_OBJECT", "TRIP_ID: " + selectedTrip.gettrip_id());
            setViewsValues(selectedTrip);
            hotelInfoQ = new LinkedList<>();
        //    firebaseDBHandler.GetTripHotelsList(selectedTrip.gettrip_id());


        } else {
            Log.d("BUNDLE", "NULL");
        }


    }

    public void initializeViews() {



        hotelLLParent = (LinearLayout)findViewById(R.id.hotel_LL);
        hotelCardLL = (LinearLayout)findViewById(R.id.hotel_card);
        entriesLL = (LinearLayout)findViewById(R.id.log_entries_layout);

        DrawerListItem sideDrawerListItems[] = new DrawerListItem[]
                {

                        new DrawerListItem(R.drawable.add_place_icon,"Place"),
                        new DrawerListItem(R.drawable.day_activity_icon,"Day"),
                        new DrawerListItem(R.drawable.night_activity_icon,"Night"),
                        new DrawerListItem(R.drawable.add_info_icon,"Info"),
                        new DrawerListItem(R.drawable.add_restaurant_icon,"Food"),
                        new DrawerListItem(R.drawable.photo_icon, "Photo"),
                        new DrawerListItem(R.drawable.video_icon, "Video"),
                        new DrawerListItem(R.drawable.music_icon, "Music"),
                        new DrawerListItem(R.drawable.add_people_icon,"People"),



                };

        DrawerListItemAdapter sideDrawerListAdapter = new DrawerListItemAdapter(this, R.layout.side_drawer_list_item, sideDrawerListItems);

        sideDrawer = (DrawerLayout)findViewById(R.id.side_drawer);
        sideDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        sideDrawerList = (ListView)findViewById(R.id.side_drawer_list);
        sideDrawerList.setAdapter(sideDrawerListAdapter);

        sideDrawer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(sideDrawer.isDrawerOpen(GravityCompat.END))
                {
                    translateButtonsDown();
                    addEventFlag = false;
                }

                return false;
            }
        });

        sideDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0)
                {

                }
                if(position==1)
                {

                }
                if(position==2)
                {

                }
                if(position==3)
                {

                }
                if(position==4)
                {

                }
                if(position==5)
                {
                    Log.d("ITEM", "CAMERA");
                    takePhoto();
                    startScan();
                    resetDrawer();


                }
                if(position==6)
                {
                    takeVideo();
                    startScan();
                    sideDrawer.closeDrawer(GravityCompat.END);
                    translateButtonsDown();
                    addEventFlag=false;
                }
                if(position==7)
                {

                }

            }
        });


        selectedTripName = (TextView) findViewById(R.id.selected_trip_name);
        selectedTripDestination = (TextView) findViewById(R.id.selected_trip_destination);
        selectedTripDurationCount = (TextView) findViewById(R.id.selected_trip_duration_count);
        selectedTripFromDate = (TextView) findViewById(R.id.selected_trip_from_date);
        selectedTripToDate = (TextView) findViewById(R.id.selected_trip_to_date);
        selectedTripStartCount = (TextView) findViewById(R.id.selected_trip_start_count);
        selectedTripOrigin = (TextView) findViewById(R.id.selected_trip_origin);
        selectedTripStat = (TextView) findViewById(R.id.selected_trip_start_end_stat);
        selectedTripPrivacyText = (TextView) findViewById(R.id.selected_trip_privacy_toggle_text);

        selectedTripImage = (ImageView)findViewById(R.id.selected_trip_image);

        selectedTripDeleteButton = (ImageButton) findViewById(R.id.delete_selected_trip);
        selectedTripDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete selected trip
            }
        });
        selectedTripEditButton = (ImageButton) findViewById(R.id.edit_selected_trip_info);
        selectedTripEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Edit selected trip
            }
        });
        selectedTripAddHotelButton = (ImageButton) findViewById(R.id.selected_trip_add_hotel_button);
        selectedTripAddHotelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add Hotel

                showAddHotelFragment();
            }
        });


        selectedTripMapButton = (ImageButton) findViewById(R.id.selected_trip_map_button);
        selectedTripMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add tags
            }
        });

        selectedTripPrivacyToggle = (SwitchCompat) findViewById(R.id.selected_trip_privacy_toggle);
        selectedTripPrivacyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ////////////////////////////should update trip info in DB accordingly
                if (isChecked) {
                    selectedTripPrivacyText.setText("Private");
                } else {
                    selectedTripPrivacyText.setText("Public");
                }

            }
        });

        selectedTripAddImage = (ImageButton) findViewById(R.id.add_selected_trip_image_button);
        selectedTripAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               takePhoto();
                startScan();


            }
        });



        selectedTripAddEventButton = (ImageButton)findViewById(R.id.add_event_button);
        selectedTripAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(addEventFlag==false)
                {
                    translateButtonsUp();
                    sideDrawer.openDrawer(GravityCompat.END);
                    addEventFlag = true;
                }
                else
                {
                    translateButtonsDown();
                    sideDrawer.closeDrawer(GravityCompat.END);
                    addEventFlag=false;
                }

            }
        });



    }


    public void getTripCounters(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy");
        try {
            Date sdate = dateFormat.parse(startDate);
            Date edate = dateFormat.parse(endDate);
            Date today = Calendar.getInstance().getTime();

            if (DateUtils.isAfterDay(sdate, today)) //date 1 is after date 2 - Start in the future
            {
                Log.d("DATE", "Future start");
                tripStat = "Starts";


            } else {
                Log.d("DATE", "TRIP STARTED"); // trips has already started
                tripStat = "Started";
            }
            if (DateUtils.isAfterDay(today, edate)) // trip ended
            {
                Log.d("DATE", "ENDED");
                tripStat = "Ended";
            }
            long durationCount = edate.getTime() - sdate.getTime();
            long startCount = today.getTime() - sdate.getTime();
            long endCount = today.getTime() - edate.getTime();
            if (durationCount == 0) {
                tripDurationCount = "1 Day";
            } else {
                int days = (int) (durationCount / (1000 * 60 * 60 * 24));

                tripDurationCount = "" + days + " Days";
            }

            Log.d("DURATION", tripDurationCount);

            if (startCount == 0) {
                Log.d("STARTS", "TODAY");
                tripStartCount = "Today";
            } else {
                int days = (int) (startCount / (1000 * 60 * 60 * 24));
                tripStartCount = "" + days + " Days";
                Log.d("STARTS", tripStartCount);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void setViewsValues(TripInfo selectedTrip) {
        selectedTripDestination.setText(selectedTrip.gettrip_destination());
        selectedTripFromDate.setText(selectedTrip.gettrip_from_date());
        selectedTripToDate.setText(selectedTrip.gettrip_to_date());
        selectedTripName.setText(selectedTrip.gettrip_name());
        selectedTripOrigin.setText(selectedTrip.gettrip_origin());
        getTripCounters(selectedTrip.gettrip_from_date(), selectedTrip.gettrip_to_date());
        selectedTripDurationCount.setText(tripDurationCount);
        selectedTripStat.setText(tripStat);
        selectedTripStartCount.setText(tripStartCount);
        if (selectedTrip.gettrip_privacy().equals("private")) {
            selectedTripPrivacyText.setText("Private");
            selectedTripPrivacyToggle.setChecked(true);
        } else {
            selectedTripPrivacyText.setText("Public");
            selectedTripPrivacyToggle.setChecked(false);
        }


    }

 public void takePhoto()
 {
     Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
     if (cameraIntent.resolveActivity(getPackageManager()) != null) {
         // Create the File where the photo should go
         File photoFile = null;
         try {
             photoFile = createImageFile();
         } catch (IOException ex) {
             // Error occurred while creating the File
             Log.i("CAMERA", ex.getMessage());
         }
         // Continue only if the File was successfully created
         if (photoFile != null) {
             cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
             startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

         }
     }
 }

    public void takeVideo()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("CAMERA", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
       ;
  //      File storageDir = Environment.getExternalStoragePublicDirectory(
    //            Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                LGSdirectory
                // storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    } //change image file to have location or data

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                selectedTripImage.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startScan()
    {
        if(conn!=null) conn.disconnect();
        conn = new MediaScannerConnection(MyTripLogActivity.this,MyTripLogActivity.this);
        conn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        try{
            conn.scanFile(LGSdirectory.getPath(), ".jpg");
            Log.d("FILE_PATH", LGSdirectory.getPath());
        } catch (IllegalStateException e)
        {
            Log.d("FILE_EXCEPTION", e.getMessage());
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        conn.disconnect();
    }


    public   void AddFragment(Fragment fragment, int container) {

        FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commitNow();

    }

    public void translateButtonsUp() {


        Log.d("TRANSLATpION", "UP");
        Log.d("TRANSLATION", "DISPLAY_HEIGHT: "+displayHeight);

        selectedTripAddEventButton.animate().rotation(180);

    }
    public void translateButtonsDown() {

        selectedTripAddEventButton.animate().rotation(360);
        Log.d("TRANSLATION", "DOWN");

    }

    public void resetDrawer()
    {
        sideDrawer.closeDrawer(GravityCompat.END);
        translateButtonsDown();
        addEventFlag=false;
    }



    public void showAddHotelFragment() {

        AddHotelDialogFragment addHotelDialogFragment= AddHotelDialogFragment.newInstance("HOTEL_DIALOG_FRAGMENT");
        addHotelDialogFragment.show(getSupportFragmentManager(), "TAG");


    }


    public static void setHotelViews(List<HotelInfo> hotelInfoList)
    {
        Log.d("SETTING_VIEWS", "HOTELS "+hotelInfoQ.size());
        for(int i=0; i<hotelInfoList.size(); i++)
        {

            View hotelView = layoutInflater.inflate(R.layout.hotel_list_item,entriesLL,false);

            TextView selectedTripHotleName = (TextView)hotelView.findViewById(R.id.hotel_name_text);
            TextView selectedTripHotleAddress = (TextView)hotelView.findViewById(R.id.hotel_address_text);
            TextView selectedTripHotleCheckin = (TextView)hotelView.findViewById(R.id.hotel_checkin_text);
            TextView selectedTripHotleCheckout = (TextView)hotelView.findViewById(R.id.hotel_checkout_text);
            TextView selectedTripHotleReview = (TextView)hotelView.findViewById(R.id.hotel_review_text);
            AppCompatRatingBar selectedTripHotelRating = (AppCompatRatingBar)hotelView.findViewById(R.id.hotel_rating_bar);


            selectedTripHotleName.setText(hotelInfoList.get(i).gethotel_name());
            selectedTripHotleAddress.setText(hotelInfoList.get(i).gethotel_address());
            selectedTripHotleCheckin.setText(hotelInfoList.get(i).gethotel_checkin());
            selectedTripHotleCheckout.setText(hotelInfoList.get(i).gethotel_checkout());
            if(!hotelInfoList.get(i).gethotel_review().isEmpty())
            {
                selectedTripHotleReview.setText(hotelInfoList.get(i).gethotel_review());
                selectedTripHotelRating.setVisibility(View.VISIBLE);
            }
            entriesLL.addView(hotelView);


        }
    }

}

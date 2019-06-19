package mebeidcreations.apps.livegosee;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;
import java.util.LinkedList;

import static mebeidcreations.apps.livegosee.Helper.AddFragment;

/**
 * Created by Admin on 11-12-2015.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    static Double markerLat, markerLng;
    static GoogleApiClient mGoogleApiClient;
    static LatLng latLng;
    LinkedList<String> pins;
    private SupportMapFragment mSupportMapFragment;
    static GoogleMap mMap;
    FirebaseDBHandler firebaseDBHandler;
    ProgressDialog progressDialog;
    ImageButton viewTripsList, toggleMapStyle;

    static int mapStyle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_my_trips_map, container, false);
        ///////////////////////////////
        MainActivity.BACK_NAVIGATION ="MY_TRIPS_LIST";
       progressDialog = new ProgressDialog(getActivity(), R.style.MyDialogTheme);
        progressDialog.setMessage("Loading Map ....");
        progressDialog.setInverseBackgroundForced(true);
        progressDialog.show();

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.my_trips_map);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.my_trips_map_container, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(this);

        }

        viewTripsList = (ImageButton)rootView.findViewById(R.id.show_trips_list_view);
        viewTripsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFragment(new MyTripsListFragment(),R.id.main_activity_fragments_container,getFragmentManager());
            }
        });

        toggleMapStyle = (ImageButton)rootView.findViewById(R.id.map_style_toggle);
        toggleMapStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            MainActivity.togglePressed++;
            AddFragment(new MapFragment(),R.id.main_activity_fragments_container,getFragmentManager());

            }
        });
        return rootView;
    }

    public void Toasting(String msg) {

        Toast toast = Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.YELLOW);
        toast.show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP_TOGGLE", ""+MainActivity.togglePressed);
        if(MainActivity.togglePressed == 0)
        {
            mapStyle = R.raw.dark_map_style_json;
        }

        else if(MainActivity.togglePressed == 1)
        {
            mapStyle = R.raw.retro_map_style_json;
        }
        else  if(MainActivity.togglePressed == 2)
        {
            mapStyle = R.raw.silver_map_style_json;
            MainActivity.togglePressed=-1;

        }
        progressDialog.dismiss();
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                getActivity().getApplicationContext(),mapStyle ));
        PopulateUserTripsMap();





    }

    public static void AddPinTOMap(LatLng latLng, CharSequence title) {


            if (mMap != null) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("" + title).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.vector_pin)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, -10));

                Log.d("LOCATION", latLng.toString());
            } else {
                Log.d("MAP", "is still NULL");
            }

    }



    public void PopulateUserTripsMap ()
    {
        for(TripInfo tripInfo:MainActivity.tripInfoQ)
        {
            if(tripInfo.getdestination_lat()!=null&tripInfo.getorigin_lat()!=null)
            {
                Double lat = Double.parseDouble(tripInfo.getdestination_lat());
                Double lng = Double.parseDouble(tripInfo.getdestination_lng());
                AddPinTOMap(new LatLng(lat,lng), tripInfo.gettrip_name());
            }
            else
            {
                Log.d("MAP", "Some locations can't be pinned due to invalid co-ordinates");
            }

        }
    }
    public static MapFragment newInstance(String title) {
        MapFragment frag = new MapFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


}
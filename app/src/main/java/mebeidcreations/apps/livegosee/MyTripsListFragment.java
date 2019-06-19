package mebeidcreations.apps.livegosee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.LinkedList;
import java.util.List;


/**
 * Created by Mazen Ebeid on 11-12-2015.
 */
public class MyTripsListFragment extends Fragment {

    static TextView noTripsMessage, numberOfSelectedTrips;
    static RecyclerView userTripsRV;
    ImageButton filterButton, showTripsMap, deleteTripsButton,closeOptions,shareSelectedTripsButton;
    static UserTripsListAdapter userTripsListAdapter;
    FirebaseDBHandler firebaseDBHandler;
    EditText searchTripsField;
    boolean searched = false;
    static RelativeLayout myTripsListBottomToolbarDeleteOptions;
    static boolean filtered = false;


   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View rootView = inflater.inflate(R.layout.fragment_my_trips_list, container, false);


       initializeViews(rootView);




       MainActivity.progressDialog.show();

       MainActivity.BACK_NAVIGATION = "HOME_FEED";

       if(MainActivity.tripInfoQ.size()>0) {
           noTripsMessage.setVisibility(View.GONE);
       }
       else
       {
           noTripsMessage.setVisibility(View.VISIBLE);
       }

       resetTripsQueue();

       firebaseDBHandler  = new FirebaseDBHandler(getActivity().getApplication());
       firebaseDBHandler.GetUserTripsList(MainActivity.currentUserId); //fetching trips from DB;


       return rootView;

   }

    public static MyTripsListFragment newInstance(String title) {
        MyTripsListFragment frag = new MyTripsListFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("TRIPS_LIST", "PAUSED");
        userTripsRV.setClickable(false);


    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("TRIPS_LIST", "RESUMED");
        userTripsRV.setClickable(true);

    }

    public void resetTripsQueue() {
        Log.d("TRIPS_QUEUE", "Q has " + MainActivity.tripInfoQ.size() + " trips");
        if (!MainActivity.tripInfoQ.isEmpty()) {

            MainActivity.tripInfoQ.clear();

            Log.d("TRIPS_QUEUE",  "Trips Q cleared");
        }
    }

public void initializeViews(View rootView)
{
     myTripsListBottomToolbarDeleteOptions = (RelativeLayout)rootView.findViewById(R.id.mytrips_list_botom_toolbar);

     deleteTripsButton = (ImageButton)rootView.findViewById(R.id.delete_selected_trips_button);
     deleteTripsButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             Log.d("DELETING", ""+userTripsListAdapter.selectedTripsList.size()+ " Trips");

             if(userTripsListAdapter.selectedCount==0)
             {
                 Toast.makeText(getActivity().getApplicationContext(), "Please select a trip to delete", Toast.LENGTH_SHORT).show();
             }
             else
             {

                 firebaseDBHandler.DeleteTripInfo(MainActivity.currentUserId,userTripsListAdapter.selectedTripsList);

                 userTripsListAdapter.tripsInfoList.removeAll(userTripsListAdapter.selectedTripsList);
                 userTripsListAdapter.selectedTripsList.clear();
                 for(int i=0; i<userTripsListAdapter.tripContainers.size() ;i++)
                 {
                     RelativeLayout l = (RelativeLayout)userTripsListAdapter.tripContainers.get(i);
                     l.setBackgroundResource(R.drawable.trip_list_item_background);
                 }


                 userTripsListAdapter.selectedCount = 0;
                 if(userTripsListAdapter.tripsInfoList.size()>0)
                 {
                     numberOfSelectedTrips.setText(userTripsListAdapter.selectedCount+" Selected");
                 }
                 else
                 {
                     myTripsListBottomToolbarDeleteOptions.setVisibility(View.INVISIBLE);
                 }

             }


         }
     });

    shareSelectedTripsButton = (ImageButton)rootView.findViewById(R.id.share_selected_trip_button);



     closeOptions = (ImageButton)rootView.findViewById(R.id.close_mytrips_options);
     closeOptions.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             exitSelectionMode();
             Log.d("Button", "close");
         }
     });
     numberOfSelectedTrips = (TextView)rootView.findViewById(R.id.number_of_selected_trips_text);

    searchTripsField = (EditText)rootView.findViewById(R.id.my_trips_search_field);
    searchTripsField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus)
            {
                Log.d("SEARCH_FIELD", "FOCUSED");
                userTripsListAdapter.tripsInfoList.clear();
                firebaseDBHandler.GetUserTripsList(MainActivity.currentUserId);
                userTripsListAdapter.tripsInfoList.addAll(MainActivity.tripInfoQ);
            }
        }
    });


    searchTripsField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {


            searched = true;
            if(EditorInfo.IME_ACTION_GO==actionID)
            {
                Log.d("MAIN_LIST", ""+MainActivity.tripInfoQ.size());

                String searchKey = searchTripsField.getText().toString().toLowerCase();
                List<TripInfo> searchResults = new LinkedList<TripInfo>();
                for(TripInfo tmp: MainActivity.tripInfoQ)
                {
                    if(tmp.gettrip_search_string().contains(searchKey))
                    {
                        if (searchResults.contains(tmp))
                        {
                            Log.d("SEARCH_RESULT", "TRIP is duplicated skipping...");
                        }
                        else
                        {
                            Log.d("SEARCH_RESULT", "ADDING Trip to results list"+ tmp.gettrip_id());
                            searchResults.add(tmp);
                        }
                    }
                }
               // Toast.makeText(getContext(), "Searching for "+searchKey, Toast.LENGTH_SHORT).show();
                if(searchResults.size()>0)
                {
                    userTripsListAdapter.tripsInfoList.clear();
                    userTripsListAdapter.tripsInfoList.addAll(searchResults);
                    userTripsListAdapter.notifyDataSetChanged();

                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "No trips found matching your keyword!!", Toast.LENGTH_SHORT).show();
                }
                searchTripsField.setText("");
                searchTripsField.clearFocus();
            }

            return false;



        }


    });


    showTripsMap = (ImageButton)rootView.findViewById(R.id.show_trips_map);
    showTripsMap.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(MainActivity.tripInfoQ.size()>0)
            {
                AddFragment(new MapFragment(), R.id.main_activity_fragments_container);

            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "You have no trips to view", Toast.LENGTH_SHORT).show();
            }

            searchTripsField.clearFocus();
            searchTripsField.setText("");
        }
    });

    filterButton = (ImageButton)rootView.findViewById(R.id.my_trips_filter_button);
    filterButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {



            exitSelectionMode();
            searchTripsField.clearFocus();
            searchTripsField.setText("");
            if(filtered)
            {
                userTripsListAdapter.tripsInfoList.clear();
                firebaseDBHandler.GetUserTripsList(MainActivity.currentUserId);
                userTripsListAdapter.tripsInfoList.addAll(MainActivity.tripInfoQ);
                FilterTripsDialogFragment.filteredTrips.clear();
                FilterTripsDialogFragment.filters.clear();
                Log.d("FILTER", "TRUE");
            }
            else
            {

            }
            showFilterDialog();
        }
    });

    noTripsMessage = (TextView)rootView.findViewById(R.id.my_trips_list_no_trips_msg);
    userTripsRV = (RecyclerView)rootView.findViewById(R.id.user_trips_rv);
    userTripsRV.setHasFixedSize(true);
    LinearLayoutManager llm = new LinearLayoutManager(getContext());
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    userTripsRV.setLayoutManager(llm);
    userTripsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);



            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
            {
               MainActivity.translateToolbarUp();
            }
            else
            {
                MainActivity.translateToolbarDown();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);



        }
    });
    userTripsListAdapter  = new UserTripsListAdapter(MainActivity.tripInfoQ);
    userTripsRV.setAdapter(userTripsListAdapter);
    resetTripsQueue();



}
    public void showFilterDialog() {

        FilterTripsDialogFragment filterDialog = FilterTripsDialogFragment.newInstance("NEW_TRIP_DIALOG_FRAGMENT");
        filterDialog.show(getFragmentManager(), "TAG");


    }

    public   void AddFragment(Fragment fragment, int container) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commitNow();

    }

    public void exitSelectionMode()
    {

        userTripsListAdapter.longPress=false;
        userTripsListAdapter.selectedCount = 0;
        for(int i=0; i<userTripsListAdapter.tripContainers.size() ;i++)
        {
            RelativeLayout l = (RelativeLayout)userTripsListAdapter.tripContainers.get(i);
            l.setBackgroundResource(R.drawable.trip_list_item_background);

        }
        for(int i=0; i<userTripsListAdapter.editTripButtons.size(); i++)
        {
            Button b = (Button)userTripsListAdapter.editTripButtons.get(i);
            b.setVisibility(View.INVISIBLE);
        }
        userTripsListAdapter.selectedTripsList.clear();
        myTripsListBottomToolbarDeleteOptions.setVisibility(View.GONE);
        numberOfSelectedTrips.setText("Selected");
    }


}


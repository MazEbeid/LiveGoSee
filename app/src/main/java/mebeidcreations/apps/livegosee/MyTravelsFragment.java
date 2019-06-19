package mebeidcreations.apps.livegosee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


public class MyTravelsFragment extends Fragment {

    ImageButton backButton;
    ImageButton addNewTripButton, viewMyClipsButton, viewMyTripsButton;


    ///////////////////////////////On create Method shite
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.BACK_NAVIGATION ="HOME_FEED";
       final View rootView = inflater.inflate(R.layout.fragment_my_travels, container, false);
        ////////////////////////// /////////////////////////////




            viewMyTripsButton = (ImageButton)rootView.findViewById(R.id.view_my_trips);
              viewMyTripsButton.setOnClickListener(new View.OnClickListener() {
               @Override
                  public void onClick(View v) {

                     AddFragment(new MyTripsListFragment(),R.id.main_activity_fragments_container);

            }
        });

           addNewTripButton = (ImageButton)rootView.findViewById(R.id.adD_new_trip);
           addNewTripButton.setOnClickListener(new View.OnClickListener() {
           @Override
               public void onClick(View v) {

                    AddFragment(new AddNewTripFragment(), R.id.main_activity_fragments_container);



               }
               });

              viewMyClipsButton = (ImageButton)rootView.findViewById(R.id.view_my_clips);
              viewMyClipsButton.setOnClickListener(new View.OnClickListener() {
                  @Override

           public void onClick(View v) {

                   Toast.makeText(getActivity().getApplicationContext(), "My Clips", Toast.LENGTH_SHORT).show();

            }
               });






        return rootView;
        }

    public   void AddFragment(Fragment fragment, int container) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_down,R.anim.fade_out);
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commitNow();

    }

}
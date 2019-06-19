package mebeidcreations.apps.livegosee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import static mebeidcreations.apps.livegosee.AddNewTripFragment.newTripId;
import static mebeidcreations.apps.livegosee.AddTripTransportFragment.chipViewsList;
import static mebeidcreations.apps.livegosee.AddTripTransportFragment.chipsContainer;
import static mebeidcreations.apps.livegosee.AddTripTransportFragment.numberOfTransportAddedText;
import static mebeidcreations.apps.livegosee.AddTripTransportFragment.transportModesObjectList;


public class AddTripOptionsFragment extends Fragment {

    ImageButton addGettingThereButton, addAccommoButton, addWeatherButton, addMusicButton,
            addPhotosButton, addVideosButton, addFriendsButton, addActivityButton;
    TextView finishButton;
    FragmentTransaction fragmentTransaction;
    FirebaseDBHandler firebaseDBHandler;
    static AlertDialogAction moreOptionsDialogAction;
    ///////////////////////////////On create Method shite
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       final View rootView = inflater.inflate(R.layout.choose_more_options_fragment, container, false);
        ////////////////////////// /////////////////////////////

        MainActivity.BACK_NAVIGATION = "SHOW_CHOOSE_MORE_OPTIONS_LATER";
        moreOptionsDialogAction = new AlertDialogAction() {
            @Override
            public void positiveAction() {

            }

            @Override
            public void negativeAction() {

                AddFragment(new MyTripsListFragment(),R.id.main_activity_fragments_container);
            }
        };

        firebaseDBHandler = new FirebaseDBHandler(getContext());
        finishButton = (TextView)rootView.findViewById(R.id.finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseDBHandler.addTransportMode(newTripId,"GettingThere",transportModesObjectList);
                chipViewsList.clear();
                transportModesObjectList.clear();
                if(chipsContainer!=null)
                {
                    chipsContainer.removeAllViewsInLayout();
                }

                AddFragment(new MyTripsListFragment(), R.id.main_activity_fragments_container);
            }
        });
        addGettingThereButton = (ImageButton)rootView.findViewById(R.id.add_transport_button);
        addGettingThereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddFragment(new AddTripTransportFragment(), R.id.main_activity_fragments_container);


            }
        });


        addAccommoButton = (ImageButton)rootView.findViewById(R.id.add_accomo_button);
        addAccommoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement accomodation fragment
            }
        });

        addWeatherButton = (ImageButton)rootView.findViewById(R.id.add_weather_button);
        addWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //implement weather fragment
            }
        });

        addMusicButton = (ImageButton)rootView.findViewById(R.id.add_weather_button);
        addMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement music fragment
            }
        });

        addPhotosButton = (ImageButton)rootView.findViewById(R.id.add_images_button);
        addPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement add photos fragment
            }
        });

        addVideosButton = (ImageButton)rootView.findViewById(R.id.add_video_button);
        addVideosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement add Videos fragment
            }
        });

        addFriendsButton = (ImageButton)rootView.findViewById(R.id.add_friends_button);
        addFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Implement add friends fragment
            }
        });

        addActivityButton = (ImageButton)rootView.findViewById(R.id.add_more_event_button);
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement add activity fragment
            }
        });

        return rootView;
    }

    public void AddFragment(Fragment fragment, int container) {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.card_flip_left_in,R.anim.fade_out);

        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();

    }


}
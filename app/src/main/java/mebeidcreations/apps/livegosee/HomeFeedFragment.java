package mebeidcreations.apps.livegosee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class HomeFeedFragment extends Fragment {

    LinearLayout homeFeedLL;
    TextView friendsDesc, myTravelsDesc, inspirationDesc;
    ImageButton  expandMyTravelsButton, expandFriendsButton, expandInspirationButton;
    RelativeLayout friendsExpansionRL, myTravelsExpansionRL, inspirationExpansionRL;
    static FragmentTransaction fragmentTransaction;
    ///////////////////////////////On create Method shite
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainActivity.BACK_NAVIGATION = "EXIT_APP";

//        MainActivity.bottomToolbar.setVisibility(View.VISIBLE);

/////////////////////////////////////////////////
        //////////////Main root view shite
        final View rootView = inflater.inflate(R.layout.fragment_home_feed, container, false);
        ////////////////////////// /////////////////////////////
        initializeViews(rootView, inflater);

        return  rootView;

    }



    public   void AddFragment(Fragment fragment, int container) {

       fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commitNow();
    }

    public  void initializeViews(View rootView, LayoutInflater inflater)
    {

        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_down,R.anim.fade_out);

        friendsDesc = (TextView)rootView.findViewById(R.id.friends_desc);
        inspirationDesc = (TextView)rootView.findViewById(R.id.inspiration_desc);
        myTravelsDesc = (TextView)rootView.findViewById(R.id.my_travels_desc);

        friendsExpansionRL = (RelativeLayout)rootView.findViewById(R.id.friends_expansion_layout);
        myTravelsExpansionRL=(RelativeLayout)rootView.findViewById(R.id.mytravels_expansion_layout);
        inspirationExpansionRL = (RelativeLayout)rootView.findViewById(R.id.inspiration_expansion_layout);


        expandFriendsButton = (ImageButton)rootView.findViewById(R.id.expand_friends_button);
        expandFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddFragment(new FriendsTravelsFragment(),R.id.main_activity_fragments_container);

            }
        });

        expandInspirationButton = (ImageButton)rootView.findViewById(R.id.expand_inspiration_button);
        expandInspirationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 AddFragment(new TravelInspirationFragment(),R.id.main_activity_fragments_container);

            }
        });


        expandMyTravelsButton = (ImageButton)rootView.findViewById(R.id.expand_mytravels_button);
        expandMyTravelsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentTransaction.setCustomAnimations(R.anim.slide_up,R.anim.fade_out);
               AddFragment(new MyTravelsFragment(),R.id.main_activity_fragments_container);

            }
        });



    }



}
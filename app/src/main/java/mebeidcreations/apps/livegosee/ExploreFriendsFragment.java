package mebeidcreations.apps.livegosee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ExploreFriendsFragment extends Fragment {


    ///////////////////////////////On create Method shite
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.BACK_NAVIGATION ="HOME_FEED";
       final View rootView = inflater.inflate(R.layout.fragment_explore_friends, container, false);
        ////////////////////////// /////////////////////////////

        return rootView;
    }
}
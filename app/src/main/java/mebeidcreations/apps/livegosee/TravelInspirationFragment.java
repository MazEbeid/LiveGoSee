package mebeidcreations.apps.livegosee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class TravelInspirationFragment extends Fragment {

    ImageButton backButton;

    ///////////////////////////////On create Method shite
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.BACK_NAVIGATION ="";
       final View rootView = inflater.inflate(R.layout.fragment_inspiration, container, false);
        ////////////////////////// /////////////////////////////

        return rootView;
    }
    public   void AddFragment(Fragment fragment, int container) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_up,R.anim.fade_out);
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commitNow();

    }
}
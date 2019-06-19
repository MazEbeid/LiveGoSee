package mebeidcreations.apps.livegosee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Mazen Ebeid on 22/02/2018.
 */

public class TransportDetailDialog  extends DialogFragment {


    TextView transportOrigin, transportDestination, transportDate, transportTime, transportNotes, transportNotification, editTransportButton, titleText, transportDuration;
    Button closeTransportDialogButton;
    static TransportMode transport;
    FragmentTransaction fragmentTransaction;
    ImageView transportImage1, transportImage2;
    static int chipViewId;
    public TransportDetailDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static TransportDetailDialog newInstance(String title, TransportMode transportMode) {
        TransportDetailDialog frag = new TransportDetailDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        transport = transportMode;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        return inflater.inflate(R.layout.transport_chip_detail_dialog, container);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        editTransportButton = (TextView)view.findViewById(R.id.edit_button);
        editTransportButton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {

                Log.d("CHIP_VIEW", "REMOVING chip "+chipViewId);
                for(View chip:AddTripTransportFragment.chipViewsList)
                {
                    if(chip.getId()==chipViewId)
                    {
                        Log.d("CHIP_VIEW", "REMOVED");
                        AddTripTransportFragment.chipsContainer.removeView(chip);
                        AddTripTransportFragment.chipViewsList.remove(chip);
                        return;
                    }
                }

               /* for(TransportMode transportMode:AddTripTransportFragment.transportModesObjectList)
                {
                    Log.d("TRANSPORT_OBJECTS_LIST", "Comparing IDs "+chipViewId+" and "+transportMode.gettransport_chipId());
                    if(Integer.parseInt(transportMode.gettransport_chipId())==chipViewId)
                    {
                        Log.d("TRANSPORT_OBJECTS_LIST","Found object with id: "+chipViewId );
                        AddTripTransportFragment.transportModesObjectList.remove(transportMode);
                        Log.d("TRANSPORT_OBJECTS_LIST", "OBJECT REMOVED");
                        return;

                    }
                 }*/


                Log.d("CHIP_CONTAINER", "has "+AddTripTransportFragment.chipsContainer.getChildCount()+" children");
                Log.d("CHIP_OBJECTS", ""+AddTripTransportFragment.transportModesObjectList.size());

                AddTripTransportFragment.chipId = transport.gettransport_chipId();
                AddTripTransportFragment.showDefineLayout(transport.gettransport_type(),true);
                dismiss();


            }
        });

        transportDuration = (TextView)view.findViewById(R.id.transport_detail_duration);
        transportDuration.setRotation(-90);
        transportDuration.setText("2 Hrs and 35 Mins");
        transportOrigin = (TextView)view.findViewById(R.id.transport_detail_origin);
        transportOrigin.setText(transport.gettransport_origin());

        transportDestination = (TextView)view.findViewById(R.id.transport_detail_destination);
        transportDestination.setText(transport.gettransport_destination());

        transportDate = (TextView)view.findViewById(R.id.transport_detail_date);
        transportDate.setText(transport.gettransport_date());

        transportTime = (TextView)view.findViewById(R.id.transport_detail_time);
        transportTime.setText(transport.gettransport_time());

        transportNotes = (TextView)view.findViewById(R.id.transport_detail_notes);
        transportNotes.setText(transport.gettransport_notes());

        titleText = (TextView)view.findViewById(R.id.title_text);
        titleText.setText(transport.gettransport_type());

        transportImage1 = (ImageView)view.findViewById(R.id.transportImage_1);
        transportImage2 = (ImageView)view.findViewById(R.id.transportImage_2);
        String mDrawableName = transport.gettransport_type().toLowerCase().replace(" ", "") + "_transport";
        int resID = getResources().getIdentifier(mDrawableName, "drawable", getActivity().getApplicationContext().getPackageName());
        transportImage1.setImageResource(resID);
        transportImage2.setImageResource(resID);

        transportNotification = (TextView)view.findViewById(R.id.transport_notification_interval);
        transportNotification.setText(transport.gettransport_notification_interval());

    }
    public void AddFragment(Fragment fragment, int container) {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(!AddNewTripFragment.initialFrag)
        {
            if(AddNewTripFragment.moreFlag==false) //enter,exit
            {
                fragmentTransaction.setCustomAnimations(R.anim.card_flip_left_in,R.anim.fade_out);

            }
            else if(AddNewTripFragment.moreFlag==true)
            {
                fragmentTransaction.setCustomAnimations(R.anim.card_flip_right_in,R.anim.fade_out);
            }
        }


        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();

    }


}

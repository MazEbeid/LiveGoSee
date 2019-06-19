package mebeidcreations.apps.livegosee;


import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mazen Ebeid on 05/09/2017.
 */

public class FilterTripsDialogFragment extends DialogFragment implements CheckedBox, View.OnClickListener {

    Helper helper;
    boolean clicked = false;
    static boolean fromFlag = false;
    static boolean toFlag = false;
    static LinkedList<String> filters;
    int FILTER_TRIPS_FRAG = 4 ;
    static LinkedList<TripInfo>filteredTrips;
    LinkedList<TripInfo> mainList;
    CheckBox privateCheck, publicCheck, europeCheck, asiaCheck, africaCheck, americasCheck;
    static ImageButton filterFromButton, filterToButton;
    static TextView filterFromText, filterToText;
    Button applyFiltersButton;
    public FilterTripsDialogFragment() {
    }

    public static FilterTripsDialogFragment newInstance(String title) {
        FilterTripsDialogFragment frag = new FilterTripsDialogFragment();
        frag.setStyle(STYLE_NORMAL,R.style.NewTripDialogStyle);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        helper = new Helper(getActivity().getApplicationContext());
        
        return inflater.inflate(R.layout.filter_trips_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        filters = new LinkedList<>();
        filteredTrips = new LinkedList<>();
        mainList = new LinkedList<>();
        mainList.addAll(MainActivity.tripInfoQ);
        initializeViews(view);




    }

    public void Toasting(String msg) {

        Toast toast = Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.YELLOW);
        toast.show();
    }


    public void initializeViews(View view)
    {

        filterFromText = (TextView)view.findViewById(R.id.filter_from_text);
        filterFromButton = (ImageButton)view.findViewById(R.id.filte_from_date_button);
        filterFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromFlag = true;
                toFlag = false;
                showDatePicker();

            }
        });
        filterToText = (TextView)view.findViewById(R.id.filter_to_text);
        filterToButton= (ImageButton)view.findViewById(R.id.filter_to_button);
        filterToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFlag = true;
                fromFlag = false;
                showDatePicker();
            }
        });

        africaCheck = (CheckBox)view.findViewById(R.id.africa_checkbox);
        africaCheck.setOnClickListener(this);
        europeCheck = (CheckBox)view.findViewById(R.id.europ_checkbox);
        europeCheck.setOnClickListener(this);
        americasCheck = (CheckBox)view.findViewById(R.id.americas_checkbox);
        americasCheck.setOnClickListener(this);
        asiaCheck = (CheckBox)view.findViewById(R.id.asia_checkbox);
        asiaCheck.setOnClickListener(this);
        publicCheck = (CheckBox)view.findViewById(R.id.public_checkbox);
        publicCheck.setOnClickListener(this);
        privateCheck = (CheckBox)view.findViewById(R.id.private_checkbox);
        privateCheck.setOnClickListener(this);
        applyFiltersButton  = (Button)view.findViewById(R.id.apply_filters_button);
        applyFiltersButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            filterTripsWithKey(filters,filteredTrips);
            if(helper.validDates(filterFromText.getText().toString(),filterToText.getText().toString()))
            {
                filterTripsWithDate(filterFromText,filterToText,filteredTrips);
            }
                if(filteredTrips.size()==0)
                {
                    Toasting("You don't have any trips matching your filters");
                    filteredTrips.clear();
                    filters.clear();
                    dismiss();

                }
                else
                {
                    MyTripsListFragment.userTripsListAdapter.tripsInfoList.clear();
                    MyTripsListFragment.userTripsListAdapter.tripsInfoList.addAll(filteredTrips);
                    MyTripsListFragment.userTripsListAdapter.notifyDataSetChanged();
                    dismiss();
                    MyTripsListFragment.filtered = true;

                }






        }
    });
}
    @Override
    public void onChecked(View v) {
        CheckBox box = (CheckBox)v;
        if(box.isChecked())
        {
            filters.add(box.getText().toString().toLowerCase());
        }
        else {
            if(filters.contains(box.getText().toString().toLowerCase()))
            {
                filters.remove(box.getText().toString().toLowerCase());
            }
            else
            {
                //Do nothing
            }

        }

        for(String f: filters)
        {
            Log.d("FILTERS_LIST", f);
        }
    }
    @Override
    public void onClick(View v) {
        CheckBox box = (CheckBox)v;
        onChecked(box);
    }
    public void showDatePicker()
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "Age");

    }
    public List<TripInfo> filterTripsWithKey(List<String> filters, List<TripInfo> recursiveList)
    {
        Log.d("FILTERING_FROM_LIST", ""+MainActivity.tripInfoQ.size());

        for(String filter: filters)
        {
            Log.d("FILTER", filter);
            for (TripInfo temp: MainActivity.tripInfoQ)
            {
                if(temp.gettrip_search_string().contains(filter))
                {
                    if(recursiveList.contains(temp))
                    {
                        Log.d("FILTERING","Trip is already included in filtered list" );

                    }
                    else
                    {
                        recursiveList.add(temp);
                        Log.d("FILTERING","Adding Trip to filtered list" );
                    }

                }
                else
                {
                    Log.d("FILTERING_KEY", "FALSE");

                }
            }
        }



        for(TripInfo temp: recursiveList)
        {
            Log.d("FILTERED_TRIPS", temp.gettrip_id());

        }
        return recursiveList;
    }

    public List<TripInfo> filterTripsWithDate(TextView rangeFrom, TextView rangeTo, List<TripInfo>recursiveList)
    {

        try
        {
            String from = rangeFrom.getText().toString();
            String to = rangeTo.getText().toString();

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date fromDate = formatter.parse(from);
            Date toDate = formatter.parse(to);

            Calendar fDate = Calendar.getInstance();
            Calendar tDate = Calendar.getInstance();
            fDate.setTime(fromDate);
            tDate.setTime(toDate);

            for(TripInfo trip: MainActivity.tripInfoQ)
            {
                String tripFromDate = trip.gettrip_from_date();
                String tripToDate = trip.gettrip_to_date();
                Date tripFrom = formatter.parse(tripFromDate);
                Date tripTo = formatter.parse(tripToDate);
                Calendar tFdate = Calendar.getInstance();
                Calendar tTDate = Calendar.getInstance();
                tFdate.setTime(tripFrom);
                tTDate.setTime(tripTo);

                if(DateUtils.isSameDay(fDate,tTDate))
                {
                    if(!recursiveList.contains(trip))
                    {
                        recursiveList.add(trip);   //last day of trip is first day in range
                    }
                }
                if(DateUtils.isSameDay(tDate,tFdate))
                {
                    if(!recursiveList.contains(trip))
                    {
                        recursiveList.add(trip);   //first day of trip is last day in range
                    }
                }

                if(DateUtils.isAfterDay(tFdate,fDate))
                {
                    if(DateUtils.isBeforeDay(tFdate,tDate))
                    {
                        if(!recursiveList.contains(trip))
                        {
                            recursiveList.add(trip);   //first day of trip is last day in range
                        }
                    }
                }
            }

        }catch (Exception e)
        {
            Log.d("EXCEPTION", e.getMessage());
        }

        for(TripInfo trip: recursiveList)
        {
            Log.d("FILTERING_BY_DATE", "From: "+trip.gettrip_from_date()+" To "+trip.gettrip_to_date());
        }



        return recursiveList;
    }

}
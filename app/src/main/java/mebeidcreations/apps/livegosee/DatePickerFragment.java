package mebeidcreations.apps.livegosee;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public  class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    static String dateSet="";
    static Date returnDate;
    static  Calendar returnCal;
    static int setYear, setMonth, setDay;
    static TextView dateText;


    public static DatePickerFragment newInstance(TextView dateTextView) {
        DatePickerFragment frag = new DatePickerFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        dateText  = dateTextView;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (dateSet.isEmpty()) {
            return new DatePickerDialog(getContext(), R.style.MyDialogTheme, this, year, month, day);
        } else {
            try {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                returnDate = formatter.parse(dateSet);
                Log.d("START DATE", dateSet);

            } catch (Exception e) {
                Log.d("EXCEPTION", e.getMessage());
            }

            return new DatePickerDialog(getContext(), R.style.MyDialogTheme, this, setYear, setMonth, setDay);
        }
        // Create a new instance of DatePickerDialog and return it
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {

        String m = "";



        dateSet = addZero(day) + "/" +fixMonth(month)+ "/" + year;
        setDay = day;
        setMonth = month;
        setYear = year;
        Log.d("act", getActivity().toString());

        dateText.setText(dateSet);
        dateSet = "";
    }

    public String addZero(int number)
    {
        if(number <=9)
        {
            return "0"+number;
        }
        return ""+number;
    }

    public String fixMonth(int number)
    {
        if(number <=9)
        {
            number++;
            return "0"+number;
        }
        number++;
        return ""+number;
    }

}
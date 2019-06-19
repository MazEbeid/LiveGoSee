package mebeidcreations.apps.livegosee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * Created by Mazen Ebeid on 29/09/2017.
 */

public class Helper {

    static Context context;
    public Helper(Context context)
    {

        this.context = context;
    }



    public static boolean validateTextViews(List<TextView> forms) {
        boolean valid = true;

        for(int i=0; i<forms.size(); i++)
        {
            String s = forms.get(i).getText().toString();

            if (TextUtils.isEmpty(s)) {
                forms.get(i).setError("Required.");
                valid = false;
            } else {
                forms.get(i).setError(null);
            }

        }

        return valid;
    }



    public static boolean validDates(String checkin_date, String checkout_date)
    {
        boolean valid = false;

        Log.d("DATE-BEFORE-TRY", checkin_date+" "+checkout_date);
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            Date fromDate = formatter.parse(checkin_date);
            Date toDate = formatter.parse(checkout_date);

            Calendar fDate = Calendar.getInstance();
            Calendar tDate = Calendar.getInstance();

            fDate.setTime(fromDate);
            tDate.setTime(toDate);
            Log.d("DATES", "" + fromDate + " " + toDate);


            if (DateUtils.isAfterDay(fDate, tDate)) {
                valid = false;
                Toasting("Please enter a valid date");

            } else {
                valid = true;

            }

        }


        catch (Exception e)
        {
            Log.d("EXCEPTION",e.toString());
        }
        return valid;

    }




    public static void Toasting(String msg) {

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    public static boolean validateTransportFields(TextView date, TextView time, List <AutoCompleteTextView> locations, String tripStartDate, String tripEndDate) {

        List<TextView> textViews = new ArrayList<TextView>();
        textViews.add(date);
        textViews.add(time);
        textViews.addAll(locations);

        if (validateTextViews(textViews))
        {
            if(validDates(tripStartDate,date.getText().toString()))
            {
                Log.d("TRANSPORT_DATE", "Date is after trip starts");
                if(validDates(date.getText().toString(), tripEndDate))
                {
                    Log.d("TRANSPORT_DATE", "Date is before trip ends");
                    return true;
                }
            }
            else
            {
                Log.d("TRANSPORT_DATE", "Date is either before trip start or after trip ends");
                return false;
            }
        }
        else
        {

            return false;
        }

        return  false;


    }
    public static void showDatePicker(TextView dateText, FragmentManager fragmentManager)
    {
        DialogFragment datePickerFragment = DatePickerFragment.newInstance(dateText);
        datePickerFragment.show(fragmentManager, "NEW_TRIP_DATES");
    }




    public static void showAlertDialog(String title, String message, String positiveAction, String negativeAction, Activity activity, final AlertDialogAction action )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MyAlertDialog);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(positiveAction, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                action.positiveAction();
            }
        });
        builder.setNegativeButton(negativeAction, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                action.negativeAction();

            }
        });
        builder.show();


    }
    public static void AddFragment(Fragment fragment, int container, FragmentManager fragmentManager) {

        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commitNow();

    }


}

package mebeidcreations.apps.livegosee;

/**
 * Created by Mazen Ebeid on 19/03/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static mebeidcreations.apps.livegosee.Helper.Toasting;


public class FirebaseDBHandler {
    static UserInfo userInfo;
    static TripInfo tripInfo;
    static Context context;
    static List<HotelInfo> hotelInfoList;
    static DatabaseReference databaseReference;
    static StorageReference storageReference;
    static boolean isPersistenceEnabled = false;
    static boolean isNotified = false;


    public FirebaseDBHandler(Context context) {

        this.context = context;    // context can be used to call PreferenceManager etc.

        if (!isPersistenceEnabled) {
           FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isPersistenceEnabled = true;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("TripInfo").keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        
    }


    public static void addUserInfoToDB(UserInfo userInfo, String userId) {
        databaseReference.child("UserInfo").child(userId).setValue(userInfo);
    }


    public static void addTripInfoToDB(TripInfo tripInfo, String userID) {
        databaseReference.child("TripInfo").child(userID).child(tripInfo.gettrip_id()).setValue(tripInfo);

        UserInfo userInfo = findUserById(userID);
        int count = userInfo.getuser_trip_count();
        count++;
        databaseReference.child("UserInfo").child(userID).child("user_trip_count").setValue(count);
        Log.d("DATABASE-UPDATE", "Trip count " + count);
      //  Log.d("DATABASE-UPDATE", "Trip thumbnail uri " + tripInfo.gettrip_thumbnail_uri());

    }

    public static void addTripDetailObject(String trip_id, HotelInfo hotelInfo) {
        databaseReference.child("TripDetails").child(trip_id).child(hotelInfo.gethotel_id()).setValue(hotelInfo);

    }

    public static void addTransportMode(String trip_id, String DBParent,List<TransportMode> transportModesList)
    {       int i = 0;

        if(transportModesList.size()==0)
        {
            Log.d("TRANSPORT_DB", "There are no transport modes to be added");
        }
        for(TransportMode transport:transportModesList)
        {
            databaseReference.child("TripDetails").child(trip_id).child(DBParent).push().setValue(transport);
            Log.d("TRANSPORT_DB", "Added transport "+transport.gettransport_type());
            i++;
        }

    }

    public static UserInfo findUserById(String userID) {


        databaseReference.child("UserInfo").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userInfo = dataSnapshot.getValue(UserInfo.class);
                MainActivity.userName.setText(userInfo.getuser_name());
                MainActivity.user_first_name = userInfo.getuser_name();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userInfo;


    }

    public static void GetUserTripsList(final String userId) {

        MainActivity.progressDialog.show();
        databaseReference.child("TripInfo").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                       MainActivity.tripInfoQ.add(child.getValue(TripInfo.class));
                       Log.d("TRIP-DB","ADDING" );

                }

                if (MainActivity.tripInfoQ.size() == 0) {
                    MyTripsListFragment.noTripsMessage.setVisibility(View.VISIBLE);
                } else {
                    MyTripsListFragment.noTripsMessage.setVisibility(View.GONE);
                }

                Log.d("FIREBASE_GETTING_TRIPS", MainActivity.tripInfoQ.size() + " FETCHED TRIPS from DB");
                if (MyTripsListFragment.userTripsListAdapter != null) {
                    Log.d("FIREBASE_GETTING_TRIPS", "NOTIFIED");
                    MyTripsListFragment.userTripsListAdapter.notifyDataSetChanged();
                    MainActivity.progressDialog.dismiss();
                    databaseReference.child("TripInfo").child(userId).removeEventListener(this);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                MainActivity.progressDialog.dismiss();


            }
        });


    }


    public static void GetTripHotelsList(String tripId) {
        MainActivity.progressDialog.show();
        databaseReference.child("TripDetails").child(tripId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    MyTripLogActivity.hotelInfoQ.add(child.getValue(HotelInfo.class));
                    Log.d("HOTEL", child.getValue(HotelInfo.class).gethotel_name());
                    Log.d("HOTELS Q", "" + MyTripLogActivity.hotelInfoQ.get(0).gethotel_name());

                }
                MyTripLogActivity.setHotelViews(MyTripLogActivity.hotelInfoQ);
                MainActivity.progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                MainActivity.progressDialog.dismiss();


            }
        });


    }

    public void DeleteTripInfo(String user_id, final List<TripInfo> selectedTripsToDeleteList) {


        for (int i = 0; i < selectedTripsToDeleteList.size(); i++) {

            Log.d("DELETION", selectedTripsToDeleteList.get(i).gettrip_id());
            databaseReference.child("TripInfo").child(user_id).child(selectedTripsToDeleteList.get(i).gettrip_id()).removeValue();
            FirebaseDBHandler.storageReference.child(user_id).child(selectedTripsToDeleteList.get(i).gettrip_id()).child("Thumbnails").child(selectedTripsToDeleteList.get(i).gettrip_thumbnail_stamp()).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                      Log.d("DELETION", "Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                    Log.d("DELETION", "Failed");
                }
            });
        }

        Log.d("ITEM_DELETE", "BEFORE " + MainActivity.tripInfoQ.size());

        MainActivity.tripInfoQ.removeAll(selectedTripsToDeleteList);
        Log.d("ITEM_DELETE", "AFTER " + MainActivity.tripInfoQ.size());
        MyTripsListFragment.userTripsListAdapter.notifyDataSetChanged();


        if (MainActivity.tripInfoQ.size() == 0) {
            MyTripsListFragment.noTripsMessage.setVisibility(View.VISIBLE);
         } else {
            MyTripsListFragment.noTripsMessage.setVisibility(View.GONE);
        }

    }

    public void AddPhotoToDB(Uri uri, String user_id, String trip_id, String folder, String stamp) {

        UploadTask uploadTask = storageReference.child(user_id).child(trip_id).child(folder).child(stamp).putFile(uri);

       try
       {
           com.google.android.gms.tasks.Tasks.await(uploadTask);
       }
       catch (Exception e)
       {
           Log.d("UPLOAD_TASK", e.getMessage());

       }
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("IMAGE_BYTES",""+taskSnapshot.getBytesTransferred());
            }
       });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Log.d("IMAGE_SUCCES",""+taskSnapshot.getBytesTransferred());
         //               Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
           //             AddPhotoActivity.imageUploaded = true;

                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                //        Toast.makeText(context, "Image upload was completed", Toast.LENGTH_SHORT).show();
           //     AddPhotoActivity.progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void AddThumbnailStampToDB(String user_id, String trip_id, String stamp)
    {
        databaseReference.child("TripInfo").child(user_id).child(trip_id).child("trip_thumbnail_stamp").setValue(stamp);
    }

    public void GetThumbnailStamp(String user_id, String trip_id) {
        databaseReference.child("Thumbnails").child(user_id).child(trip_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserTripsListAdapter.thumbnailStamp = (String)dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("IMAGE-DB", "STAMP "+UserTripsListAdapter.thumbnailStamp);

    }



}
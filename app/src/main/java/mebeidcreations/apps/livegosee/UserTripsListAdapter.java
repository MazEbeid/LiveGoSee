package mebeidcreations.apps.livegosee;



import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserTripsListAdapter extends RecyclerView.Adapter<UserTripsListAdapter.UserTripsListViewHolder> {

    static boolean longPress = false;
    int selectedCount = 0;
    List<TripInfo> selectedTripsList = new ArrayList<>();
    List tripContainers = new LinkedList<>();
    List editTripButtons = new LinkedList<>();
    static int GET_FROM_GALLERY = 3;
    static FirebaseDBHandler firebaseDBHandler;
    static String thumbnailStamp = "";
    static int viewHolderPos = -1;

    public static class UserTripsListViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout tripContainer;
        TextView trip_info_destination, trip_info_name, trip_info_date;
        ImageView trip_info_thumbnail;
        Button trip_edit_thumbnail;


        private final Context context;

        UserTripsListViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            firebaseDBHandler = new FirebaseDBHandler(context);
            //initialize variables
            trip_info_destination = (TextView) itemView.findViewById(R.id.trip_list_item_destination);
            trip_info_name = (TextView) itemView.findViewById(R.id.trip_list_item_trip_name);
            trip_info_date = (TextView) itemView.findViewById(R.id.trip_list_item_date);
            trip_info_thumbnail = (ImageView) itemView.findViewById(R.id.trip_list_item_thumbnail);
            tripContainer = (RelativeLayout) itemView.findViewById(R.id.trip_item_container);
            trip_edit_thumbnail = (Button)itemView.findViewById(R.id.trip_edit_thumbnail_button);

        }


    }


    List<TripInfo> tripsInfoList;


    UserTripsListAdapter(List<TripInfo> tripCardsList) {
        this.tripsInfoList = tripCardsList;
    }


    @Override
    public int getItemCount() {
        return tripsInfoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public UserTripsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_list_item, viewGroup, false);
        UserTripsListViewHolder userTripsListViewHolder = new UserTripsListViewHolder(v);


        return userTripsListViewHolder;
    }

    @Override
    public void onBindViewHolder(final UserTripsListViewHolder userTripsListViewHolder, final int i) {
        Log.d("ADAPTER", "BINDING");


        ///find view and set text to data text of tripcard object viewHolder.view.setText
        userTripsListViewHolder.trip_info_destination.setText(tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()).getCityCountry());
        userTripsListViewHolder.trip_info_name.setText(tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()).gettrip_name());
        userTripsListViewHolder.trip_info_date.setText(tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()).getTranslatedTripMonthYear());
        userTripsListViewHolder.tripContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (longPress) {



                    buildList(tripsInfoList.get(getItemViewType(i)), userTripsListViewHolder.tripContainer, userTripsListViewHolder.trip_edit_thumbnail);
                    MyTripsListFragment.numberOfSelectedTrips.setText("" + selectedCount + " Selected");
                    Log.d("SELECTED_TRIPS", ""+selectedTripsList.size());
                    Log.d("ADAPTER_POS", ""+userTripsListViewHolder.getAdapterPosition());
                    MyTripsListFragment.myTripsListBottomToolbarDeleteOptions.setVisibility(View.VISIBLE);

                } else {

                    Intent goToTripActivity = new Intent(userTripsListViewHolder.context, MyTripLogActivity.class);
                    goToTripActivity.putExtra("selected_trip", tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()));
                    userTripsListViewHolder.context.startActivity(goToTripActivity);
                }

            }
        });

        userTripsListViewHolder.tripContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                Log.d("SELECTED_LIST", "" + selectedTripsList.size());
                MainActivity.vibrator.vibrate(100);
                longPress = true;
                MyTripsListFragment.myTripsListBottomToolbarDeleteOptions.setVisibility(View.VISIBLE);
                return longPress;
            }
        });


        userTripsListViewHolder.trip_edit_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToAddPhotoActivity = new Intent(userTripsListViewHolder.context, AddPhotoActivity.class);
                goToAddPhotoActivity.putExtra("selected_trip", tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()));
                Log.d("TRIP_FROM_ADAPTER", tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()).gettrip_id());
                goToAddPhotoActivity.putExtra("selected_trip_position", "" + userTripsListViewHolder.getAdapterPosition());
                Log.d("TRIP_POSITION_ADAPTER", "" + userTripsListViewHolder.getAdapterPosition());
                userTripsListViewHolder.context.startActivity(goToAddPhotoActivity);
                userTripsListViewHolder.trip_edit_thumbnail.setVisibility(View.INVISIBLE);
             //   userTripsListViewHolder.tripContainer.setBackgroundResource(R.drawable.trip_list_item_background);
                for(int i =0; i<selectedTripsList.size(); i++)
                {
                    RelativeLayout r = (RelativeLayout)tripContainers.get(i);
                    r.setBackgroundResource(R.drawable.trip_list_item_background);
                }

                for(int i=0; i<editTripButtons.size();i++)
                {
                    Button b = (Button)editTripButtons.get(i);
                    b.setVisibility(View.INVISIBLE);
                }
                selectedCount=0;
                MyTripsListFragment.numberOfSelectedTrips.setText(selectedCount+" Selected");
                selectedTripsList.clear();

            }
        });

        if(tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()).gettrip_thumbnail_stamp()!="NO_PHOTO")
        {
            String user_id = tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()).gettripuser_id();
            String trip_id = tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()).gettrip_id();
            Glide.with(userTripsListViewHolder.context)
                    .using(new FirebaseImageLoader())
                    .load(firebaseDBHandler.storageReference.child(user_id).child(trip_id).child("Thumbnails").
                            child(tripsInfoList.get(userTripsListViewHolder.getAdapterPosition()).gettrip_thumbnail_stamp()))
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.d("RESOURCE", userTripsListViewHolder.getAdapterPosition()+" first resource "+isFirstResource);

                            userTripsListViewHolder.trip_info_thumbnail.setBackgroundResource(R.drawable.no_photo);


                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {


                            return false;
                        }
                    })
                    .into(userTripsListViewHolder.trip_info_thumbnail);
            userTripsListViewHolder.trip_info_thumbnail.setBackgroundResource(R.color.transparent_black_hex_10);
            userTripsListViewHolder.trip_edit_thumbnail.setVisibility(View.INVISIBLE);

        }


    }







    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.d("RV", "ATTACHED");

        MainActivity.progressDialog.dismiss();

    }



    public void buildList(TripInfo trip, RelativeLayout tripContaier, Button editThumbnail)
    {


        if(selectedTripsList.contains(trip))
        {
            editThumbnail.setVisibility(View.INVISIBLE);
            editTripButtons.remove(editThumbnail);
            Log.d("LIST_BUILDING", "REMOVING "+trip);
            selectedTripsList.remove(trip);
            tripContainers.remove(tripContaier);
            tripContaier.setBackgroundResource(R.drawable.trip_list_item_background);
            selectedCount--;
            MyTripsListFragment.userTripsRV.invalidate();
        }
        else {

            editTripButtons.add(editThumbnail);
            editThumbnail.setVisibility(View.VISIBLE);
            selectedCount++;
            Log.d("LIST_BUILDING", "ADDING "+trip);
            selectedTripsList.add(trip);
            tripContaier.setBackgroundResource(R.color.transparent_white_hex_5);
            tripContainers.add(tripContaier);
            MyTripsListFragment.userTripsRV.invalidate();

        }

        Log.d("TRIPS_SELECETED", ""+selectedTripsList.size());


    }



}
package mebeidcreations.apps.livegosee;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Mazen Ebeid on 28/10/2017.
 */

public class ImageUploadService extends IntentService {

    public static final String NOTIFICATION = "com.vogella.android.service.receiver";

    public ImageUploadService() {
        super("ImageUploadService");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent!=null)
        {
            String user_id = intent.getStringExtra("user_id");
            String trip_id= intent.getStringExtra("trip_id");
            String stamp = intent.getStringExtra("stamp");
            String old_stamp = intent.getStringExtra("old_stamp");
            Uri uri = Uri.parse(intent.getStringExtra("uri"));
            Log.d("INTENT_DATA", user_id+" "+trip_id+" "+uri);

            FirebaseDBHandler firebaseDBHandler =  new FirebaseDBHandler(getApplicationContext());
            FirebaseDBHandler.storageReference.child(user_id).child(trip_id).child("Thumbnails").child(old_stamp).delete();
            firebaseDBHandler.AddPhotoToDB(uri,user_id,trip_id,"Thumbnails",stamp);
            firebaseDBHandler.AddThumbnailStampToDB(user_id,trip_id,stamp);


        }
        else
        {
            Log.d("INTENT DATA", "NULL");
        }

        publishResults();

    }

    private void publishResults() {


        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("STATUS", "SERVICE IS GOOD");//  Intent Extras here will go to the calling activities who register as recievers
        sendBroadcast(intent);
    }
}

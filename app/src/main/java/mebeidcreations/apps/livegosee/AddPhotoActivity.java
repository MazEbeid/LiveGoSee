package mebeidcreations.apps.livegosee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;

public class AddPhotoActivity extends AppCompatActivity {


    static boolean imageUploaded = false;
    static int ADD_FROM_GALLERY;
    static String CALLER = "";
    ImageView imageContainer;
    FirebaseDBHandler firebaseDBHandler;
    TripInfo selectedTrip;

    BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        firebaseDBHandler = new FirebaseDBHandler(getApplicationContext());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String string = bundle.getString("STATUS");

                    Log.d("RECEIVER", string);
                    MainActivity.tripInfoQ.clear();
                    firebaseDBHandler.GetUserTripsList(selectedTrip.gettripuser_id());
                    MainActivity.progressDialog.dismiss();
                    finish();
                }


            }
        };

        Bundle bundle = getIntent().getExtras();
        if (bundle.get("selected_trip") != null)
        {
            Log.d("BUNDLE", "EXISTS");
            selectedTrip  = (TripInfo) bundle.getSerializable("selected_trip");
            Log.d("TRIP_IN_PHOTO_ACTIVITY", "TRIP_ID: " + selectedTrip.gettrip_id());

        } else {
            Log.d("BUNDLE", "NULL");
        }

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , ADD_FROM_GALLERY);

        MainActivity.progressDialog.setMessage("Updating image..");
        MainActivity.progressDialog.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                ImageUploadService.NOTIFICATION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(requestCode==ADD_FROM_GALLERY&&resultCode==RESULT_OK)
        {
           Calendar cal1 = Calendar.getInstance();
            Uri uri= imageReturnedIntent.getData();
            try{
                getImageSize(uri);
            }
            catch (Exception e)
            {
                Log.d("IMAGE_EXCEPTION", e.getMessage());
            }
            String stamp = cal1.get(Calendar.DAY_OF_MONTH)+""+cal1.get(Calendar.MONTH)+""+cal1.get(Calendar.YEAR)+
                    ""+cal1.get(Calendar.HOUR)+""+cal1.get(Calendar.MINUTE)+""+cal1.get(Calendar.SECOND);
  //          firebaseDBHandler.AddPhotoToDB(uri,selectedTrip.gettripuser_id(),selectedTrip.gettrip_id(), stamp);
  //          firebaseDBHandler.AddThumbnailStampToDB(selectedTrip.gettripuser_id(),selectedTrip.gettrip_id(),stamp);
 //           MainActivity.tripInfoQ.clear();
  //          firebaseDBHandler.GetUserTripsList(selectedTrip.gettripuser_id());
  //          progressDialog.dismiss();
   //         finish();
            Intent uploadImageIntent = new Intent(this,ImageUploadService.class);
            uploadImageIntent.putExtra("uri",uri.toString());
            uploadImageIntent.putExtra("user_id", selectedTrip.gettripuser_id());
            uploadImageIntent.putExtra("trip_id",selectedTrip.gettrip_id());
            uploadImageIntent.putExtra("old_stamp",selectedTrip.gettrip_thumbnail_stamp());
            uploadImageIntent.putExtra("stamp", stamp);
            startService(uploadImageIntent);
          //  finish();
        }
        else
        {
            MainActivity.progressDialog.dismiss();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    public   void AddFragment(Fragment fragment, int container) {

        FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commitNow();

    }

    private void getImageSize(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length;


        Log.d("IMAGE_SIZE",Long.toString(lengthbmp));
    }


}

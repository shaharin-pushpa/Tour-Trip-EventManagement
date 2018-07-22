package com.example.kowshick.travelmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class TakePhotoActivity extends AppCompatActivity {
    private ImageView ivImage;
    private EditText caption;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference momentRef;
    DatabaseReference eventref;
    private Uri photoURI;
    private FirebaseStorage mStorage;
    private StorageReference momentref;
    private String eventid;
    private String userid;
    private String photoPath;
    private Uri picUri;
    public Intent intent;
    private Event ev=new Event();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        ivImage = findViewById(R.id.img);
        caption= findViewById(R.id.caption);
        mStorage=FirebaseStorage.getInstance();
        momentref=mStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        eventref = firebaseDatabase.getReference("Events");
        //show = findViewById(R.id.showmessageCamra);
        Intent in = getIntent();
        ev = (Event) in.getSerializableExtra("evid");
        userid = in.getStringExtra("usid");
        eventid=ev.getId();
        momentRef = eventref.child("Moments");
        //selectImage();
    }

    public void takePhoto(View view) {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            File file=null;
            try {
                file=createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file!=null){
                photoPath=file.getAbsolutePath();
                 photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(intent,111);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==111 && resultCode== RESULT_OK){
            /*Bundle extras=data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            ivImage.setImageBitmap(bitmap);*/
            setPic();
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName="JPEG_"+timeStamp;
        File storageDir= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile=File.createTempFile(imageName,".jpg",storageDir);
        return imageFile;

    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = ivImage.getWidth();
        int targetH = ivImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        ivImage.setImageBitmap(bitmap);
    }

    private boolean uploadFile(){
        if (photoURI!=null){
            final ProgressDialog progressDialog= new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference ref=momentref.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(photoURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            picUri=taskSnapshot.getDownloadUrl();
                            Toast.makeText(TakePhotoActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            String cap=caption.getText().toString();
                                String key = momentRef.push().getKey();
                                String pic=picUri.toString();
                                Moments mo = new Moments(key, pic, cap);
                                momentRef.child(userid).child(eventid).child(key).setValue(mo);



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(TakePhotoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double proress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)proress+"%");

                        }
                    });
            return true;

        }
        return false;

    }


    public void savePhot(View view) {
        uploadFile();

        //startActivity(new Intent(TakePhotoActivity.this,DetailsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.back_detais:
                Intent evntIntent=new Intent(TakePhotoActivity.this,DetailsActivity.class);
                evntIntent.putExtra("msg",ev);
                startActivity(evntIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

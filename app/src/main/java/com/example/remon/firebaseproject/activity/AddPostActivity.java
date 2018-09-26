package com.example.remon.firebaseproject.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.remon.firebaseproject.R;
import com.example.remon.firebaseproject.model.ImageUploadInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class AddPostActivity extends AppCompatActivity {

    //Vars
    EditText mTitleEt , mDesecEt;
    ImageView mPostsIv;
    Button mUploadBtn;
    //Folder Path for path FireBase Storage
    String mStoragePath = "All_Image_Upload/";

    //Root DataBase name firebase database
    String mDataBasePath = "Data";

    // Creating URL;
    Uri mFilePath;

    //Creating DataReference and DataBase Reference
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    ProgressDialog mProgressDialog;

    int IMAGE_REQUEST_CODE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        ActionBar actionBar = getSupportActionBar ( );
        actionBar.setTitle ( "Add New Post" );

        setContentView ( R.layout.activity_add_post );
        mTitleEt = findViewById ( R.id.pTitleEt );
        mDesecEt = findViewById ( R.id.pDerscEt );
        mPostsIv = findViewById ( R.id.pImageIv );
        mUploadBtn = findViewById ( R.id.pUploadBtn );


        mPostsIv.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent (  );
                //Setting intent type as image to select image from phone Storage

                intent.setType ( "image/*" );
                intent.setAction ( Intent.ACTION_GET_CONTENT );
                startActivityForResult ( Intent.createChooser ( intent, "Selected Image" ),IMAGE_REQUEST_CODE );
            }
        } );

        // Button click listener
        mUploadBtn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                uploadDataToFireBase();
            }
        } );
        mStorageReference = FirebaseStorage.getInstance ().getReference ();
        mDatabaseReference = FirebaseDatabase.getInstance ().getReference (mDataBasePath);
        mProgressDialog = new ProgressDialog ( AddPostActivity.this );
    }

    private void uploadDataToFireBase() {
        if ( mFilePath != null ){
            mProgressDialog.setTitle ( "Uploading...." );
            mProgressDialog.show ();

            StorageReference storageReference2nd = mStorageReference.child ( mStoragePath + System.currentTimeMillis ()
                    + getFileExtension (mFilePath) );

            storageReference2nd.putFile ( mFilePath )
                    .addOnSuccessListener ( new OnSuccessListener <UploadTask.TaskSnapshot> ( ) {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String mPostTitle = mTitleEt.getText ( ).toString ( ).trim ( );
                            String mDescri = mDesecEt.getText ( ).toString ( ).trim ( );
                            mProgressDialog.hide ( );
                            Toast.makeText ( AddPostActivity.this , "Uploaded Successful..." , Toast.LENGTH_SHORT ).show ( );
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo ( mPostTitle , mDescri , taskSnapshot.getDownloadUrl ( ).toString ( ) ,
                                    mPostTitle.toLowerCase ( ) );
                            String imageId = mDatabaseReference.push ().getKey ();
                            mDatabaseReference.child ( imageId ).setValue ( imageUploadInfo );
                        }
                    } )
                    .addOnFailureListener ( new OnFailureListener ( ) {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss ();
                            Toast.makeText ( AddPostActivity.this , e.getMessage () , Toast.LENGTH_SHORT ).show ( );
                        }
                    } )
                    .addOnProgressListener ( new OnProgressListener <UploadTask.TaskSnapshot> ( ) {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.setTitle ( "Uploading..." );
                        }
                    } );

        }else {
            Toast.makeText ( this , "Please select image or add image name" , Toast.LENGTH_SHORT ).show ( );
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver =getContentResolver ();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton ();

        return mimeTypeMap.getExtensionFromMimeType ( contentResolver.getType ( uri ) );
    }// method to getFileExtension from file path uri

    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data) {
        super.onActivityResult ( requestCode , resultCode , data );
        if ( requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && data.getData ( ) != null ) {
            mFilePath = data.getData ( );
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap ( getContentResolver ( ) , mFilePath );
                mPostsIv.setImageBitmap ( bitmap );
            } catch (Exception e) {
                Toast.makeText ( this , e.getMessage () , Toast.LENGTH_SHORT ).show ( );
            }
        }
    }
}

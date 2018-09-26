package com.example.remon.firebaseproject.activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remon.firebaseproject.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {

    // vars
    Bitmap bitmap;
    TextView mTextViewTitle ,mDescriptionView;
    ImageView mimageView;
    Button mSaveBtn , mShareBtn , mWallBtn;

    private static final int WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_post_details );

        ActionBar actionBar = getSupportActionBar ( );
        actionBar.setTitle ( "Post Details" );
        actionBar.setDisplayShowHomeEnabled ( true );
        actionBar.setDisplayHomeAsUpEnabled ( true );

        mTextViewTitle = findViewById ( R.id.title );
        mDescriptionView = findViewById ( R.id.descriptionView );
        mimageView = findViewById ( R.id.imageView );
        mSaveBtn = findViewById ( R.id.saveBtn );
        mShareBtn = findViewById ( R.id.shareBtn );
        mWallBtn = findViewById ( R.id.wallBtn );


        // Get Data
        String image= getIntent ( ).getStringExtra ( "image" );
        String title = getIntent ( ).getStringExtra ( "title" );
        String desc = getIntent ( ).getStringExtra ( "description" );

        // Set Data
        mTextViewTitle.setText ( title );
        mDescriptionView.setText ( desc );
        Picasso.get ().load ( image ).into ( mimageView );


        // Save on click listener
        mSaveBtn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                    if ( checkSelfPermission ( Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                            PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        // show popup to grant permission
                        requestPermissions ( permission ,WRITE_EXTERNAL_STORAGE );
                    }else {
                        saveImage();
                    }
                }else {
                    saveImage ();
                }
            }
        } );
        // Share On click Listener
        mShareBtn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        } );
        //Wall On Click Listener
        mWallBtn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                setImgWallpaper();
            }
        } );
    }

    private void setImgWallpaper() {
        //get image from imageView
        bitmap = (( BitmapDrawable )mimageView.getDrawable ()).getBitmap();
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance (getApplicationContext ());
        try {
            myWallpaperManager.setBitmap ( bitmap );
            Toast.makeText ( this , "Wallpaper set...." , Toast.LENGTH_SHORT ).show ( );
        }catch (Exception e){
            Toast.makeText ( this , e.getMessage () , Toast.LENGTH_SHORT ).show ( );
        }
    }

    private void shareImage() {
        try {

            //get image from imageView
            bitmap = (( BitmapDrawable)mimageView.getDrawable ()).getBitmap();

            String s = mTextViewTitle.getText ().toString () + "\n" +
                    mDescriptionView.getText ().toString ();
            File file = new File ( getExternalCacheDir (),"sample.png");
            FileOutputStream fOut = new FileOutputStream ( file );
            bitmap.compress ( Bitmap.CompressFormat.PNG,100,fOut );
            fOut.flush ();
            fOut.close ();
            file.setReadable ( true,false );

            //intent to share image
            Intent intent = new Intent ( Intent.ACTION_SEND );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
            intent.putExtra ( Intent.EXTRA_TEXT , s );
            intent.putExtra ( Intent.EXTRA_STREAM, Uri.fromFile ( file ) );
            intent.setType ( "image/png" );
            startActivity ( Intent.createChooser ( intent,"Share via" ) );

        }catch (Exception e){
            Toast.makeText ( this , e.getMessage () , Toast.LENGTH_SHORT ).show ( );
        }
    }

    private void saveImage() {
        //get image from imageView
        bitmap = (( BitmapDrawable)mimageView.getDrawable ()).getBitmap();
        String timeStamp = new SimpleDateFormat ( "yyyyMMdd_HHmmss",
                Locale.getDefault ()).format ( System.currentTimeMillis () );

        File path = Environment.getExternalStorageDirectory ();
        File dir = new File ( path + "/Firebase/" );
        dir.mkdirs ();

        String imageName = timeStamp +".PNG";
        File file = new File ( path + imageName );
        OutputStream out;

        try {
            out = new FileOutputStream ( file );
            bitmap.compress ( Bitmap.CompressFormat.PNG,100,out );
            out.flush ();
            out.close ();
            Toast.makeText ( this , imageName+"saved to"+dir , Toast.LENGTH_SHORT ).show ( );

        }catch (Exception e){
            Toast.makeText ( this , e.getMessage () , Toast.LENGTH_SHORT ).show ( );

        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed ( );
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode , @NonNull String[] permissions , @NonNull int[] grantResults) {
       switch (requestCode){
           case WRITE_EXTERNAL_STORAGE:
               if ( grantResults.length > 0 && grantResults[0] ==
                       PackageManager.PERMISSION_GRANTED){
                   saveImage ();
               }else {
                   Toast.makeText ( this , "Enable Perission to save image" , Toast.LENGTH_SHORT ).show ( );
               }
       }
    }
}

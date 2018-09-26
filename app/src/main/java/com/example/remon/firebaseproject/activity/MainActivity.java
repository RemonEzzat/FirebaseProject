package com.example.remon.firebaseproject.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.remon.firebaseproject.R;
import com.example.remon.firebaseproject.adapter.ViewHolder;
import com.example.remon.firebaseproject.model.Model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    // Vars
    LinearLayoutManager mLayoutManger; // for sorting
    SharedPreferences mSharedPref; // For Saving Setting

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        ActionBar actionBar = getSupportActionBar ( );
        actionBar.setTitle ( "Posts List" );
        mSharedPref = getSharedPreferences ( "SortSettings" , MODE_PRIVATE );
        String mSorting = mSharedPref.getString ( "Sort" , "newest" ); // where is no settings selected


        if ( mSorting.equals ( "newest" ) ) {
            mLayoutManger = new LinearLayoutManager ( this );
            mLayoutManger.setReverseLayout ( true );
            mLayoutManger.setStackFromEnd ( true );
        } else if ( mSorting.equals ( "oldest" ) ) {
            mLayoutManger = new LinearLayoutManager ( this );
            mLayoutManger.setReverseLayout ( false );
            mLayoutManger.setStackFromEnd ( false );
        }

        initRecyclerView ( );
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById ( R.id.recyclerview );
        mRecyclerView.setHasFixedSize ( true );

        mRecyclerView.setLayoutManager ( mLayoutManger );

        // send Query to FireBaseData
        mFirebaseDatabase = FirebaseDatabase.getInstance ( );
        mRef = mFirebaseDatabase.getReference ( "Data" );
    }


    private void firebaseSearch(String searchText) {

        // convert string entered in Search View to lower

        String query = searchText.toLowerCase ( );

        Query firebaseQuery = mRef.orderByChild ( "search" ).startAt ( query ).endAt ( query
                + "\uf0ff" );

        FirebaseRecyclerAdapter <Model, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <Model, ViewHolder> (
                        Model.class ,
                        R.layout.row ,
                        ViewHolder.class ,
                        firebaseQuery
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder , Model model , int position) {
                        viewHolder.setDetails ( getApplicationContext ( ) , model.getTitle ( ) ,
                                model.getDescription ( ) ,
                                model.getImage ( )
                        );

                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder ( parent , viewType );

                        viewHolder.setOnClickListener ( new ViewHolder.ClickListener ( ) {
                            @Override
                            public void onItemClick(View view , int position) {


                                // get data from FireBase
                                String mTitlev = getItem ( position ).getTitle ( );
                                String mDes = getItem ( position ).getDescription ( );
                                String mImage = getItem ( position ).getImage ( );
                                // Pass Data to new Activity
                                Intent intent = new Intent ( view.getContext ( ) , PostDetailsActivity.class );
                                intent.putExtra ( "title" , mTitlev );
                                intent.putExtra ( "description" , mDes );
                                intent.putExtra ( "image" , mImage );
                                startActivity ( intent );

                            }

                            @Override
                            public void onItemLongClick(View view , int position) {
                                // TODO do your implementation on long click
                            }
                        } );

                        return viewHolder;
                    }
                };
        mRecyclerView.setAdapter ( firebaseRecyclerAdapter );
    }// search data

    @Override
    protected void onStart() {
        super.onStart ( );
        FirebaseRecyclerAdapter <Model, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <Model, ViewHolder> ( Model.class , R.layout.row , ViewHolder.class , mRef ) {

                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder , Model model , int position) {
                        viewHolder.setDetails ( getApplicationContext ( ) , model.getTitle ( ) ,
                                model.getDescription ( ) ,
                                model.getImage ( )
                        );
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder ( parent , viewType );

                        viewHolder.setOnClickListener ( new ViewHolder.ClickListener ( ) {
                            @Override
                            public void onItemClick(View view , int position) {
                                // get data from firebase
                                String mTitlev = getItem ( position ).getTitle ( );
                                String mDes = getItem ( position ).getDescription ( );
                                String mImage = getItem ( position ).getImage ( );
                                // Pass Data to new Activity
                                Intent intent = new Intent ( view.getContext ( ) , PostDetailsActivity.class );
                                intent.putExtra ( "title" , mTitlev );
                                intent.putExtra ( "description" , mDes );
                                intent.putExtra ( "image" , mImage );
                                startActivity ( intent );

                            }

                            @Override
                            public void onItemLongClick(View view , int position) {
                                // TODO do your implementation on long click
                            }
                        } );

                        return viewHolder;
                    }
                };
        mRecyclerView.setAdapter ( firebaseRecyclerAdapter );

    }// Load Data into RecyclerView OnStart

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater ( ).inflate ( R.menu.menu , menu );
        MenuItem item = menu.findItem ( R.id.action_search );
        SearchView searchView = ( SearchView ) MenuItemCompat.getActionView ( item );
        searchView.setOnQueryTextListener ( new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter you type
                firebaseSearch ( newText );
                return false;
            }
        } );
        return super.onCreateOptionsMenu ( menu );
    }//inflate the menu , this add item to the action bar if it present

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId ( );

        // handel other action bar item click here
        if ( id == R.id.action_Sort ) {
            // display alert dialog to choose Sorting
            showSortDialog ( );
            return true;
        }
        if ( id == R.id.action_add ) {

            startActivity ( new Intent ( MainActivity.this , AddPostActivity.class ) );
            return true;
        }
        return super.onOptionsItemSelected ( item );
    }

    private void showSortDialog() {
        String[] sortOption = {"Newest" , "Oldest"};

        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setTitle ( "Sort by" )
                .setIcon ( R.drawable.ic_action_sort )
                .setItems ( sortOption , new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(DialogInterface dialog , int which) {
                        // 0 means newest 1 means oldest
                        if ( which == 0 ) {
                            // sort by Newest
                            SharedPreferences.Editor editor = mSharedPref.edit ( );
                            editor.putString ( "Sort" , "newest" );
                            editor.apply ( );
                            recreate ( );
                        } else {
                            // Sort By Oldest
                            SharedPreferences.Editor editor = mSharedPref.edit ( );
                            editor.putString ( "Sort" , "oldest" );
                            editor.apply ( );
                            recreate ( );
                        }
                    }
                } );
        builder.show ( );
    }
}
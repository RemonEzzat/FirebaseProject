package com.example.remon.firebaseproject.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remon.firebaseproject.R;
import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    // Vars
    View mView;

    public ViewHolder(@NonNull View itemView) {
        super ( itemView );

        mView = itemView;
        // Item Click
        itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick ( v , getAdapterPosition ( ) );
            }
        } );
        // Item Long Click
        itemView.setOnLongClickListener ( new View.OnLongClickListener ( ) {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick ( v , getAdapterPosition ( ) );
                return true;
            }
        } );
    }


    public void setDetails(Context context , String title , String description , String image) {

        TextView titleView = mView.findViewById ( R.id.mTitle );
        TextView descriptionView = mView.findViewById ( R.id.mDescription );
        ImageView imageView = mView.findViewById ( R.id.mImageView );

        titleView.setText ( title );
        descriptionView.setText ( description );
        Picasso.get ( ).load ( image ).into ( imageView );


    }// Set Details in RecyclerView Row

    // vars
    private ViewHolder.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view , int position);

        void onItemLongClick(View view , int position);
    } //interface to send callback

    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;

    }
}

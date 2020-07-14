package com.example.grybos.firechat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Item> mItems;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CircularImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.chatroomPicture);
            mTextView1 = itemView.findViewById(R.id.chat_name);
            mTextView2 = itemView.findViewById(R.id.last_message);

        }
    }

    public RecyclerAdapter(ArrayList<Item> items){

        mItems = items;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Item currentItem = mItems.get(position);

        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getChat_name());
        holder.mTextView2.setText(currentItem.getLast_message());

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}

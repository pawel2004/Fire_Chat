package com.example.grybos.firechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Item> mItems;
    private Context mContext;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mListener2;

    public interface OnItemClickListener{

        void onItemClick(int position);

    }

    public interface OnItemLongClickListener{

        void onItemLongClick(int position);

    }

    public void setOnitemClickListener(OnItemClickListener listener){

        mListener = listener;

    }

    public void setOnitemLongClickListener(OnItemLongClickListener listener){

        mListener2 = listener;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView1;
        public CircularImageView mCircularImageView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener, final OnItemLongClickListener listener2) {
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.chat_name);
            mCircularImageView = itemView.findViewById(R.id.chatroomPicture);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null){

                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){

                            listener.onItemClick(position);

                        }

                    }

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (listener2 != null){

                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){

                            listener2.onItemLongClick(position);

                        }

                    }

                    return true;
                }
            });

        }
    }

    public RecyclerAdapter(Context context, ArrayList<Item> items){

        mContext = context;
        mItems = items;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener, mListener2);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Item currentItem = mItems.get(position);

        holder.mTextView1.setText(currentItem.getChat_name());
        Glide.with(mContext).load(currentItem.getImage_resource()).into(holder.mCircularImageView);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}

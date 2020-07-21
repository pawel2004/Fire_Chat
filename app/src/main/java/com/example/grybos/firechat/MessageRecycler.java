package com.example.grybos.firechat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageRecycler extends RecyclerView.Adapter<MessageRecycler.ViewHolder> {

    private ArrayList<Message> mMessages;
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
        public TextView mTextView2;
        public TextView mTextView3;
        public CircularImageView mCircularImageView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener, final OnItemLongClickListener listener2) {
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.user_name);
            mTextView2 = itemView.findViewById(R.id.date);
            mTextView3 = itemView.findViewById(R.id.message);
            mCircularImageView = itemView.findViewById(R.id.userPicture);

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

    public MessageRecycler(Context context, ArrayList<Message> messages){

        mContext = context;
        mMessages = messages;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener, mListener2);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Message currentMessage = mMessages.get(position);

        holder.mTextView1.setText(currentMessage.getUserName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        String date = dateFormat.format(currentMessage.getMessageDate());

        holder.mTextView2.setText(date);
        holder.mTextView3.setText(currentMessage.getMessageText());
        Glide.with(mContext).load(currentMessage.getUserImage()).into(holder.mCircularImageView);

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}

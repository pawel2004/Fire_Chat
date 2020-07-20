package com.example.grybos.firechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListViewAdapter extends ArrayAdapter {

    private ArrayList<User> users;
    private Context context;
    private int resource;

    public ListViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);

        this.users = objects;
        this.context = context;
        this.resource = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(resource, null);

        CircularImageView profilePicture = convertView.findViewById(R.id.profilePicture);
        Glide.with(context).load(users.get(position).getImageResource()).into(profilePicture);
        TextView userName = convertView.findViewById(R.id.user_name);
        userName.setText(users.get(position).getDisplayName());
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);

        return convertView;
    }
}

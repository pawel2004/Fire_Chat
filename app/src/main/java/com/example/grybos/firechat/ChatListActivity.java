package com.example.grybos.firechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.InputStream;

public class ChatListActivity extends AppCompatActivity {

    //zmienne
    private FirebaseAuth mAuth;

    //widgety
    private CircularImageView profile_picture;
    private ImageView log_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        profile_picture = findViewById(R.id.profile_picture);
        log_out = findViewById(R.id.log_out);

        loadUserInformation();

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChatListActivity.this, ProfileCreateActivity.class);
                intent.putExtra("key", 1);
                startActivity(intent);

            }
        });

    }

    private void loadUserInformation(){

        FirebaseUser user = mAuth.getCurrentUser();

        if (user.getPhotoUrl() != null){

            Glide.with(this).load(user.getPhotoUrl().toString()).into(profile_picture);

        }
        else {

            startActivity(new Intent(ChatListActivity.this, ProfileCreateActivity.class));

        }

        if (user.getDisplayName() != null){

        }
        else {

            Intent intent = new Intent(ChatListActivity.this, ProfileCreateActivity.class); //Do nowego activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Czyści poprzednie activity
            startActivity(intent);

        }

    }
}

package com.example.grybos.firechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    //zmienne
    private FirebaseAuth mAuth;
    private ArrayList<Item> items = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //widgety
    private CircularImageView profile_picture;
    private ImageView log_out;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        profile_picture = findViewById(R.id.profile_picture);
        log_out = findViewById(R.id.log_out);

        loadUserInformation();

        items.add(new Item(R.drawable.ic_icon, "Chat1", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat2", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat3", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat4", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat5", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat6", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat7", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat8", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat9", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat10", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat11", "Paweł: Cześć!"));
        items.add(new Item(R.drawable.ic_icon, "Chat12", "Paweł: Cześć!"));

        mRecyclerView = findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerAdapter(items);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChatListActivity.this, ProfileCreateActivity.class);
                intent.putExtra("key", 1);
                startActivity(intent);

            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut(); //Wyloguj
                Intent intent = new Intent(ChatListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

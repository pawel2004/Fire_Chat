package com.example.grybos.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity implements AddingChatBottomSheetDialog.BottomSheetListener {

    //zmienne
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ArrayList<Item> items = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference chatRoomList;

    //widgety
    private CircularImageView profile_picture;
    private ImageView log_out;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        chatRoomList = database.getReference();

        profile_picture = findViewById(R.id.profile_picture);
        log_out = findViewById(R.id.log_out);
        fab = findViewById(R.id.fab);

        loadUserInformation();

        generateRecyclerView();

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddingChatBottomSheetDialog bottomSheetDialog = new AddingChatBottomSheetDialog();
                bottomSheetDialog.show(getSupportFragmentManager(), "addingChat");

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

    private void generateRecyclerView(){

        chatRoomList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.w("xxx", "Failed to read value.", error.toException());

            }
        });

        mRecyclerView = findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerAdapter(items);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onButtonClicked(String text) {

        chatRoomList.push().setValue(text);

        generateRecyclerView();

    }

}

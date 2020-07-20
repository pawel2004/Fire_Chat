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
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference chatRoomList;

    //widgety
    private CircularImageView profile_picture;
    private ImageView log_out;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;

    //static
    public static final String RoomId = "RoomId";
    public static final String RoomImage = "RoomImage";
    public static final String RoomName = "RoomName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        chatRoomList = database.getReference("Rooms");

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

    @Override
    protected void onStart() {
        super.onStart();

        chatRoomList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                items.clear();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()){

                    Item item = itemSnapshot.getValue(Item.class);

                    items.add(item);

                }

                generateRecyclerView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        mRecyclerView = findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerAdapter(this, items);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnitemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Item item = items.get(position);

                Intent intent = new Intent(ChatListActivity.this, ChatRoomActivity.class);
                intent.putExtra(RoomId, item.getId());
                intent.putExtra(RoomImage, item.getImage_resource());
                intent.putExtra(RoomName, item.getChat_name());
                startActivity(intent);

            }
        });

    }

    @Override
    public void onButtonClicked(String text) {

        String id = chatRoomList.push().getKey();
        DatabaseReference tempRef = chatRoomList.child(id);
        tempRef.setValue(new Item(id, "https://firebasestorage.googleapis.com/v0/b/fire-chat-1ce3d.appspot.com/o/ic_icon.png?alt=media&token=ff5e63a8-5f3a-4902-8c3a-feb3bf87517f", text));

    }

}

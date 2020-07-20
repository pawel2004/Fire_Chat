package com.example.grybos.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class ChatListActivity extends AppCompatActivity implements AddingChatBottomSheetDialog.BottomSheetListener, EditingChatBottomSheetDialog.EditBottomSheetListener {

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
    private ProgressBar progressBar;

    //static
    public static final String RoomId = "RoomId";
    public static final String RoomImage = "RoomImage";
    public static final String RoomName = "RoomName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        isNetworkConnected();

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        chatRoomList = database.getReference("Rooms");

        profile_picture = findViewById(R.id.profile_picture);
        log_out = findViewById(R.id.log_out);
        fab = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progress_bar);

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

        progressBar.setVisibility(View.VISIBLE);

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

        mAdapter.setOnitemLongClickListener(new RecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {

                EditingChatBottomSheetDialog bottomSheet = new EditingChatBottomSheetDialog(items.get(position).getChat_name(), items.get(position).getId(), items.get(position).getImage_resource());
                bottomSheet.show(getSupportFragmentManager(), "edycja");

            }
        });

        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onButtonClicked(String text) {

        String id = chatRoomList.push().getKey();
        DatabaseReference tempRef = chatRoomList.child(id);
        tempRef.setValue(new Item(id, "https://firebasestorage.googleapis.com/v0/b/fire-chat-1ce3d.appspot.com/o/ic_icon.png?alt=media&token=ff5e63a8-5f3a-4902-8c3a-feb3bf87517f", text));

    }

    @Override
    public void onViewClicked(String text, final String id, final String image_resource) {

        final DatabaseReference drRef = chatRoomList.child(id);

        if (text.equals("0")){

            final AlertDialog.Builder alert = new AlertDialog.Builder(ChatListActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.info_alert, null);

            final TextView textViewTitle = dialogView.findViewById(R.id.title);
            final TextView textViewMessage = dialogView.findViewById(R.id.message);
            final Button button_ok = dialogView.findViewById(R.id.button_ok);
            final Button button_anuluj = dialogView.findViewById(R.id.button_anuluj);

            textViewTitle.setText("Uwaga!");
            textViewMessage.setText("Czy na pewno chcesz usunąć ten chat?");

            alert.setView(dialogView);
            final AlertDialog alertDialog = alert.create();
            alertDialog.show();

            button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    drRef.removeValue();

                    alertDialog.dismiss();

                }
            });

            button_anuluj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();

                }
            });

        }else {

            final AlertDialog.Builder alert = new AlertDialog.Builder(ChatListActivity.this);

            LayoutInflater inflater = getLayoutInflater();

            final View dialogView = inflater.inflate(R.layout.edit_room_alert, null);
            alert.setView(dialogView);

            final EditText editTextName = dialogView.findViewById(R.id.name);
            final Button buttonOk = dialogView.findViewById(R.id.button_ok);
            final Button buttonAnuluj = dialogView.findViewById(R.id.button_anuluj);

            final AlertDialog alertDialog = alert.create();
            alertDialog.show();

            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String name = editTextName.getText().toString();

                    if (!name.isEmpty()){

                        Item item = new Item(id, image_resource, name);

                        drRef.setValue(item);

                        alertDialog.dismiss();

                    }else {

                        editTextName.setError("Musisz wpisać jakąś nazwę!");
                        editTextName.requestFocus();

                    }

                }
            });

            buttonAnuluj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();

                }
            });

        }

    }

    private void isNetworkConnected(){

        if (!Networking.checkConnection(this)){

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.info_alert, null);

            final TextView textViewTitle = dialogView.findViewById(R.id.title);
            final TextView textViewMessage = dialogView.findViewById(R.id.message);
            final Button button_ok = dialogView.findViewById(R.id.button_ok);
            final Button button_anuluj = dialogView.findViewById(R.id.button_anuluj);

            button_anuluj.setVisibility(View.GONE);

            textViewTitle.setText("Problem z Internetem!");
            textViewMessage.setText("Nie masz połączenia! Włącz Internet i spróbuj ponownie!");

            alert.setView(dialogView);
            final AlertDialog alertDialog = alert.create();
            alertDialog.show();

            button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();

                }
            });

        }

    }

}

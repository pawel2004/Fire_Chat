package com.example.grybos.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.transform.Templates;

import static com.example.grybos.firechat.ChatListActivity.RoomId;
import static com.example.grybos.firechat.ChatListActivity.RoomImage;
import static com.example.grybos.firechat.ChatListActivity.RoomName;
import static com.example.grybos.firechat.ChatListActivity.RoomPrivate;
import static com.example.grybos.firechat.ChatListActivity.RoomUsers;

public class ChatRoomActivity extends AppCompatActivity {

    //zmienne
    private String mRoomName;
    private String mRoomId;
    private String mImageResource;
    private Boolean mIsPrivate;
    private ArrayList<User> mUsers;
    private Uri uriChatImage;
    private String chatImageUrl;
    private DatabaseReference messagesList;
    private ArrayList<User> activeUsers;
    private FirebaseAuth auth;

    //widgety
    private ImageView back;
    private ImageView private_switch;
    private ImageView addUsers;
    private CircularImageView room_image;
    private TextView chat_name;
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private FloatingActionButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        isNetworkConnected();

        getSupportActionBar().hide();

        back = findViewById(R.id.back);
        private_switch = findViewById(R.id.private_switch);
        addUsers = findViewById(R.id.add_users);
        room_image = findViewById(R.id.chat_picture);
        chat_name = findViewById(R.id.room_name);
        recyclerView = findViewById(R.id.recycler);
        editTextMessage = findViewById(R.id.edit_text_message);
        send = findViewById(R.id.fab);
        auth = FirebaseAuth.getInstance();

        activeUsers = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            mRoomName = bundle.getString(RoomName);
            mRoomId = bundle.getString(RoomId);
            mImageResource = bundle.getString(RoomImage);
            mIsPrivate = bundle.getBoolean(RoomPrivate);
            mUsers = (ArrayList<User>) bundle.get(RoomUsers);

        }

        messagesList = FirebaseDatabase.getInstance().getReference("Messages").child(mRoomId);

        private_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference drRef = FirebaseDatabase.getInstance().getReference("Rooms").child(mRoomId);

                if (mIsPrivate){

                    mIsPrivate = false;

                    private_switch.setImageResource(R.drawable.ic_visibility_black_24dp);
                    addUsers.setVisibility(View.GONE);

                    mUsers = new ArrayList<>();

                    drRef.setValue(new Item(mRoomId, mImageResource, mRoomName, mIsPrivate, mUsers));

                }else {

                    mIsPrivate = true;

                    private_switch.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    addUsers.setVisibility(View.VISIBLE);

                    drRef.setValue(new Item(mRoomId, mImageResource, mRoomName, mIsPrivate, mUsers));

                }

            }
        });

        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");

                AlertDialog.Builder alert = new AlertDialog.Builder(ChatRoomActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_user_dialog, null);

                final ListView listView = dialogView.findViewById(R.id.list);

                users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        activeUsers.clear();

                        for (DataSnapshot usersnaphot : snapshot.getChildren()){

                            activeUsers.add(usersnaphot.getValue(User.class));

                        }

                        ListViewAdapter adapter = new ListViewAdapter(
                                ChatRoomActivity.this,
                                R.layout.listview_layout,
                                activeUsers
                        );

                        listView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final Button button_ok = dialogView.findViewById(R.id.button_ok);
                final Button button_anuluj = dialogView.findViewById(R.id.button_anuluj);

                alert.setView(dialogView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.show();

                button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }
                });

                button_anuluj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }
                });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        room_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImageChooser();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadRoomInfo();

        if (mIsPrivate){

            private_switch.setImageResource(R.drawable.ic_visibility_off_black_24dp);
            addUsers.setVisibility(View.VISIBLE);

        }else {

            addUsers.setVisibility(View.GONE);
            private_switch.setImageResource(R.drawable.ic_visibility_black_24dp);

        }

    }

    private void loadRoomInfo() {

        chat_name.setText(mRoomName);

        Glide.with(this).load(mImageResource).into(room_image);

    }

    private void showImageChooser(){ //Otwiera galerię i pobiera zdjęcie

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie chatu: "), 200);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null && data.getData() != null){

            uriChatImage = data.getData(); //Zdjęcie ląduje w tym obiekcie

            uploadImageToFirebaseStorage();

        }

    }

    private void uploadImageToFirebaseStorage() { //Ładowanie zdjęcia do magazynu danych

        final StorageReference chatImageRef = FirebaseStorage.getInstance().getReference("chatroompics/" + System.currentTimeMillis() + ".jpg"); //Tworzenie szablonu zdjęcia

        if (uriChatImage != null) {

            chatImageRef.putFile(uriChatImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // profileImageUrl taskSnapshot.getDownloadUrl().toString(); //this is depreciated

                            //this is the new way to do it
                            chatImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    chatImageUrl=task.getResult().toString();

                                    updateDatabase(chatImageUrl);

                                    Glide.with(ChatRoomActivity.this).load(chatImageUrl).into(room_image);

                                    Log.i("URL",chatImageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatRoomActivity.this, "aaa "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateDatabase(String chatImageUrl) {

        DatabaseReference drRef = FirebaseDatabase.getInstance().getReference("Rooms").child(mRoomId);

        Item item = new Item(mRoomId, chatImageUrl, mRoomName, mIsPrivate, mUsers);

        drRef.setValue(item);

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

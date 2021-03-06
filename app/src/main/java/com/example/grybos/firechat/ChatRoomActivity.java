package com.example.grybos.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.xml.transform.Templates;

import static com.example.grybos.firechat.ChatListActivity.RoomId;
import static com.example.grybos.firechat.ChatListActivity.RoomImage;
import static com.example.grybos.firechat.ChatListActivity.RoomName;
import static com.example.grybos.firechat.ChatListActivity.RoomPrivate;
import static com.example.grybos.firechat.ChatListActivity.RoomUsers;

public class ChatRoomActivity extends AppCompatActivity implements EditingBottomSheetDialog2.EditBottomSheetListener {

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
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Message> messages;
    private DatabaseReference users;

    //widgety
    private ImageView back;
    private ImageView private_switch;
    private ImageView addUsers;
    private CircularImageView room_image;
    private TextView chat_name;
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private FloatingActionButton send;
    private MessageRecycler mAdapter;
    private ProgressBar progressBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        isNetworkConnected();

        getSupportActionBar().hide();

        back = findViewById(R.id.back);
        room_image = findViewById(R.id.chat_picture);
        chat_name = findViewById(R.id.room_name);
        recyclerView = findViewById(R.id.recycler);
        editTextMessage = findViewById(R.id.edit_text_message);
        send = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progress_bar);
        auth = FirebaseAuth.getInstance();

        activeUsers = new ArrayList<>();
        messages = new ArrayList<>();

        users = FirebaseDatabase.getInstance().getReference("Users");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            mRoomName = bundle.getString(RoomName);
            mRoomId = bundle.getString(RoomId);
            mImageResource = bundle.getString(RoomImage);
            mIsPrivate = bundle.getBoolean(RoomPrivate);
            mUsers = (ArrayList<User>) bundle.get(RoomUsers);

        }

        messagesList = FirebaseDatabase.getInstance().getReference("Messages").child(mRoomId);

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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();

            }
        });

    }

    private void generateRecyclerView(){

        recyclerView = findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new MessageRecycler(this, messages);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnitemLongClickListener(new MessageRecycler.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {

                EditingBottomSheetDialog2 bottomSheet = new EditingBottomSheetDialog2(messages.get(position).getUserName(), messages.get(position).getId(), messages.get(position).getUserImage(), messages.get(position).getMessageDate(),  messages.get(position).getMessageText(), messages.get(position).getEmailAdress(), messages.get(position).getUserId());
                bottomSheet.show(getSupportFragmentManager(), "edycja");

            }
        });

        progressBar.setVisibility(View.GONE);

    }

    private void sendMessage() {

        final String text = editTextMessage.getText().toString();

        if (auth.getCurrentUser() != null){

            final String userName = auth.getCurrentUser().getDisplayName();

            final String userImage = auth.getCurrentUser().getPhotoUrl().toString();

            final String userEmail = auth.getCurrentUser().getEmail();

            final long date = new Date().getTime();

            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot userSnapshot : snapshot.getChildren()){

                        User user = userSnapshot.getValue(User.class);

                        if (user.getEmailAdress().equals(auth.getCurrentUser().getEmail())){

                            String userId = user.getId();

                            String id = messagesList.push().getKey();

                            messagesList.child(id).setValue(new Message(id, userName, date, userImage, text, userEmail, userId));

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        editTextMessage.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadRoomInfo();

        progressBar.setVisibility(View.VISIBLE);

        messagesList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messages.clear();

                for (DataSnapshot messageSnaphot : snapshot.getChildren()){

                    Message message = messageSnaphot.getValue(Message.class);

                    messages.add(message);

                }

                generateRecyclerView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    @Override
    public void onViewClicked(String text, final String id, final String userName, final String userImage, final long messageDate, String messageText, final String userEmail, final String userId) {

        final DatabaseReference drRef = messagesList.child(id);

        if (text.equals("0")){

            final AlertDialog.Builder alert = new AlertDialog.Builder(ChatRoomActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.info_alert, null);

            final TextView textViewTitle = dialogView.findViewById(R.id.title);
            final TextView textViewMessage = dialogView.findViewById(R.id.message);
            final Button button_ok = dialogView.findViewById(R.id.button_ok);
            final Button button_anuluj = dialogView.findViewById(R.id.button_anuluj);

            textViewTitle.setText("Uwaga!");
            textViewMessage.setText("Czy na pewno chcesz usunąć tę wiadomość?");

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

            final AlertDialog.Builder alert = new AlertDialog.Builder(ChatRoomActivity.this);

            LayoutInflater inflater = getLayoutInflater();

            final View dialogView = inflater.inflate(R.layout.edit_message_alert, null);
            alert.setView(dialogView);

            final EditText editTextName = dialogView.findViewById(R.id.name);
            final Button buttonOk = dialogView.findViewById(R.id.button_ok);
            final Button buttonAnuluj = dialogView.findViewById(R.id.button_anuluj);

            editTextName.setText(messageText);

            final AlertDialog alertDialog = alert.create();
            alertDialog.show();

            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String name = editTextName.getText().toString();

                    Message message = new Message(id, userName, messageDate, userImage, name, userEmail, userId);

                    drRef.setValue(message);

                    alertDialog.dismiss();

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
}

package com.example.grybos.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.UserPrincipalLookupService;

public class ProfileCreateActivity extends AppCompatActivity {

    //zmienne
    private Uri uriProfileImage; //Ta zmienna reprezentuje zdjęcie profilowe
    private String profileImageUrl; //Ścieżka do zdjęcia
    private FirebaseAuth mAuth; //Obiekt do aktualizacji danych użytkownika
    private DatabaseReference users;
    private Bundle bundle;
    private String userId;

    //widgety
    private CircularImageView profile_image;
    private EditText name;
    private Button save;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_create);

        isNetworkConnected();

        getSupportActionBar().hide();

        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        save = findViewById(R.id.button_save);
        progressBar = findViewById(R.id.progress_bar);

        users = FirebaseDatabase.getInstance().getReference("Users");

        bundle = getIntent().getExtras();

        mAuth = FirebaseAuth.getInstance(); //Inicjalizacja menadżera użytkowników

        profile_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                showImageChooser();

                return true;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserInfo();

            }
        });

        if (bundle != null){

            if (bundle.getInt("key") == 1){

                loadUserInformation();

            }

        }

    }

    private void loadUserInformation(){ //ładuje zdjęcie i nazwę

        FirebaseUser user = mAuth.getCurrentUser();

        Glide.with(this).load(user.getPhotoUrl().toString()).into(profile_image);

        name.setText(user.getDisplayName());

    }

    @Override
    protected void onStart() { //Przy starcie
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

    }

    private void saveUserInfo() {

        String displayname = name.getText().toString(); //Pobieram nazwę

        if (displayname.isEmpty()){

            name.setError("Name required");
            name.requestFocus();
            return;

        }

        final FirebaseUser user = mAuth.getCurrentUser(); //Tworzenie obiektu użytkownika

        if (user != null && profileImageUrl != null){

            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayname)
                    .setPhotoUri(Uri.parse(profileImageUrl)).build(); //Dodawanie informacji

            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() { //Metoda zapisująca dane na profilu
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        Toast.makeText(ProfileCreateActivity.this, "Profil zmieniony!", Toast.LENGTH_LONG).show();

                        if (bundle.getInt("key") == 1){

                            users.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot userSnapshot : snapshot.getChildren()){

                                        User usertmp = userSnapshot.getValue(User.class);

                                        if (usertmp.getEmailAdress().equals(mAuth.getCurrentUser().getEmail())){

                                            userId = usertmp.getId();

                                            users.child(userId).setValue(new User(userId, user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail()));

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else {

                            String id = users.push().getKey();
                            users.child(id).setValue(new User(id, user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail()));

                        }

                        Log.d("xxx", "0");

                        Intent intent = new Intent(ProfileCreateActivity.this, MainActivity.class); //Do nowego activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Czyści poprzednie activity
                        startActivity(intent);

                    }

                }
            });

        }
        else if (profileImageUrl == null){

            if (user.getPhotoUrl() != null){

                profileImageUrl = user.getPhotoUrl().toString();

                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayname)
                        .setPhotoUri(Uri.parse(profileImageUrl)).build(); //Dodawanie informacji

                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() { //Metoda zapisująca dane na profilu
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(ProfileCreateActivity.this, "Profil zmieniony!", Toast.LENGTH_LONG).show();

                            if (bundle.getInt("key") == 1){

                                users.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot userSnapshot : snapshot.getChildren()){

                                            User usertmp = userSnapshot.getValue(User.class);

                                            if (usertmp.getEmailAdress().equals(mAuth.getCurrentUser().getEmail())){

                                                userId = usertmp.getId();

                                                users.child(userId).setValue(new User(userId, user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail()));

                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            else {

                                String id = users.push().getKey();
                                users.child(id).setValue(new User(id, user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail()));

                            }

                            Log.d("xxx", "1");

                            Intent intent = new Intent(ProfileCreateActivity.this, MainActivity.class); //Do nowego activity
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Czyści poprzednie activity
                            startActivity(intent);

                        }

                    }
                });

            }
            else {

                profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/fire-chat-1ce3d.appspot.com/o/default_profile.jpg?alt=media&token=1c402dfa-18b2-40d6-8756-0639fe228616";

                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayname)
                        .setPhotoUri(Uri.parse(profileImageUrl)).build(); //Dodawanie informacji

                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() { //Metoda zapisująca dane na profilu
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(ProfileCreateActivity.this, "Profil zmieniony!", Toast.LENGTH_LONG).show();

                            if (bundle.getInt("key") == 1){

                                users.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot userSnapshot : snapshot.getChildren()){

                                            User usertmp = userSnapshot.getValue(User.class);

                                            if (usertmp.getEmailAdress().equals(mAuth.getCurrentUser().getEmail())){

                                                userId = usertmp.getId();

                                                users.child(userId).setValue(new User(userId, user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail()));

                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            else {

                                String id = users.push().getKey();
                                users.child(id).setValue(new User(id, user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail()));

                            }

                            Log.d("xxx", "2");

                            Intent intent = new Intent(ProfileCreateActivity.this, MainActivity.class); //Do nowego activity
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Czyści poprzednie activity
                            startActivity(intent);

                        }

                    }
                });

            }

        }

    }

    private void showImageChooser(){ //Otwiera galerię i pobiera zdjęcie

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie profilowe: "), 200);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null && data.getData() != null){

            uriProfileImage = data.getData(); //Zdjęcie ląduje w tym obiekcie

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage); //Ustawiam zdjęcie w ImageView
                profile_image.setImageBitmap(bitmap); //-||-
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void uploadImageToFirebaseStorage() { //Ładowanie zdjęcia do magazynu danych

        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg"); //Tworzenie szblonu zdjęcia

        if (uriProfileImage != null) {

            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // profileImageUrl taskSnapshot.getDownloadUrl().toString(); //this is depreciated

                            //this is the new way to do it
                            profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    profileImageUrl=task.getResult().toString();
                                    Log.i("URL",profileImageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileCreateActivity.this, "aaa "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

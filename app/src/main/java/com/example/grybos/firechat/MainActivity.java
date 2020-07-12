package com.example.grybos.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //zmienne
    private FirebaseAuth mAuth;

    //widgety
    private EditText Edit_text_username, Edit_text_password;
    private Button login;
    private TextView signup_switch;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isNetworkConnected();

        getSupportActionBar().hide();

        Edit_text_username = findViewById(R.id.username);
        Edit_text_password = findViewById(R.id.password);
        login = findViewById(R.id.button_login);
        signup_switch = findViewById(R.id.signup_switch);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userLogin();

            }
        });

        signup_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, SignUpActivity.class));

            }
        });

    }

    @Override
    protected void onStart() { //Przy starcie
        super.onStart();

        if (mAuth.getCurrentUser() != null){ //Jeżeli jest ktoś zalogowany

            Intent intent = new Intent(MainActivity.this, ChatListActivity.class); //Nowe activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Czyści poprzednie activity
            startActivity(intent);

        }

    }

    private void userLogin(){

        String username = Edit_text_username.getText().toString();
        String password = Edit_text_password.getText().toString();

        if (username.isEmpty()){ //Sprawdzam, czy puste

            Edit_text_username.setError("Email is required"); //Ustawiam błąd
            Edit_text_username.requestFocus(); //Ustawiam, że od razu wejdzie wtedy na to Edit Text
            return;

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()){ //Sprawdzam, czy email to jest naprawdę email

            Edit_text_username.setError("Please set a correct email");
            Edit_text_username.requestFocus();
            return;

        }

        if (password.isEmpty()){

            Edit_text_password.setError("Password is required");
            Edit_text_password.requestFocus();
            return;

        }

        if (password.length() < 6){

            Edit_text_password.setError("Minimum lenght of password should be 6");
            Edit_text_password.requestFocus();
            return;

        }

        progressBar.setVisibility(View.VISIBLE); //Pojawia się progressbar

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //Logowanie
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()){

                    //Pomyślne logowanie

                    Intent intent = new Intent(MainActivity.this, ChatListActivity.class); //Nowe activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Czyści poprzednie activity
                    startActivity(intent);

                }
                else {

                    //Błąd

                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show(); //Komunikat zależny od błędu

                }

            }
        });

    }

    private void isNetworkConnected(){

        if (!Networking.checkConnection(MainActivity.this)){

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Ta aplikacja wymaga połączenia z Internetem!");
            alert.setMessage("Włącz Wi-Fi i spróbuj ponownie");
            alert.setCancelable(false);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();

                }
            });
            alert.show();

        }

    }

}

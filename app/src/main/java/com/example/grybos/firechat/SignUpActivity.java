package com.example.grybos.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    //zmienne
    private FirebaseAuth mAuth; //Obiekt do logowania
    private ProgressBar progressBar;

    //widgety
    private EditText Edit_text_username;
    private EditText Edit_text_password;
    private Button sign_up;
    private TextView signup_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        isNetworkConnected();

        getSupportActionBar().hide();

        Edit_text_username = findViewById(R.id.username);
        Edit_text_password = findViewById(R.id.password);
        sign_up = findViewById(R.id.button_signup);
        signup_switch = findViewById(R.id.login_switch);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance(); //Inicjalizacja obiektu do logowania

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();

            }
        });

        signup_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    private void registerUser(){

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

        mAuth.createUserWithEmailAndPassword(username, password) //Rejestracja
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    //Rejestracja przebiega pomyślnie

                    progressBar.setVisibility(View.GONE); //Znika progressbar
                    Intent intent = new Intent(SignUpActivity.this, ChatListActivity.class); //Do nowego activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Czyści poprzednie activity
                    startActivity(intent);
                    Toast.makeText(SignUpActivity.this, "Zarejestrowano!!!", Toast.LENGTH_LONG).show();

                }
                else {

                    //Jest jakiś problem

                    progressBar.setVisibility(View.GONE);

                    if (task.getException() instanceof FirebaseAuthUserCollisionException){ //Jeżeli ten problem to duplikat emaila, to

                        Toast.makeText(SignUpActivity.this, "Takie konto już istnieje!!!", Toast.LENGTH_LONG).show();

                    }
                    else { //inny błąd

                        Toast.makeText(SignUpActivity.this, "Wystąpił błąd!!!", Toast.LENGTH_LONG).show();

                    }

                }

            }
        });

    }

    private void isNetworkConnected(){ //Sprawdzam połączenie z Internetem

        if (!Networking.checkConnection(SignUpActivity.this)){

            AlertDialog.Builder alert = new AlertDialog.Builder(SignUpActivity.this);
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

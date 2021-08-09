package com.example.googleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    SignInButton signInButton;

    LoginButton b2;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b2=(LoginButton)findViewById(R.id.login_button);
        signInButton = (SignInButton) findViewById(R.id.signin);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Facebooklogin.class);
                startActivity(i);
                finish();
            }
        });
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("383589770607-ab5r7o4i5ta6rsc31htufe1tvtf8hqnu.apps.googleusercontent.com").requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = googleSignInClient.getSignInIntent();
                startActivityForResult(i, 99);
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent j = new Intent(MainActivity.this, MainActivity2.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(j);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                Toast.makeText(this, "google sign in done", Toast.LENGTH_SHORT).show();
                try{
                    GoogleSignInAccount googleSignInAccount=signInAccountTask.getResult(ApiException.class);
                    if(googleSignInAccount!=null){
                        AuthCredential authCredential= GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Firebase updated", Toast.LENGTH_SHORT).show();
                                    Intent j=new Intent(MainActivity.this,MainActivity2.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(j);
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Firebase not updated", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }
                catch(ApiException e){
                    e.printStackTrace();
                }

            }

        }

    }
}
package com.example.googleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivity2 extends AppCompatActivity {
    ImageView i1;
    TextView textView;
    TextView t1;
    Button b1;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        i1=(ImageView)findViewById(R.id.imageView);
        b1=(Button)findViewById(R.id.button);
        textView=(TextView)findViewById(R.id.textView);
        t1=(TextView)findViewById(R.id.textView3);

        fa=FirebaseAuth.getInstance();
        googleSignInClient= GoogleSignIn.getClient(MainActivity2.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        FirebaseUser firebaseUser=fa.getCurrentUser();
        if(firebaseUser!=null){
            Glide.with(MainActivity2.this).load(firebaseUser.getPhotoUrl()).into(i1);
            textView.setText(firebaseUser.getDisplayName());
            t1.setText(firebaseUser.getEmail());
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            fa.signOut();
                            Toast.makeText(MainActivity2.this, "sign out", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }
}
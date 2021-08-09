package com.example.googleapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class Facebooklogin extends AppCompatActivity {
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private TextView textView;
    private ImageView imageView;
    private LoginButton loginButton;
    private  static  final String TAG ="FacebookAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebooklogin);
        firebaseAuth=FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        textView=findViewById(R.id.textView2);
        imageView=findViewById(R.id.imageView2);
        loginButton=findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public profile");
        callbackManager=CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel" );            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onError" + error);

            }
        });
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    updateUI(user);
                }
                else{
                    updateUI(null);
                }
            }
        };
        accessTokenTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken==null){
                    firebaseAuth.signOut();
                }
            }
        };
    }
    public void handleFacebookToken(AccessToken token){
        Log.d(TAG,"handleFacebookToken" + token);
        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Successful signIn");
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    updateUI(user);

                }
                else{
                    Log.d(TAG,"Failed signIn",task.getException());
                    Toast.makeText(Facebooklogin.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            textView.setText(user.getDisplayName());
            if(user.getPhotoUrl()!=null){
                String photoUrl=user.getPhotoUrl().toString();
                photoUrl=photoUrl +"?type=large";
                Picasso.get().load(photoUrl).into(imageView);
            }
        }
        else{
            textView.setText(" ");
            Toast.makeText(this, "Login please", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected  void onStart() {

        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onStop() {

        super.onStop();
        if(authStateListener!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
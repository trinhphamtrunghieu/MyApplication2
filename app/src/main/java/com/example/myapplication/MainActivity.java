package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Notify","Call me");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Button button = findViewById(R.id.loginButton);
        Button signup = findViewById(R.id.signUpButton);
        final EditText email = findViewById(R.id.emailField);
        final EditText pass = findViewById(R.id.passField);
        signup.setText("Sign Up");
        signup.setOnClickListener(new View.OnClickListener() {
            boolean check = true;
            @Override
            public void onClick(View view) {
                if((email.getText().toString().isEmpty())){
                    Toast.makeText(MainActivity.this,"This field is required",Toast.LENGTH_SHORT).show();
                    check = false;
                    email.requestFocus();
                }
                else check = checkEmail(email.getText().toString());
                if (pass.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"This field is required",Toast.LENGTH_SHORT).show();
                    check = false;
                    pass.requestFocus();
                }
                else check=checkPass(pass.getText().toString());
                if(check){
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("Notify","createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                            }
                            else{
                                Log.w("Notify", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Toast.makeText(MainActivity.this,"Sign up Successful",Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(MainActivity.this,"Sign up Failed",Toast.LENGTH_SHORT).show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = true;
                if((email.getText().toString().isEmpty())){
                    Toast.makeText(MainActivity.this,"This field is required",Toast.LENGTH_SHORT).show();
                    check = false;
                    email.requestFocus();
                }
                else check = checkEmail(email.getText().toString());
                if (pass.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"This field is required",Toast.LENGTH_SHORT).show();
                    check = false;
                    pass.requestFocus();
                }
                else check=checkPass(pass.getText().toString());
                if(check){
                    mAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Log.d("Notify","Log in with email : success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(MainActivity.this,"Log in successful",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this,AddSensor.class));
                                    }
                                    else{
                                        Log.d("Notify","Log in with email : failed");
                                        Toast.makeText(MainActivity.this,"Log in failed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        LoginButton facebookLogin = findViewById(R.id.loginFacebook);
        facebookLogin.setReadPermissions("email","public_profile");
        facebookLogin.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Facebook login","facebook:onSuccess:"+loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        startActivity(new Intent(MainActivity.this,AddSensor.class));
                    }

                    @Override
                    public void onCancel() {
                        Log.d("Facebook login","facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("Facebook login","facebook:onError: "+error);
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currUser = mAuth.getCurrentUser();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }
    public boolean checkEmail(String e){
        if(e.contains("@")) {
            Log.d("Notify","Email right format");
            return true;
        }
        Log.d("Notify","Email wrong format");
        return false;
    }
    public boolean checkPass(String p){
        Log.d("Notify",p);
        String regex = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,40})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(p);
        if(!matcher.matches()) {
            Log.d("Notify","Pass wrong format");
            return false;
        }
        return true;
    }
    private void handleFacebookAccessToken(AccessToken token){
        Log.d("FB Access Token ","handleFacebookAccessToken: "+token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign in with credential", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sign in with credential", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private Context context;
    private FrameLayout fl;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    public loginFragment(){}
    public loginFragment(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Login");
        return inflater.inflate(R.layout.login,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Notify","Call me");
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        final Button button = view.findViewById(R.id.loginButton);
        final Button signup = view.findViewById(R.id.signUpButton);
        final EditText email = view.findViewById(R.id.emailField);
        final EditText pass = view.findViewById(R.id.passField);
        fl = view.findViewById(R.id.content_login);
        signup.setText("Sign Up");
        signup.setOnClickListener(new View.OnClickListener() {
            boolean check = true;
            @Override
            public void onClick(View view) {
                signup.setClickable(false);
                button.setClickable(false);
                if((email.getText().toString().isEmpty())){
                    Toast.makeText(context,"This field is required",Toast.LENGTH_SHORT).show();
                    check = false;
                    email.requestFocus();

                }
                else check = checkEmail(email.getText().toString());
                if (pass.getText().toString().isEmpty()){
                    Toast.makeText(context,"This field is required",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Toast.makeText(context,"Sign up Successful",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context,"Sign up Failed",Toast.LENGTH_SHORT).show();
                    email.setText("");
                    pass.setText("");
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = true;
                if((email.getText().toString().isEmpty())){
                    Toast.makeText(context,"This field is required",Toast.LENGTH_SHORT).show();
                    check = false;
                    email.requestFocus();
                }
                else check = checkEmail(email.getText().toString());
                if (pass.getText().toString().isEmpty()){
                    Toast.makeText(context,"This field is required",Toast.LENGTH_SHORT).show();
                    check = false;
                    pass.requestFocus();
                }
                else check=checkPass(pass.getText().toString());
                if(check){
                    signup.setClickable(false);
                    button.setClickable(false);
                    mAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Log.d("Notify","Log in with email : success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(context,"Logging in ...",Toast.LENGTH_SHORT).show();
                                        fl.removeAllViews();
                                        Fragment fragment = new addSensorFragment(context);
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_login, fragment).addToBackStack(null);
                                        ft.commit();
                                    }
                                    else{
                                        Log.d("Notify","Log in with email : failed");
                                        Toast.makeText(context,"Wrong password and email combination",Toast.LENGTH_SHORT).show();
                                        email.setText("");
                                        pass.setText("");
                                    }
                                }
                            });
                }
                signup.setClickable(true);
                button.setClickable(true);

            }
        });
    }

    @Override
    public void onDestroy() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    private void sendData(String result){
        Intent i = new Intent(getActivity().getBaseContext(),Main2Activity.class);
        i.putExtra("checkLogin",result);
        getActivity().startActivity(i);
    }
}
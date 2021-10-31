package com.example.societychange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {
    TextInputLayout emailinput,passwordinput;
    private FirebaseAuth mAuth;
    Button loginInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        emailinput=findViewById(R.id.email);
        passwordinput=findViewById(R.id.password);
        loginInput=findViewById(R.id.bttn2);

        loginInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=emailinput.getEditText().getText().toString().trim();
                String password=passwordinput.getEditText().getText().toString().trim();
                if ((email.isEmpty())){
                    emailinput.setError("Enter an email address");
                    emailinput.requestFocus();
                    return;


                }if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailinput.setError("Enter a valid email address");
                    emailinput.requestFocus();
                    return;
                }
                if ((password.isEmpty())){
                    passwordinput.setError("Enter  password");
                    passwordinput.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Intent intent = new Intent(getApplicationContext(),HomePage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            FirebaseUser user=mAuth.getCurrentUser();



                        }else {
                            Toast.makeText(getApplicationContext(),"Login Unsuccessfull",Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            finish();

        }
    }

}
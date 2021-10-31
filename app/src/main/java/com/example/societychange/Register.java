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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference mRef1 =db.collection("User").document("Profile").collection("User Profile");
    TextInputLayout nameinput,emailinput,passwordinput,numberinput;
    Button registerbutton;
    private static final String TAG="Register";
    private static final String NAME_KEY="userName";
    private static final String EMAIL_KEY="userEmail";
    private static final String PHONE_KEY="userPhoneNumber";
    private static final String ACCOUNT_KEY="userID";
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameinput=findViewById(R.id.name);
        emailinput=findViewById(R.id.email);
        passwordinput=findViewById(R.id.password);
        numberinput=findViewById(R.id.number);
        registerbutton=findViewById(R.id.register);
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth=FirebaseAuth.getInstance();

            String nameInput=nameinput.getEditText().getText().toString().trim();
            String emaiInput=emailinput.getEditText().getText().toString().trim();
            String passwordInput=passwordinput.getEditText().getText().toString().trim();
            String numberInput=numberinput.getEditText().getText().toString().trim();
            if ((nameInput.isEmpty())){
                nameinput.setError("Enter an email address");
                nameinput.requestFocus();
                return;
            }if ((emaiInput.isEmpty())){
                    emailinput.setError("Enter an email address");
                    emailinput.requestFocus();
                    return;


                }if(!Patterns.EMAIL_ADDRESS.matcher(emaiInput).matches()){
                    emailinput.setError("Enter a valid email address");
                    emailinput.requestFocus();
                    return;
                }
            if ((passwordInput.isEmpty())){
                    passwordinput.setError("Enter  password");
                    passwordinput.requestFocus();
                    return;
                }if ((numberInput.isEmpty())){
                    numberinput.setError("Enter phone Number");
                    numberinput.requestFocus();
                    return;
                }


            mAuth.createUserWithEmailAndPassword(emaiInput,passwordInput) .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        userId=mAuth.getCurrentUser().getUid();
                        String name=nameinput.getEditText().getText().toString().trim();
                        String email=emailinput.getEditText().getText().toString().trim();
                        String phonenumber=numberinput.getEditText().getText().toString().trim();
                        Map<String,Object> note = new HashMap<>();

                        note.put(NAME_KEY,name);
                        note.put(EMAIL_KEY,email);
                        note.put(PHONE_KEY,phonenumber);
                        note.put(ACCOUNT_KEY,userId);
                        mRef1.add(note);
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_LONG).show();
                        finish();
                    }
                  /*  if(task.isSuccessful()){



                    }

                   */
                    else {
                        Toast.makeText(getApplicationContext(),"Error :"+task.getException().getMessage(),Toast.LENGTH_LONG).show();

                    }

                }
            });

            }
        });
    }


}
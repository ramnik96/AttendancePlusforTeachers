package com.example.ramnik_singh.attendanceplus_teacher;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail,editTextPassword,editTextPassword1;
    private Button buttonRegister;
    private TextView textViewLogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextEmail=(EditText) findViewById(R.id.editTextEmail);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        editTextPassword1=(EditText) findViewById(R.id.editTextPassword1);
        buttonRegister=(Button)findViewById(R.id.buttonRegister);
        textViewLogin=(TextView)findViewById(R.id.textViewLogin);
        progressbar=(ProgressBar)findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        buttonRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                registerUser();

            }
        });
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });



    }

    private void registerUser() {
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        String password1=editTextPassword1.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is Required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is Required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length()<6){
            editTextPassword.setError("Minimum length of password is 6");
            editTextPassword.requestFocus();
            return;
        }
        if(!password.equals(password1)){
            editTextPassword1.setError("Passwords don't match!");
            editTextPassword1.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressbar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"User Registered Successfully",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(RegisterActivity.this,StudentProfileActivity.class));

                }
                else
                {
                    String errormsg=task.getException().getMessage();
                    Toast.makeText(getApplicationContext(),errormsg,Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



}

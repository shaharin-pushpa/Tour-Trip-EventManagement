package com.example.kowshick.travelmate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText emailEt,passEt;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailEt=findViewById(R.id.logEmail);
        passEt=findViewById(R.id.logPass);
       passEt.setTransformationMethod(new PasswordTransformationMethod());
        auth=FirebaseAuth.getInstance();

    }

    public void regiser(View view) {
        startActivity(new Intent(MainActivity.this,RegisterActivity.class));
    }

    public void logIn(View view) {
        try{
        String em=emailEt.getText().toString();
        String pass=passEt.getText().toString();
        Task<AuthResult> task = auth.signInWithEmailAndPassword(em,pass);
        task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Toast.makeText(MainActivity.this, "LogIn Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,NavigationDrawer.class));

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
        catch (Exception e){
        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    }
}

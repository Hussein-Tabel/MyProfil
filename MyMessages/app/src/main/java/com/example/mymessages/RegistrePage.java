package com.example.mymessages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegistrePage extends AppCompatActivity {

    EditText etusername, etemail, etpassword, etconfirmpassword;
    RadioButton rbmale, rbfemale;
    Button btnregister, btncancel;
    DatabaseReference DBR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registre_page);

        etusername = findViewById(R.id.etUsername);
        etemail = findViewById(R.id.etEmail);
        etpassword = findViewById(R.id.etPassword);
        etconfirmpassword = findViewById(R.id.etConfirmPassword);
        rbmale = findViewById(R.id.rbMale);
        rbfemale = findViewById(R.id.rbFemale);
        btnregister = findViewById(R.id.btnRegister);
        btncancel = findViewById(R.id.btnCancel);

        DBR = FirebaseDatabase.getInstance().getReference("USERS");

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateUser();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public String Gender(){
        String gender = "";
        if (rbmale.isChecked()){
            gender = "Male";
            return gender;
        } else if (rbfemale.isChecked()) {
            gender = "Female";
            return gender;
        }else{
            return gender;
        }
    }

    public boolean Check_isNonEmpty(){
        String username = etusername.getText().toString().trim();
        String email = etemail.getText().toString().trim();
        String password = etpassword.getText().toString().trim();
        String pass2 = etconfirmpassword.getText().toString().trim();
        String gender = Gender();

        if (!pass2.equals(password)){
            Toast.makeText(RegistrePage.this, "Different codes.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || gender.isEmpty()){

            Toast.makeText(RegistrePage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    public void CreateUser(){
       if (Check_isNonEmpty()){
           String username = etusername.getText().toString().trim();
           String email = etemail.getText().toString().trim();
           String password = etpassword.getText().toString().trim();
           String gender = Gender();

           DBR.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   boolean UserFound = false;
                   for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                       String UserName = snapshot.child("UserName").getValue(String.class);
                       if (UserName.equals(username)) {
                           UserFound = true;
                           Toast.makeText(RegistrePage.this, "The username already exist!", Toast.LENGTH_SHORT).show();
                           break;
                       }
                   }
                   if (UserFound == false){
                       HashMap<String, Object> UserHashMap = new HashMap<>();
                       UserHashMap.put("UserName", username);
                       UserHashMap.put("Email", email);
                       UserHashMap.put("Password", password);

                       FirebaseDatabase database = FirebaseDatabase.getInstance();
                       DatabaseReference myRef = database.getReference("USERS");
                       String key = myRef.push().getKey();
                       myRef.child(key).setValue(UserHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               Toast.makeText(RegistrePage.this, "User register successfully.", Toast.LENGTH_SHORT).show();
                               Intent i = new Intent(RegistrePage.this, MainActivity.class);
                               startActivity(i);
                               etusername.getText().clear();
                               etemail.getText().clear();
                               etpassword.getText().clear();
                               etconfirmpassword.getText().clear();
                               rbmale.setChecked(false);
                               rbfemale.setChecked(false);
                           }
                       });
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }
    }
}
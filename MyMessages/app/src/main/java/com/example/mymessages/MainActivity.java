package com.example.mymessages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText etusername, etpassword;
    Button btnlogin;
    TextView tvregistration;
    DatabaseReference DBR;
    SharedPreferences SP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etusername = findViewById(R.id.etUsername);
        etpassword = findViewById(R.id.etPassword);
        btnlogin = findViewById(R.id.btnLogin);
        tvregistration = findViewById(R.id.tvRegistration);

        DBR = FirebaseDatabase.getInstance().getReference("USERS");

        SP = getSharedPreferences("User", MODE_PRIVATE);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etusername.getText().toString().trim();
                String password = etpassword.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }else{
                    DBR.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean UserFound = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String UserName = snapshot.child("UserName").getValue(String.class);
                                String Password = snapshot.child("Password").getValue(String.class);
                                if (UserName.equals(username) && Password.equals(password)) {
                                    UserFound = true;
                                    SharedPreferences.Editor editor = SP.edit();
                                    editor.putString("UserName", username);
                                    editor.apply();
                                    etusername.getText().clear();
                                    etpassword.getText().clear();
                                    Intent i = new Intent(MainActivity.this, SendMessagePage.class);
                                    startActivity(i);
                                    break;
                                }
                            }
                            if (UserFound == false){
                                Toast.makeText(MainActivity.this, "The username is not exist!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        tvregistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegistrePage.class);
                startActivity(i);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
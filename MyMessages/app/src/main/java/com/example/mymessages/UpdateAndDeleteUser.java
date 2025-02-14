package com.example.mymessages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import androidx.annotation.NonNull;

public class UpdateAndDeleteUser extends AppCompatActivity {

    EditText etusername, etemail;
    Button btnupdate, btndelete, btncancel, btnlayout;
    SharedPreferences SP;
    DatabaseReference DBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_update_and_delete_user);

        btnupdate = findViewById(R.id.btnUpdate);
        btndelete = findViewById(R.id.btnDelete);
        etusername = findViewById(R.id.etUsername);
        etemail = findViewById(R.id.etEmail);
        btncancel = findViewById(R.id.btnCancel);
        btnlayout = findViewById(R.id.btnLayout);

        SP = getSharedPreferences("User", MODE_PRIVATE);
        String name = SP.getString("UserName", null);
        etusername.setText(name);

        DBR = FirebaseDatabase.getInstance().getReference("USERS");

        fetchAndSetEmail(name);

        btnlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = SP.edit();
                editor.clear();
                editor.apply();
                Intent i = new Intent(UpdateAndDeleteUser.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = etusername.getText().toString().trim();
                String newEmail = etemail.getText().toString().trim();
                if (newName.isEmpty() || newEmail.isEmpty()) {
                    Toast.makeText(UpdateAndDeleteUser.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    updateUser(newName, newEmail);
                }
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(name);
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

    public void fetchAndSetEmail(String name) {
        DBR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String UserName = snapshot.child("UserName").getValue(String.class);
                    if(UserName.equals(name)){
                        etemail.setText(snapshot.child("Email").getValue(String.class));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUser(String newName, String newEmail) {
        DBR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userExists = false;
                String currentUserId = SP.getString("UserName", null);
                String userId = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String UserName = snapshot.child("UserName").getValue(String.class);
                    if (UserName.equals(newName) && !newName.equals(currentUserId)) {
                        userExists = true;
                        break;
                    } else if (UserName.equals(currentUserId)) {
                        userId = snapshot.getKey();
                    }
                }

                if (userExists) {
                    Toast.makeText(UpdateAndDeleteUser.this, "The username already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    if (userId != null) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("USERS");
                        myRef.child(userId).child("UserName").setValue(newName);
                        myRef.child(userId).child("Email").setValue(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UpdateAndDeleteUser.this, "User details updated successfully", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = SP.edit();
                                editor.clear();
                                editor.putString("UserName", newName);
                                editor.apply();
                                etusername.getText().clear();
                                etemail.getText().clear();
                                Intent i = new Intent(UpdateAndDeleteUser.this, SendMessagePage.class);
                                startActivity(i);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateAndDeleteUser.this, "Failed to check user existence", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteUser(String name) {
        DBR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUserId = SP.getString("UserName", null);
                String userId = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String UserName = snapshot.child("UserName").getValue(String.class);
                    if (UserName.equals(name)) {
                        userId = snapshot.getKey();
                        break;
                    }
                }

                DBR.child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateAndDeleteUser.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = SP.edit();
                            editor.clear();
                            editor.apply();
                            Intent i = new Intent(UpdateAndDeleteUser.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(UpdateAndDeleteUser.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
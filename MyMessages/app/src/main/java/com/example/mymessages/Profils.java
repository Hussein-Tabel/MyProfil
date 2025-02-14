package com.example.mymessages;

import android.content.Intent;
import android.os.Bundle;
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

public class Profils extends AppCompatActivity {

    TextView tvname, tvemail, tvpublication;
    DatabaseReference DBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profils);

        tvname = findViewById(R.id.tvUserName);
        tvemail = findViewById(R.id.tvEmail);
        tvpublication = findViewById(R.id.tvPublication);

        DBR = FirebaseDatabase.getInstance().getReference("MESSAGES");

        Intent j = getIntent();
        String name = j.getStringExtra("NAME");
        tvname.setText(name);
        tvemail.setText(j.getStringExtra("EMAIL"));

        ListOfPublication(name);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void ListOfPublication(String name) {
        DBR.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder publicationData = new StringBuilder();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String pub = dataSnapshot.child("message").getValue(String.class);
                    if (pub != null) {
                        publicationData.append(pub).append("\n");
                    }
                }

                tvpublication.setText(publicationData.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvpublication.setText("Failed to load publications.");
                Toast.makeText(Profils.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
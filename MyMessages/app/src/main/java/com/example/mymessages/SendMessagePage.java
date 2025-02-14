package com.example.mymessages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

public class SendMessagePage extends AppCompatActivity {

    TextView tvusername, tvuserfindname, tvuserfindemail, tvClose;
    LinearLayout lluserfind;
    SharedPreferences SP;
    EditText etusername;
    ImageView ivsearch, ivprofil, ivPublication;
    DatabaseReference DBR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_message_page);

        tvusername = findViewById(R.id.tvUsername);
        etusername = findViewById(R.id.etUsername);
        ivsearch = findViewById(R.id.ivSearch);
        tvuserfindname = findViewById(R.id.tvUserFindName);
        tvuserfindemail = findViewById(R.id.tvUserFindEmail);
        lluserfind = findViewById(R.id.llUserFind);
        tvClose = findViewById(R.id.tvClose);
        ivprofil = findViewById(R.id.ivProfil);
        ivPublication = findViewById(R.id.ivPublication);

        lluserfind.setVisibility(View.GONE);

        SP = getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();

        DBR = FirebaseDatabase.getInstance().getReference("USERS");

        String me = SP.getString("UserName", null);
        if(me != null){
            tvusername.setText(me);
        }

        ivPublication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SendMessagePage.this, WriteAndReadMsg.class);
                i.putExtra("SENDER", me);
                startActivity(i);
            }
        });

        ivsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchUser();
            }
        });

        ivprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SendMessagePage.this, UpdateAndDeleteUser.class);
                startActivity(i);
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etusername.getText().clear();
                lluserfind.setVisibility(View.GONE);
            }
        });

        lluserfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userSearch = tvuserfindname.getText().toString().trim();
                String emailSearch = tvuserfindemail.getText().toString().trim();
                Intent i = new Intent(SendMessagePage.this, Profils.class);
                i.putExtra("NAME", userSearch);
                i.putExtra("EMAIL", emailSearch);
                startActivity(i);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void fetchUser(){
        String me = SP.getString("UserName", null);
        String username = etusername.getText().toString().trim();
        if (username.isEmpty()){
            Toast.makeText(SendMessagePage.this, "Please select the user name", Toast.LENGTH_SHORT).show();
        }else {
            DBR.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean userFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String UserName = snapshot.child("UserName").getValue(String.class);
                        if (UserName.equals(username) && !UserName.equals(me)){
                            String email = snapshot.child("Email").getValue(String.class);
                            lluserfind.setVisibility(View.VISIBLE);
                            tvuserfindname.setText(UserName);
                            tvuserfindemail.setText(email);
                            userFound = true;
                            break;
                        }
                    }
                    if (userFound == false){
                        Toast.makeText(SendMessagePage.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
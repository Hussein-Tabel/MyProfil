package com.example.mymessages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class WriteAndReadMsg extends AppCompatActivity {

    TextView tvmsg;
    EditText etmsg;
    ImageView ivsend;
    DatabaseReference DBR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_write_and_read_msg);

        tvmsg = findViewById(R.id.tvMsg);
        etmsg = findViewById(R.id.etMsg);
        ivsend = findViewById(R.id.ivSend);

        DBR = FirebaseDatabase.getInstance().getReference("MESSAGES");

        Intent j = getIntent();
        String sender = j.getStringExtra("SENDER");

        List_Message(sender);

        ivsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etmsg.getText().toString().trim();
                if (message.isEmpty()){
                    Toast.makeText(WriteAndReadMsg.this, "Please write message", Toast.LENGTH_SHORT).show();
                }else{
                    SendMessage(message, sender);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void SendMessage(String message, String sender) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("MESSAGES").child(sender);

        String messageId = myRef.push().getKey();

        HashMap<String, Object> MsgHashMap = new HashMap<>();
        MsgHashMap.put("message", message);
        MsgHashMap.put("sender", sender);

        myRef.child(messageId).setValue(MsgHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    etmsg.setText("");
                    List_Message(sender);
                } else {
                    Toast.makeText(WriteAndReadMsg.this, "Message failed to send", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void List_Message(String sender) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("MESSAGES");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HashMap<String, Object>> messagesList = new ArrayList<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot msgSnapshot : userSnapshot.getChildren()) {
                        HashMap<String, Object> messageData = (HashMap<String, Object>) msgSnapshot.getValue();
                        if (messageData != null) {
                            String msgSender = (String) messageData.get("sender");
                            if (msgSender != null && msgSender.equals(sender)) {
                                messagesList.add(messageData);
                            }
                        }
                    }
                }
                StringBuilder messagesText = new StringBuilder();
                for (HashMap<String, Object> msg : messagesList) {
                    String message = (String) msg.get("message");
                    messagesText.append(message).append("\n\n");
                }

                tvmsg.setText(messagesText.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}
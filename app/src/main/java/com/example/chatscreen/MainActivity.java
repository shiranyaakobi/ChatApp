package com.example.chatscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.User;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private RecyclerView rvChat;
    private ChatAdapter rvAdapter;

    private ImageButton sendButton;
    private EditText messageField;


    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = findViewById(R.id.send_btn);
        messageField = findViewById(R.id.message_field);
        rvChat = findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(this));


        if (getIntent() != null)
            currentUser = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);

        FirebaseFirestore.getInstance()
                .collection("chat")
                .orderBy("date")
                .addSnapshotListener((value, error) -> {
                    ArrayList<ChatMessage> chatMessageArrayList = new ArrayList<>();
                    ChatMessage next;
                    if (value != null)
                        for (DocumentSnapshot child : value.getDocuments()) {
                            next = child.toObject(ChatMessage.class);
                            if (next != null)
                                chatMessageArrayList.add(next);
                        }
                    if (rvAdapter == null) {
                        rvAdapter = new ChatAdapter(chatMessageArrayList);
                        rvChat.setAdapter(rvAdapter);
                    } else {
                        rvAdapter.reloadMessages(chatMessageArrayList);
                        rvChat.scrollToPosition(chatMessageArrayList.size() - 1);
                    }
                });

        sendButton.setOnClickListener((v) -> sendMessage());
    }

    public void sendMessage() {
        String content = messageField.getText().toString();
        if (!content.isEmpty() && FirebaseAuth.getInstance().getCurrentUser() != null) {
            ChatMessage cm = new ChatMessage(System.currentTimeMillis(), FirebaseAuth.getInstance().getUid(),
                    FirebaseAuth.getInstance().getCurrentUser()
                            .getEmail()
                            .split("@")[0], content,
                    currentUser != null ? currentUser.getImage() : LoginActivity.anonymous_user_image);
            messageField.getText().clear();
            FirebaseFirestore.getInstance()
                    .collection("chat")
                    .add(cm);
        }
    }

}
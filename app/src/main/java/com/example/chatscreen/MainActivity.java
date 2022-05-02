package com.example.chatscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.Sender;
import com.example.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private RecyclerView rvChat;
    private ChatAdapter rvAdapter;

    private ImageButton sendButton;
    private EditText messageField;

    private RequestQueue requestQueue;


    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set Crash analytics
        FirebaseCrashlytics.getInstance()
                .setCrashlyticsCollectionEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        sendButton = findViewById(R.id.send_btn);
        messageField = findViewById(R.id.message_field);
        rvChat = findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = Volley.newRequestQueue(this);

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
            ChatMessage cm = new ChatMessage(FirebaseAuth.getInstance().getUid(),
                    FirebaseAuth.getInstance().getCurrentUser()
                            .getEmail()
                            .split("@")[0], content,
                    currentUser != null ? currentUser.getImage() : LoginActivity.anonymous_user_image);
            messageField.getText().clear();
            FirebaseFirestore.getInstance()
                    .collection("chat")
                    .add(cm);
            sendPushNotificationToFCM(cm);
        }

    }

    public void sendPushNotificationToFCM(ChatMessage cm) {
        Sender sender = new Sender("all",cm,this,this);
        sender.sendNotification();
    }
}
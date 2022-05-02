package com.example;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatscreen.ChatMessage;
import com.example.chatscreen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Sender {

    String userFcmToken;
    ChatMessage cm;
    Context mContext;
    Activity mActivity;


    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAAedxuFuU:APA91bHryeF7LhgWX2qPcaM3hF8ZkssFm82R9LVd7_eZCpD8doK3cZkgb4Nv5_2ER8ctAWykbe9WwnUTTvXC_20ZNQ0VQ3T2WNjuqtg5_ns_g2BCFenyiCWSeglVu6WYXNE8vW1494dj";
    public Sender(String userFcmToken, ChatMessage cm, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.cm = cm;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void sendNotification() {

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", "/topics/" + userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", cm.getSenderName());
            notiObject.put("body", cm.getMessageContent());
            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, error -> {
            }) {
                @Override
                public Map<String, String> getHeaders() {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;

                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
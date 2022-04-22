package com.example.chatscreen;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {


    private ImageView profileImagePick;

    public static final int GALLERY_REQUEST = 1;
    public static final String anonymous_user_image = "https://i.ibb.co/cv8cNGh/profile.png";
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );


    FirebaseAnalytics analytics;
    private Uri userImagePicked = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        analytics = FirebaseAnalytics.getInstance(this);

        // Add Click listener to google sign in button
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        profileImagePick = findViewById(R.id.profile_image_picker);

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null)
            findViewById(R.id.imagePickLayout).setVisibility(View.GONE);
        findViewById(R.id.google_sign_in)
                .setOnClickListener((v) -> signInLauncher.launch(signInIntent));
        findViewById(R.id.profile_image_picker).setOnClickListener((v) -> pickGalleryImage());
    }


    // Accept auth result from google
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK && FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore
                    .getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user == null) {
                            // user picked an image -> upload to fire storage
                            if (userImagePicked != null) {
                                uploadImageToStorage(userImagePicked, FirebaseAuth.getInstance().getUid(), imageUrl -> {
                                    User newUser = new User(imageUrl, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    saveUserToFirestore(newUser);
                                });

                                // user did not pick an image -> register and login
                            } else {
                                User newUser = new User(anonymous_user_image, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                saveUserToFirestore(newUser);
                            }
                        } else signInUser(user);
                    }).addOnFailureListener(e -> Toast.makeText(this, "There was a problem logging you in please try again later", Toast.LENGTH_SHORT).show());
        } else if (response != null && response.getError() != null) {
            Toast.makeText(this, "Sign-n failed " + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
        }
    }


    public void uploadImageToStorage(Uri uri, String uid, OnSuccessListener<String> successListener) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("UserImages/")
                .child(uid);

        ref.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(newImageUri -> successListener.onSuccess(newImageUri.toString())))
                .addOnFailureListener(e -> Toast.makeText(this, "There was a problem uploading your image", Toast.LENGTH_SHORT).show());
    }

    public void saveUserToFirestore(User u) {
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(u)
                .addOnSuccessListener(unused -> signInUser(u));
    }

    public void signInUser(User u) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", new Gson().toJson(u));
        startActivity(i);
        Bundle loginData = new Bundle();
        loginData.putString("User_Name", u.getName());
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, loginData);
    }


    // pick image from gallery
    public void pickGalleryImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST + 1);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_REQUEST);
        }
    }


    // Receive user image picked from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && data != null) {
            Uri image = data.getData();
            this.userImagePicked = image;
            profileImagePick.setImageURI(image);
        }
    }
}

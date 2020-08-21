package com.mindyyip.bestteas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    private ImageView profilePicture;
    private EditText nameInput, emailInput, bioInput;
    private TextView schoolInput;
    private Button submitButton, backButton;
    private Spinner schoolSpinner;
    private FirebaseAuth auth;
    private DatabaseReference userData;
    private String userId, name, email, school, bio, profilePicUrl;
    private Uri resultUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        nameInput = (EditText) findViewById(R.id.name);
        emailInput = (EditText) findViewById(R.id.email);
        bioInput = (EditText) findViewById(R.id.bio);
        schoolInput = (TextView) findViewById(R.id.currentSchool);
        schoolSpinner = findViewById(R.id.schoolList);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        submitButton = findViewById(R.id.confirm);
        backButton = findViewById(R.id.back);

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        userData= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        getUserInfo();
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(Intent.ACTION_PICK);
                intent.setType("image/+");
                startActivityForResult(intent, 1);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUserInfo() {
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if(map.get("name") != null) {
                        name = map.get("name").toString();
                        nameInput.setText(name);
                    }
                    if(map.get("email") != null) {
                        email = map.get("email").toString();
                        emailInput.setText(email);
                    }
                    if(map.get("school") != null) {
                        school = map.get("school").toString();
                        schoolInput.setText(school);

                    }
                    if(map.get("bio") != null) {
                        bio = map.get("bio").toString();
                        bioInput.setText(bio);
                    }
                    if(map.get("profilePictureUrl") != null) {
                        profilePicUrl = map.get("profilePictureUrl").toString();
                        switch(profilePicUrl) {
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.default_profile).into(profilePicture);
                                break;
                            default:
                                Glide.with(getApplication()).load(profilePicUrl).into(profilePicture);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveUserInfo() {
        name = nameInput.getText().toString();
        email = emailInput.getText().toString();
        bio = bioInput.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("email", email);
        userInfo.put("bio", bio);
        int schoolId = schoolSpinner.getSelectedItemPosition();
        String schoolName = schoolSpinner.getSelectedItem().toString();
        if (schoolId == 0) {
            schoolName = school;
        }
        else {
            Toast.makeText(SettingsActivity.this, "Re-Login to see changes", Toast.LENGTH_SHORT).show();
        }
        userInfo.put("school", schoolName);


        userData.updateChildren(userInfo);
        if (resultUri != null) { //if there is an image
            final StorageReference imgPath = FirebaseStorage.getInstance().getReference().child("profilePictures").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri); //image location
            }
            catch (IOException e){
                e.printStackTrace();
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteStream); //compress the image
            byte[] data = byteStream.toByteArray(); //helps save to storage
            UploadTask upload = imgPath.putBytes(data);
            upload.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map userInfo = new HashMap();
                            userInfo.put("profilePictureUrl", uri.toString());
                            userData.updateChildren(userInfo);
                            finish();
                        }
                    });
                }
            });

        }
        else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) { //1 Lets activity know which intent
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profilePicture.setImageURI(resultUri);
        }
    }
}
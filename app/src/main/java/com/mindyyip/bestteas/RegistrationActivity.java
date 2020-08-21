package com.mindyyip.bestteas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private Button registerButton, backButton;
    private EditText emailReg, passwordReg, nameReg, bioReg;
    private ImageView profilePicReg; //added
    private Spinner schoolSpinner;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private DatabaseReference currUserData;
    private Uri resultUri; //added
    private String profilePictureUrl; //added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //if the login is good
                if (user != null) {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        registerButton = findViewById(R.id.register);
        backButton = findViewById(R.id.back);
        nameReg = findViewById(R.id.name);
        emailReg = findViewById(R.id.email);
        bioReg = findViewById(R.id.bio);
        passwordReg = findViewById(R.id.password);
        schoolSpinner = findViewById(R.id.schoolList);
        profilePicReg =  (ImageView) findViewById(R.id.profilePicture); //added

        profilePicReg.setOnClickListener(new View.OnClickListener() { //added
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(Intent.ACTION_PICK);
                intent.setType("image/+");
                startActivityForResult(intent, 1);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                //create user with inputted email and password
                final String name = nameReg.getText().toString();
                final String email = emailReg.getText().toString();
                final String password = passwordReg.getText().toString();
                final String bio = bioReg.getText().toString();

                int schoolId = schoolSpinner.getSelectedItemPosition();
                final String school = schoolSpinner.getSelectedItem().toString();
                if (schoolId == 0) {
                    Toast.makeText(RegistrationActivity.this, "Please select a school", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    schoolId--;
                }
                if (bio.matches("")){
                    Toast.makeText(RegistrationActivity.this, "Please enter a bio", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if registration failed
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Sign up error", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String userId = auth.getCurrentUser().getUid();

                            currUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map userInfo = new HashMap();
                            userInfo.put("name", name);
                            userInfo.put("email", email);
                            userInfo.put("school", school);
                            userInfo.put("bio", bio);
//                            currUserData.updateChildren(userInfo);
                            //added
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
                                                currUserData.updateChildren(userInfo);

                                            }
                                        });
                                    }
                                });

                            }
                            else {
                                userInfo.put("profilePictureUrl", "default");
                                currUserData.updateChildren(userInfo);
                            }
                            //till here
                            //moved to inside
//                            userInfo.put("profilePictureUrl", "default");
//                            currUserData.updateChildren(userInfo);
                        }
                    }
                });
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseAuthStateListener);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) { //1 Lets activity know which intent
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profilePicReg.setImageURI(resultUri);
        }
    }
}
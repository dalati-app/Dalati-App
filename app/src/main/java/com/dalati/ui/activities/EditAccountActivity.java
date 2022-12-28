package com.dalati.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dalati.R;
import com.dalati.ui.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

public class EditAccountActivity extends AppCompatActivity {
    EditText etName, etPhone, etEmail;
    Button btnEdit;
    User user;
    ImageView btnImage;
    DatabaseReference databaseReference2;
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ImageView add_img;
    Button btnUpload;
    FirebaseUser firebaseUser;
    String name, desc, category, price, fileLink;
    String library_id, book_id, type;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        defineViews();
        getUser();
    }

    public void defineViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        btnEdit = findViewById(R.id.btnEdit);
        btnImage = findViewById(R.id.profileImg);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Users");

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });


    }

    private void update() {
        if (!user.getEmail().equals(etEmail.getText().toString())) {
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            user1.updateEmail(etEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.setEmail(etEmail.getText().toString());
                            }
                        }
                    });
        }

        user.setName(etName.getText().toString());
        user.setPhone(etPhone.getText().toString());
        databaseReference2.child("End Users").child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    saveObjectToSharedPreference(user);
                    finish();
                }
            }
        });


    }

    public void saveObjectToSharedPreference(Object object) {
        SharedPreferences mPrefs = getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString("Key", json);
        prefsEditor.commit();


    }

    public void getUser() {
        SharedPreferences mPrefs = getSharedPreferences("User", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = mPrefs.getString("Key", "");
        user = gson.fromJson(json2, User.class);
        if (user != null) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
        } else {
            Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show();
        }
    }


    public void upload() {


        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StorageReference ref
                = storageReference
                .child(
                        "images/"
                                + UUID.randomUUID().toString());
        UploadTask uploadTask = ref.putFile(filePath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    fileLink = task.getResult().toString();
                                    user.setImgUrl(fileLink);
                                    databaseReference2.child(user.getId()).child("imgUrl").setValue(fileLink);
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress
                        = (100.0
                        * taskSnapshot.getBytesTransferred()
                        / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage(
                        "Uploaded "
                                + (int) progress + "%");
                System.out.println("Your progress is: " + progress);
            }
        });

    }


    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                btnImage.setImageBitmap(bitmap);
                upload();
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}

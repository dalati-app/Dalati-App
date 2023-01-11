package com.dalati.ui.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.dalati.MainActivity;
import com.dalati.R;
import com.dalati.ui.base.BaseActivity;
import com.dalati.ui.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class LoginActivity extends BaseActivity {
    TextView tvSignUp, tvReset;
    String email, password;
    Button loginBtn;
    EditText et_email, et_password;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineViews();

    }

    @Override
    public int defineLayout() {
        return R.layout.activity_login;
    }

    public void defineViews() {
        tvSignUp = findViewById(R.id.tv_signUp);
        tvReset = findViewById(R.id.tvReset);
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Library");
        auth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.btnSignIn);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialog();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_email.getText().toString();
                password = et_password.getText().toString();

                if (isValidate()) {
                    signingIn(email, password);
                }
            }
        });
    }

    public void signingIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String id = firebaseUser.getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child("End Users").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                User user = snapshot.getValue(User.class);
                                saveObjectToSharedPreference(user);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    if (!firebaseUser.isEmailVerified()) {
                        firebaseUser.sendEmailVerification();
                    }
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    public boolean isValidate() {
        if (email.isEmpty()) {
            et_email.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            et_password.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void initDialog() {
        Button btn_reset, btn_cancel;
        TextView tvMessage;
        EditText et_email;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.reset_password_dialog, null);
        et_email = view.findViewById(R.id.et_email);
        btn_reset = view.findViewById(R.id.btn_reset);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        tvMessage = view.findViewById(R.id.tvResetMessage);

        btn_reset.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             FirebaseAuth.getInstance().sendPasswordResetEmail(et_email.getText().toString())
                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             if (task.isSuccessful()) {
                                                                 Toast.makeText(LoginActivity.this, R.string.rest_email_sent, Toast.LENGTH_SHORT).show();
                                                                 btn_reset.setVisibility(View.GONE);
                                                                 et_email.setVisibility(View.GONE);
                                                                 tvMessage.setText(R.string.sent_email);
                                                             }
                                                         }
                                                     }).addOnFailureListener(new OnFailureListener() {
                                                         @Override
                                                         public void onFailure(@NonNull Exception e) {
                                                             Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                         }
                                                     });
                                         }
                                     }
        );

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }


}
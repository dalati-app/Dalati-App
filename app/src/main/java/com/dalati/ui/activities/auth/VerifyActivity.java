package com.dalati.ui.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dalati.MainActivity;
import com.dalati.R;
import com.dalati.R.string;
import com.dalati.ui.base.BaseActivity;
import com.dalati.ui.models.User;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

public class VerifyActivity extends BaseActivity {

    EditText inputCode1, inputCode2, inputCode3, inputCode4;
    Button sign;
    private Button bt_verify;
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    SpinKitView progressBar;
    String myCode, verificationId;
    String name, password, email, phone, token = "test", id, age, gender;

    @Override
    public int defineLayout() {
        return R.layout.activity_verify;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        defineViews();
        fetchAccountData();
        listeners();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        //      Toast.makeText(Sign_up.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        bt_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myCode = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                bt_verify.setVisibility(View.INVISIBLE);

                Toast.makeText(VerifyActivity.this, verificationId+"\n"+myCode, Toast.LENGTH_SHORT).show();
                if (myCode.equals(verificationId)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(VerifyActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    //checking if success
                                    if (task.isSuccessful()) {
                                        id = mAuth.getCurrentUser().getUid();
                                        User user = new User(id, name, email, phone, "End Users", token, gender, age);

                                        System.out.println("I Signed");

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.child("Tokens").child("End Users").child(id).setValue(token);
                                        mdatabase.child("End Users").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override

                                            public void onComplete(@NonNull Task<Void> task) {
                                                mdatabase.child(id).child("token").setValue(token);
                                                saveObjectToSharedPreference(user);
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();


                                                //  startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "onFailure: Email not sent " + e.getMessage().toString(), Toast.LENGTH_LONG).show();

                                            }
                                        });


                                    } else {
                                        //display some message here
                                        Toast.makeText(getApplicationContext(), R.string.RegError, Toast.LENGTH_LONG).show();
                                    }

                                    progressBar.setVisibility(View.INVISIBLE);
                                    bt_verify.setVisibility(View.VISIBLE);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), string.RegError + e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    bt_verify.setVisibility(View.VISIBLE);
                    Toast.makeText(VerifyActivity.this, string.invalid_Code, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void fetchAccountData() {
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        verificationId = getIntent().getStringExtra("verificationId");
        age = getIntent().getStringExtra("age");
        gender = getIntent().getStringExtra("gender");
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String TAG = "OtpVerificationActivity";
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            Log.d(TAG, "signInWithCredential:success");

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG);

                            progressBar.setVisibility(View.INVISIBLE);
                            bt_verify.setVisibility(View.VISIBLE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                progressBar.setVisibility(View.INVISIBLE);
                                bt_verify.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), string.invalid_Code, Toast.LENGTH_LONG);


                            }
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


    public void defineViews() {
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);

        bt_verify = findViewById(R.id.btnVerify);
        progressBar = findViewById(R.id.progressBar);


        mdatabase = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();


    }


    public void listeners() {

        inputCode1.requestFocus();

        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                } else {
                    Toast.makeText(getApplicationContext(), string.enter_phone , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }
}

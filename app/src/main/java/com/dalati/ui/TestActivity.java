package com.dalati.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dalati.R;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Type;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {
    List<Type> typeList;
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        String phone = "+966509312422";

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                          @Override
                                          public void onVerificationCompleted(PhoneAuthCredential credential) {


                                              // This callback will be invoked in two situations:
                                              // 1 - Instant verification. In some cases the phone number can be instantly
                                              //     verified without needing to send or enter a verification code.
                                              // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                              //     detect the incoming verification SMS and perform verification without
                                              //     user action.
                                              Log.d(TAG, "onVerificationCompleted:" + credential);

                                          }

                                          @Override
                                          public void onVerificationFailed(FirebaseException e) {
                                              // This callback is invoked in an invalid request for verification is made,
                                              // for instance if the the phone number format is not valid.
                                              Log.w(TAG, "onVerificationFailed", e);
                                                                                   if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                  // Invalid request
                                              } else if (e instanceof FirebaseTooManyRequestsException) {
                                                  // The SMS quota for the project has been exceeded
                                              }

                                              // Show a message and update the UI
                                          }

                                          @Override
                                          public void onCodeSent(@NonNull String verificationId,
                                                                 @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                              // The SMS verification code has been sent to the provided phone number, we
                                              // now need to ask the user to enter the code and then construct a credential
                                              // by combining the code with a verification ID.
                                              Log.d(TAG, "onCodeSent:" + verificationId);
                                                // Save verification ID and resending token so we can use them later

                                          }
                                      }
                        )
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }
}
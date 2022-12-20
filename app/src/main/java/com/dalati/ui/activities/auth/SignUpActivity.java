package com.dalati.ui.activities.auth;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dalati.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    TextView tvSignIn;
    Button btnSignUp;
    FirebaseAuth auth;
    String name, password, email, phone, token = "test", id;
    EditText etName, etPassword, etPhone, etEmail, etAge;
    SpinKitView progressbar;
    ArrayList<String> drop_genderList = new ArrayList<>();
    AutoCompleteTextView drop_menu_gender;
    ArrayAdapter<String> adapter_gender;
    CountryCodePicker ccp;
    String gender;
    int age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        defineViews();
    }

    public void defineViews() {
        tvSignIn = findViewById(R.id.tv_signIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAge = findViewById(R.id.et_age);
        progressbar = findViewById(R.id.progressBar);
        drop_menu_gender = findViewById(R.id.dropdown_gender);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        auth = FirebaseAuth.getInstance();

        drop_genderList.add("Male");
        drop_genderList.add("Female");
        adapter_gender = new ArrayAdapter<String>(getApplicationContext(), R.layout.filter_item, drop_genderList);
        drop_menu_gender.setAdapter(adapter_gender);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

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
    }


    public void registerUser() {
        name = etName.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        String tempAge = etAge.getText().toString();
        age = Integer.parseInt(tempAge);
        phone = ccp.getSelectedCountryCodeWithPlus() + phone;


        drop_menu_gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gender = (String) adapterView.getItemAtPosition(i);
            }
        });
        Boolean flag = false;


        if (name.isEmpty()) {
            etName.setError("Name required");
            etName.requestFocus();
            flag = true;


        }

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            flag = true;


        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Need Email");
            etEmail.requestFocus();
            flag = true;

        }

        if (password.isEmpty()) {
            etPassword.setError("Password Required");
            etPassword.requestFocus();
            flag = true;

        }

        if (phone.length() < 9) {
            etPhone.setError("Phone Required");
            etPhone.requestFocus();
            flag = true;

        }


        if (!isValidPassword(password)) {
            etPassword.setError("Your password must contain:\n lower and upper cases with numbers ");
            flag = true;
        }


        if (!flag) {
            progressbar.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.INVISIBLE);

            sendOTP("+967" + phone);
            //creating a new user

        }
    }

    public static boolean isValidPassword(String password) {
        Matcher matcher = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{4,20})").matcher(password);
        return matcher.matches();
    }


    public void sendOTP(String phone) {

        progressbar.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);

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
                                              progressbar.setVisibility(View.INVISIBLE);
                                              btnSignUp.setVisibility(View.VISIBLE);

                                              progressbar.setVisibility(View.INVISIBLE);
                                              btnSignUp.setVisibility(View.VISIBLE);
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
                                              progressbar.setVisibility(View.INVISIBLE);
                                              btnSignUp.setVisibility(View.VISIBLE);
                                              Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
                                              intent.putExtra("phone", phone);
                                              intent.putExtra("name", name);
                                              intent.putExtra("email", email);
                                              intent.putExtra("password", password);
                                              intent.putExtra("age", age);
                                              intent.putExtra("gender", gender);
                                              intent.putExtra("verificationId", verificationId);
                                              Toast.makeText(SignUpActivity.this, "OTP Code Sent", Toast.LENGTH_SHORT).show();
                                              startActivity(intent);
                                              // Save verification ID and resending token so we can use them later

                                          }
                                      }
                        )
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

}
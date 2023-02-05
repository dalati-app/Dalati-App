package com.dalati.ui.activities.auth;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.dalati.R;
import com.dalati.ui.base.BaseActivity;
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

public class SignUpActivity extends BaseActivity {

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
    String gender, otpCode;
    int age;
    int responseCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineViews();
    }

    @Override
    public int defineLayout() {
        return R.layout.activity_sign_up;
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
        drop_genderList.add(getString(R.string.male));
        drop_genderList.add(getString(R.string.female));
        drop_genderList.add(getString(R.string.prefer));
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
        gender = drop_menu_gender.getText().toString();
        phone = ccp.getSelectedCountryCodeWithPlus() + phone;


        drop_menu_gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gender = (String) adapterView.getItemAtPosition(i);
            }
        });

        Boolean flag = false;


        if (name.isEmpty()) {
            etName.setError(getString(R.string.name_required));
            etName.requestFocus();
            flag = true;


        }

        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.email_is_required));
            etEmail.requestFocus();
            flag = true;


        }


        //to validate the email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.need_email));
            etEmail.requestFocus();
            flag = true;

        }


        if (tempAge.length() == 0) {
            etAge.setError(getString(R.string.age_error));
            etAge.requestFocus();
            flag = true;
        } else {
            age = Integer.parseInt(tempAge);

        }

        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.pass_));
            etPassword.requestFocus();
            flag = true;
        }
        if (phone.length() < 9) {
            etPhone.setError(getString(R.string.phone_required));
            etPhone.requestFocus();
            flag = true;
        }
        if (!isValidPassword(password)) {
            etPassword.setError(getString(R.string.pass1));
            flag = true;
        }

        if (gender.isEmpty()) {
            drop_menu_gender.setError("Gender is Required");
            drop_menu_gender.requestFocus();
            flag = true;
        }
        if (!flag) {
            progressbar.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.INVISIBLE);
            sendOTP();


            //creating a new user

        }
    }

    public static boolean isValidPassword(String password) {
        Matcher matcher = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{4,20})").matcher(password);
        return matcher.matches();
    }


    public void sendOTP() {

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
                                              Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                                              intent.putExtra("verificationId", verificationId);
                                              intent.putExtra("age", SignUpActivity.this.age);
                                              intent.putExtra("gender", gender);
                                              Toast.makeText(SignUpActivity.this, R.string.otp, Toast.LENGTH_SHORT).show();
                                              startActivity(intent);
                                              // Save verification ID and resending token so we can use them later

                                          }
                                      }
                        )
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }


}
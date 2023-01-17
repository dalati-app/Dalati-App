package com.dalati.ui.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
                String otpCode = String.valueOf(generateOTP());
                Toast.makeText(SignUpActivity.this, otpCode, Toast.LENGTH_SHORT).show();
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
        gender=drop_menu_gender.getText().toString();
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


        if (tempAge.length()==0) {
            etAge.setError(getString(R.string.age_error));
            etAge.requestFocus();
            flag = true;
        }
        else {
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

        if (gender.isEmpty())
        {
            drop_menu_gender.setError("Gender is Required");
            drop_menu_gender.requestFocus();
            flag=true;
        }
        if (!flag) {
            progressbar.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.INVISIBLE);
            sendOTP();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (responseCode == 200) {
                        Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("age", SignUpActivity.this.age);
                        intent.putExtra("gender", gender);
                        intent.putExtra("verificationId", otpCode);
                        Toast.makeText(SignUpActivity.this, R.string.otp, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else {
                        progressbar.setVisibility(View.INVISIBLE);
                        btnSignUp.setVisibility(View.VISIBLE);
                        Toast.makeText(SignUpActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                }
            }, 4000);

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

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    // Call the Api to send OTP CODE
                    otpCode = String.valueOf(generateOTP());
                    String content = "{\r\n    \"message\": \"مرحبا بك في تطبيق ضالتي  \\nرمز تحققك هو: " + otpCode + " \",\r\n    \"to\": \"" + phone + "\",\r\n    \"bypass_optout\": true,\r\n    \"sender_id\": \"SMSto\",\r\n    \"callback_url\": \"https://example.com/callback/handler\"\r\n}";
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");

                    RequestBody body = RequestBody.create(mediaType, content);
                    Request request = new Request.Builder()
                            .url("https://api.sms.to/sms/send")
                            .method("POST", body)
                            .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2F1dGg6ODA4MC9hcGkvdjEvdXNlcnMvYXBpL2tleS9nZW5lcmF0ZSIsImlhdCI6MTY3MTgzMzE3MywibmJmIjoxNjcxODMzMTczLCJqdGkiOiJXMWpTckplSDZWRkR0Y2FlIiwic3ViIjo0MDY0MTIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.EepuGxhwptHWNw_SShmoIu144Oo12b2OFXNLjXwnUZ8")
                            .addHeader("Content-Type", "application/json")
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        responseCode = response.code();


                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });

        thread.start();


    }

    private char[] generateOTP() {
        String numbers = "0123456789";

        // Using random method
        Random rndm_method = new Random();

        char[] otp = new char[4];

        for (int i = 0; i < 4; i++) {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] =
                    numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return otp;
    }

}